package com.maxhit.calculators;

import com.google.common.collect.ImmutableSet;
import com.maxhit.MaxHitPlugin;
import com.maxhit.NextMaxHitReqs;
import com.maxhit.equipment.EquipmentFunctions;
import com.maxhit.equipment.GolembaneWeapon;
import com.maxhit.monsters.DraconicMonsters;
import com.maxhit.monsters.GolemMonsters;
import com.maxhit.sets.BerserkerNecklaceSet;
import com.maxhit.sets.InquisitorSet;
import com.maxhit.styles.AttackStyle;
import java.util.Collection;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import com.maxhit.sets.DharokSet;
import com.maxhit.sets.ObsidianSet;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemVariationMapping;

@Slf4j
public class MeleeMaxHitCalculator extends MaxHitCalculator
{
	private final Collection<Integer> FANGS = ItemVariationMapping.getVariations(ItemID.OSMUMTENS_FANG);
	private final Set<Integer> DINHS = ImmutableSet.of(ItemID.DINHS_BULWARK_ORNAMENT, ItemID.DINHS_BULWARK);
	private final DharokSet dharokSetChecker;
	private final ObsidianSet obsidianSetChecker;
	private final BerserkerNecklaceSet berserkerNecklaceSet;
	private double baseDamage;
	private double golemBonus;
	private double obsidianBonus;
	private double inquisitorBonus;
	private double dharokBonus;
	private double berserkerBonus;
	private double soulReaperAxeBonus;

	protected MeleeMaxHitCalculator(MaxHitPlugin plugin, Client client, ItemManager itemManager, AttackStyle attackStyle)
	{
		super(plugin, client, itemManager, Skill.STRENGTH, attackStyle);
		dharokSetChecker = new DharokSet(client);
		obsidianSetChecker = new ObsidianSet();
		berserkerNecklaceSet = new BerserkerNecklaceSet();
		reset();
	}

	@Override
	protected void reset()
	{
		soulReaperAxeBonus = 0.0;
		updateSoulReaperAxeBonus();
		super.reset();
		baseDamage = 0.0;
		obsidianBonus = 0.0;
		inquisitorBonus =  0.0;
		golemBonus = 1.0;
		dharokBonus = 1.0;
		berserkerBonus = 0.0;
	}

	private void getBonuses()
	{
		getAvariceBonus();
		getSalveBonus();
		getSlayerBonus();
		getObsidianBonus();
		getDemonbaneBonus();
		getDragonbaneBonus();
		getInquisitorBonus();
		getGolemBonus();
		getDharokBonus();
		getBerserkerBonus();
		updateFlatArmour();
	}

	@Override
	protected void updateStyleBonus()
	{
		if (attackStyle == AttackStyle.AGGRESSIVE) {styleBonus = 3.0;}
		if (attackStyle == AttackStyle.CONTROLLED) {styleBonus = 1.0;}
	}

	private void updateSoulReaperAxeBonus()
	{
		if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, ItemID.SOULREAPER) ||
			EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, ItemID.SOULREAPER_AXE_ORN))
		{
			int stacks = client.getVarpValue(VarPlayerID.SOULREAPER_STACKS);

			soulReaperAxeBonus = stacks * 0.06;
		}
	}

	@Override
	protected void updateEffectiveLevel()
	{
		super.updateEffectiveLevel();
		// Stacks additively
		effectiveLevel += Math.floor(effectiveLevel * soulReaperAxeBonus);
	}

	@Override
	protected void updateStrengthBonus()
	{
		super.updateStrengthBonus();
		for (int itemId : DINHS)
		{
			if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, itemId))
			{
				strengthBonus += StrengthBonusCalculator.getDinhsBonus(attackStyle, equippedItems, itemManager);
				break;
			}
		}
	}

	private void getBaseDamage()
	{
		double bonusMultiplier = (strengthBonus + 64.0) / 640.0;
		baseDamage = Math.floor(0.5 + (effectiveStrength * bonusMultiplier));
	}

	private double applyBonuses(double baseDamage)
	{
		double damage = baseDamage;
		// Slayer helm and salve don't stack
		if (avariceBonus > 1.0)
		{
			damage = factor(damage, avariceBonus);
		}
		else if (salveBonus > 1.0)
		{
			damage = factor(damage, salveBonus);
		}
		else if (slayerBonus > 1.0)
		{
			damage = factor(damage, slayerBonus);
		}

		damage = addFactor(damage, demonbaneBonus);

		damage = addFactor(damage, obsidianBonus);

		damage = factor(damage, dragonbaneBonus);

		// TODO add support for Keris/Keris Partisan vs Kalphites
		damage = factor(damage, golemBonus);

		// TODO add support for Rev Weapon
		// TODO add support for Leaf bladed weapons
		// TODO add support for Colossal Blade
		if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, ItemID.RAT_BONE_MACE))
		{
			damage = damage + 10.0;
		}

		damage = factor(damage, inquisitorBonus);

		// The Fang calculation is a bit different. Confirmed in-game
		for(int itemId : FANGS)
		{
			if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, itemId))
			{
				double fangShrink = Math.floor(damage * 3.0 / 20.0);
				damage -= fangShrink;
				break;
			}
		}

		damage = factor(damage, dharokBonus);

		damage = addFactor(damage, berserkerBonus);

		return damage;
	}

	@Override
	public void calculateMaxHit()
	{
		reset();
		getBonuses();
		updateEffectiveStrength();
		updateStrengthBonus();
		getBaseDamage();
		maxHit = applyBonuses(baseDamage);
		maxHit -= flatArmour;
		calculateNextMaxHitRequirements();
	}

	@Override
	protected void calculateNextMaxHitRequirements()
	{
		final double nextMaxHit = maxHit + 1.0;
		double tempMaxHit = 0.0;
		double levelDiff = 0.0;

		// Add guard
		if (effectiveLevel < 1.0)
		{
			return;
		}
		// Get Skill level difference
		do
		{
			levelDiff++;
			updateEffectiveLevel();
			effectiveLevel += levelDiff;
			updateEffectiveStrength();
			updateStrengthBonus();
			getBaseDamage();
			tempMaxHit = applyBonuses(baseDamage);
			tempMaxHit -= flatArmour;
		} while (tempMaxHit < nextMaxHit);

		// Get Strength bonus difference
		tempMaxHit = 0.0;
		double strengthBonusDiff = 0.0;
		do
		{
			strengthBonusDiff++;
			updateEffectiveLevel();
			updateEffectiveStrength();
			updateStrengthBonus();
			strengthBonus += strengthBonusDiff;
			getBaseDamage();
			tempMaxHit = applyBonuses(baseDamage);
			tempMaxHit -= flatArmour;
		} while (tempMaxHit < nextMaxHit);

		// Get prayer bonus difference
		tempMaxHit = 0.0;
		double prayerDiff = 0.0;
		do
		{
			updatePrayerBonus();
			prayerDiff += 0.01;
			prayerBonus += prayerDiff;
			updateEffectiveLevel();
			updateEffectiveStrength();
			updateStrengthBonus();
			getBaseDamage();
			tempMaxHit = applyBonuses(baseDamage);
			tempMaxHit -= flatArmour;
		} while (tempMaxHit < nextMaxHit);

		nextMaxHitReqs = new NextMaxHitReqs(skill, levelDiff, strengthBonusDiff, prayerDiff * 100.0);
	}

	public void getGolemBonus()
	{
		// Check if opponent is a golem
		int npcId = getNpcId();

		if (npcId == -1)
		{
			return;
		}

		if (!GolemMonsters.ID_LIST.contains(npcId))
		{
			return;
		}

		for (GolembaneWeapon weapon : GolembaneWeapon.values())
		{
			if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, weapon.getItemId()))
			{
				golemBonus = weapon.getDamageMultiplier();
				return;
			}
		}
	}

	public void getDragonbaneBonus()
	{
		// Check if opponent is draconic
		int npcId = getNpcId();

		if (npcId == -1)
			return;

		if (!DraconicMonsters.ID_LIST.contains(npcId))
		{
			return;
		}

		// Wand is funny to me, but it does count
		if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, ItemID.DRAGONHUNTER_LANCE) ||
			EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, ItemID.DRAGONHUNTER_WAND))
		{
			dragonbaneBonus = 1.2;
		}

	}

	private void getInquisitorBonus()
	{
		inquisitorBonus = InquisitorSet.getMultiplier(client, equippedItems);
	}

	private void getObsidianBonus()
	{
		if (obsidianSetChecker.isWearingSet())
		{
			obsidianBonus = obsidianSetChecker.getMultiplier();
		}
	}

	private void getDharokBonus()
	{
		if (dharokSetChecker.isWearingSet())
		{
			dharokBonus = dharokSetChecker.getMultiplier();
		}
	}

	private void getBerserkerBonus()
	{
		if (berserkerNecklaceSet.isWearingSet())
		{
			berserkerBonus = berserkerNecklaceSet.getMultiplier();
		}
	}
}
