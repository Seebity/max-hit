package com.maxhit.calculators;

import com.maxhit.MaxHitPlugin;
import com.maxhit.NextMaxHitReqs;
import com.maxhit.equipment.EquipmentFunctions;
import com.maxhit.monsters.DraconicMonsters;
import com.maxhit.styles.AttackStyle;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.game.ItemManager;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.game.ItemVariationMapping;

@Slf4j
public class RangedMaxHitCalculator extends MaxHitCalculator
{
	private double baseDamage;
	private double crystalBonus;
	private double specialBonus;

	protected RangedMaxHitCalculator(MaxHitPlugin plugin, Client client, ItemManager itemManager, AttackStyle attackStyle)
	{
		super(plugin, client, itemManager, Skill.RANGED, attackStyle);
		reset();
	}

	@Override
	protected void reset()
	{
		super.reset();
		crystalBonus = 1.0;

		if (equippedItems != null)
		{
			Item weapon = this.equippedItems.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
			if (weapon != null)
			{
				if (weapon.getId() == ItemID.ECLIPSE_ATLATL)
				{
					this.skill = Skill.STRENGTH;
				}
			}
		}

		baseDamage = 0.0;
		specialBonus = 1.0;
	}

	@Override
	protected void updateStyleBonus()
	{
		// Have to use different method since ATTACK style is just equal to RANGED
		if (client.getVarpValue(VarPlayerID.COM_MODE) == AttackStyle.ACCURATE.ordinal())
		{
			styleBonus = 3.0;
		}
	}

	private void getBaseDamage()
	{
		double strengthCalculation = (effectiveStrength * (strengthBonus + 64)) / 640.0;
		baseDamage = Math.floor(0.5 + strengthCalculation);
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

		if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, ItemID.DRAGONHUNTER_XBOW))
		{
			dragonbaneBonus = 1.25;
		}
	}

	private double applyBonuses(double baseDamage)
	{
		double damage = baseDamage;
		if (equippedItems == null)
		{
			return damage;
		}

		Item weaponItem = equippedItems.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());

		if (weaponItem == null)
		{
			return damage;
		}

		// Bowfa and Crystal Bow
		if (ItemVariationMapping.getVariations(ItemID.BOW_OF_FAERDHINEN_INACTIVE).contains(weaponItem.getId()) ||
			ItemVariationMapping.getVariations(ItemID.ROVING_CRYSTAL_BOW_NEW).contains(weaponItem.getId()))
		{
			Item headSlotItem = equippedItems.getItem(EquipmentInventorySlot.HEAD.getSlotIdx());
			if (headSlotItem != null &&
				ItemVariationMapping.getVariations(
					ItemVariationMapping.map(ItemID.CRYSTAL_HELMET)).contains(headSlotItem.getId()))
			{
				crystalBonus += 0.025;
			}

			Item bodySlotItem = equippedItems.getItem(EquipmentInventorySlot.BODY.getSlotIdx());
			if (bodySlotItem != null &&
				ItemVariationMapping.getVariations(
					ItemVariationMapping.map(ItemID.CRYSTAL_CHESTPLATE)).contains(bodySlotItem.getId()))
			{
				crystalBonus += 0.075;
			}

			Item legsSlotItem = equippedItems.getItem(EquipmentInventorySlot.LEGS.getSlotIdx());
			if (legsSlotItem != null &&
				ItemVariationMapping.getVariations(
					ItemVariationMapping.map(ItemID.CRYSTAL_PLATELEGS)).contains(legsSlotItem.getId()))
			{
				crystalBonus += 0.05;
			}
		}

		// Crystal bonus is applied before Amulet of Avarice/Salve/Slayer Helm(i)
		damage = factor(damage, crystalBonus);

		// Avarice, Salve, and Slayer helm don't stack
		getAvariceBonus();
		getSalveBonus();
		getSlayerBonus();
		getDragonbaneBonus();
		getDemonbaneBonus();
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
			// Craw's bow, Dragonbane, and Demonbane are additive
			double numerator = 23;
			if (dragonbaneBonus > 1.0)
			{
				numerator += 5;
			}
			if (demonbaneBonus > 0.0)
			{
				numerator += 6;
			}

			damage = factor(damage, numerator / 20.0);
		}

		// If not wearing slayer helm and not on task
		if (slayerBonus == 1.0)
		{
			damage = factor(damage, dragonbaneBonus);

			if (demonbaneBonus > 0.0)
			{
				damage = addFactor(damage, demonbaneBonus);
			}
		}

		if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, ItemID.RAT_BONE_BOW))
		{
			damage = damage + 10.0;
		}

		//TODO Add support for Craw's bow and Twisted bow
		return damage;
	}

	@Override
	public void calculateMaxHit()
	{
		reset();
		updateEffectiveStrength();
		updateStrengthBonus();
		getBaseDamage();
		maxHit = baseDamage;
		maxHit = applyBonuses(baseDamage);

		//TODO add support for special attack
		updateFlatArmour();
		maxHit -= flatArmour;

		calculateNextMaxHitRequirements();
	}

	@Override
	protected void calculateNextMaxHitRequirements()
	{
		final double nextMaxHit = maxHit + 1.0;
		double tempMaxHit;
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
		double prayerDiff = 0.0;
		do
		{
			prayerDiff += 0.01;
			updatePrayerBonus();
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
}
