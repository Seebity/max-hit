package com.maxhit.calculators;

import com.maxhit.MaxHitPlugin;
import com.maxhit.MagicSpell;
import com.maxhit.Spellbook;
import com.maxhit.equipment.EquipmentFunctions;
import com.maxhit.equipment.GodCape;
import com.maxhit.equipment.PoweredStaff;
import com.maxhit.equipment.VirtusPieces;
import com.maxhit.monsters.MonsterWeaknesses;
import static com.maxhit.regions.TombsRegions.TOA_ROOM_IDS;
import com.maxhit.styles.AttackStyle;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.game.ItemManager;
import lombok.extern.slf4j.Slf4j;
import java.util.Set;

@Slf4j
public class MagicMaxHitCalculator extends MaxHitCalculator
{
	private static final Set<MagicSpell> GOD_SPELLS = ImmutableSet.of(
		MagicSpell.FLAMES_OF_ZAMORAK,
		MagicSpell.SARADOMIN_STRIKE,
		MagicSpell.CLAWS_OF_GUTHIX);

	private static final Set<MagicSpell> BOLT_SPELLS = ImmutableSet.of(
		MagicSpell.WIND_BOLT,
		MagicSpell.WATER_BOLT,
		MagicSpell.EARTH_BOLT,
		MagicSpell.FIRE_BOLT);
	
	private static final Set<Integer> STANDARD_SPELLBOOK_STAVES = ImmutableSet.of(
		ItemID.SMOKE_BATTLESTAFF,
		ItemID.MYSTIC_SMOKE_BATTLESTAFF,
		ItemID.TWINFLAME_STAFF
	);

	private static final Set<String> TWINFLAME_SPELLS = ImmutableSet.of(
		"Bolt",
		"Blast",
		"Wave"
	);

	private static final Set<Integer> TUMEKEN_SHADOWS = ImmutableSet.of(
		ItemID.TUMEKENS_SHADOW,
		ItemID.TUMEKENS_SHADOW_UNCHARGED
	);

	MagicSpell activeSpell = null;
	private double baseSpellDamage;
	private double baseDamageModifier;
	private double primaryMagicDamage;
	private double preHitRoll;
	private double shadowBonus;
	private double salveBonus;
	private double avariceBonus;
	private double standardSpellbookStaffBonus;
	private double virtusBonus;
	private double prayerBonus;
	private double elementalWeakness;
	private double slayerBonus;
	private double sceptreBonus;
	private double accursedSceptreSpecialAttackBonus;
	private double tomeBonus;
	private double markOfDarknessBonus;
	private double ahrimsDamnedBonus;
	//Ignore castle wars bracelet for now

	protected MagicMaxHitCalculator(MaxHitPlugin plugin, Client client, ItemManager itemManager, AttackStyle attackStyle)
	{
		super(plugin, client, itemManager, Skill.MAGIC, attackStyle);
		reset();
	}

	protected void reset()
	{
		voidBonus = 0.0;
		baseSpellDamage = 0.0;
		baseDamageModifier = 0.0;
		primaryMagicDamage = 0.0;
		shadowBonus = 1.0;
		salveBonus = 0.0;
		avariceBonus = 0.0;
		standardSpellbookStaffBonus = 0.0;
		virtusBonus = 0.0;
		prayerBonus = 0.0;
		elementalWeakness = 0.0;
		slayerBonus = 0.0;
		sceptreBonus = 0.0;
		accursedSceptreSpecialAttackBonus = 0.0;
		tomeBonus = 0.0;
		markOfDarknessBonus = 0.0;
		ahrimsDamnedBonus = 0.0;
	}

	@Override
	protected void getEffectiveStrength()
	{
	}

	@Override
	protected void getStyleBonus()
	{
	}

	private boolean chaosGauntletsEquipped()
	{
		return EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.GLOVES, ItemID.GAUNTLETS_OF_CHAOS);
	}

	private boolean matchingGodCapeEquipped()
	{
		for (GodCape cape : GodCape.values())
		{
			if (cape.isEquipped(equippedItems) && activeSpell.getVarbValue() == cape.getSpellId())
			{
				return true;
			}
		}
		return false;
	}

	private void getMagicDartBaseMaxDamage()
	{
		int magicLevel = client.getBoostedSkillLevel(Skill.MAGIC);
		baseSpellDamage = Math.floor(magicLevel * 0.1) + 10;
	}

	private void getSpellBaseMaxDamage()
	{

		// Powered Staves
		for (PoweredStaff staff : PoweredStaff.values())
		{
			for (int itemId : staff.getVarients())
			{
				if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, itemId))
				{
					baseSpellDamage = staff.getBaseMaxHit(getSkillLevel());
					return;
				}
			}
		}


		if (this.plugin.getActiveSpell() != null)
		{
			activeSpell = this.plugin.getActiveSpell();
		}
		else
		{
			int activeSpellVarbit = client.getVarbitValue(VarbitID.AUTOCAST_SPELL);

			// Spellbook Spells
			for (MagicSpell spell : MagicSpell.values())
			{
				if (activeSpellVarbit != spell.getVarbValue())
				{
					continue;
				}

				activeSpell = spell;
				break;
			}
		}

		// Magic Dart
		if (activeSpell == MagicSpell.MAGIC_DART)
		{
			getMagicDartBaseMaxDamage();
			return;
		}

		baseSpellDamage = activeSpell.getBaseMaxHit(client);
	}

	private void getBaseDamageModifier()
	{
		getSpellBaseMaxDamage();

		baseDamageModifier = baseSpellDamage;

		// Check for Chaos Gauntlets with bolt spells
		if (BOLT_SPELLS.contains(activeSpell))
		{
			if (chaosGauntletsEquipped())
			{
				baseDamageModifier += 3.0;
				return;
			}
		}

		// Check if a god spell is active
		if (!GOD_SPELLS.contains(activeSpell))
		{
			return;
		}
		// Check if a matching cape is equipped
		if (!matchingGodCapeEquipped())
		{
			return;
		}
		// Check if charge is active
		if (client.getVarbitValue(VarPlayerID.MAGEARENA_CHARGE) == 0)
		{
			return;
		}
		baseDamageModifier += 10.0;
	}

	private void getShadowBonus()
	{
		boolean shadowEquipped = false;
		// Check if Shadow equipped
		for (int staffId : TUMEKEN_SHADOWS)
		{
			if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, staffId))
			{
				shadowEquipped = true;
			}
		}

		if (!shadowEquipped)
		{
			return;
		}

		int region = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID();
		boolean toaInside = TOA_ROOM_IDS.contains(region);
		if (toaInside)
		{
			shadowBonus = 4.0;
		}
		else
		{
			shadowBonus = 3.0;
		}
	}

	private void getAvariceBonus()
	{
		if (!EquipmentFunctions.GetEquippedItemString(client, equippedItems, EquipmentInventorySlot.AMULET).contains(("Avarice")))
		{
			return;
		}
		NPC npc = (NPC) opponent;
		if (npc == null)
		{
			return;
		}
		String npcName = npc.getName();
		if (npcName == null)
		{
			return;
		}
		if (!npc.getName().contains("Revenant"))
		{
			return;
		}
		if (client.getVarbitValue(VarPlayerID.REVENANT_REWARD_TIME_REMAINING) > 0)
		{
			avariceBonus = 0.35;
			return;
		}
		avariceBonus = 0.2;
	}

	private void getSmokeBattlestaffBonus()
	{
		if (!Spellbook.STANDARD.isActive(client))
		{
			return;
		}

		for (int battlestaffId : STANDARD_SPELLBOOK_STAVES)
		{
			if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, battlestaffId))
			{
				standardSpellbookStaffBonus = 0.1;
				return;
			}
		}
	}

	private void getVirtusBonus()
	{
		if (!Spellbook.ANCIENT.isActive(client))
		{
			virtusBonus = 0.0;
			return;
		}
		// Count how many pieces are equipped and multiply by the bonus per piece.
		long piecesEquipped = Arrays.stream(VirtusPieces.values())
			.filter(piece -> piece.isEquipped(equippedItems))
			.count();
		virtusBonus = piecesEquipped * 0.02;
	}

	private void getElementalWeakness()
	{
		for (MonsterWeaknesses monster : MonsterWeaknesses.values())
		{
			if (monster.hasWeakness(opponent, activeSpell))
			{
				elementalWeakness += monster.getSeverity();
				return;
			}

		}
	}


	private void getPrimaryMagicDamage()
	{
		getStrengthBonus(); // I.E. visible bonuses
		getShadowBonus();
		getVoidBonus();
		getSalveBonus();
		getAvariceBonus();
		getSmokeBattlestaffBonus();
		getVirtusBonus();
		getPrayerBonus();
		getElementalWeakness();
		double tempBonuses = (strengthBonus - voidBonus) * shadowBonus;
		tempBonuses = Math.min(1.0, tempBonuses);
		double totalBonus = voidBonus + salveBonus + avariceBonus + standardSpellbookStaffBonus + virtusBonus + prayerBonus;
		double elementalWeaknessAddition = Math.floor(baseDamageModifier * elementalWeakness);
		primaryMagicDamage = Math.floor(baseDamageModifier * (1 + tempBonuses + totalBonus) + elementalWeaknessAddition);
	}

	private void getPreHitRoll()
	{

		//TODO: Add slayer, sceptre, and tome bonus
		double totalSlayerBonus = 1.0 + slayerBonus;
		double totalSceptreBonus = 1.0 + sceptreBonus;
		double totalAccursedBonus = 1.0 + accursedSceptreSpecialAttackBonus;
		double totalTomeBonus = 1.0 + tomeBonus;

		double firstFloorCalculation = Math.floor(primaryMagicDamage * totalSlayerBonus);
		double secondFloorCalculation = Math.floor(firstFloorCalculation * totalSceptreBonus);
		double thirdfFloorCalculation = Math.floor(secondFloorCalculation * totalAccursedBonus);
		preHitRoll = Math.floor(thirdfFloorCalculation * totalTomeBonus);
	}

	@Override
	public void calculateMaxHit()
	{
		reset();
		// Base Damage Modifier
		getBaseDamageModifier();
		// Primary Magic Damage
		getPrimaryMagicDamage();
		// Pre Hit Roll == Hit Roll
		// Don't need to use the hit roll step on the wiki since we're just calculating max hit
		getPreHitRoll();
		double firstFloorCalculation = Math.floor(preHitRoll * (1 + markOfDarknessBonus));
		// Skip castle wars bonus
		// Final Post Hit Roll
		maxHit = Math.floor(firstFloorCalculation * (1 + ahrimsDamnedBonus));

		applyTwinflameModifier();
	}

	private void applyTwinflameModifier()
	{
		// Twinflame Staff
		if (!EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, ItemID.TWINFLAME_STAFF))
		{
			return;
		}

		if (activeSpell.getSpellbook() != Spellbook.STANDARD)
		{
			return;
		}

		for (String spell : TWINFLAME_SPELLS)
		{
			if (activeSpell.getDisplayName().contains(spell))
			{
				double secondHitDamage = Math.floor(maxHit * 0.4);
				maxHit = maxHit + secondHitDamage;
				return;
			}
		}
	}

	@Override
	public void calculateNextMaxHitRequirements()
	{
		//TODO: Implement
	}
}