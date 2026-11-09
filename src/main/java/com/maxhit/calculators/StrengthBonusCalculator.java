package com.maxhit.calculators;

import com.maxhit.equipment.BowAmmoRegistry;
import com.maxhit.equipment.EquipmentFunctions;
import com.maxhit.styles.AttackStyle;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ParamID;
import net.runelite.api.Skill;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.game.ItemEquipmentStats;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStats;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StrengthBonusCalculator
{

	public static double getDinhsBonus(AttackStyle attackStyle, ItemContainer equippedItems, ItemManager itemManager)
	{
		double stabBonus = 0.0;
		double slashBonus = 0.0;
		double crushBonus = 0.0;
		double rangedBonus = 0.0;

		if (equippedItems == null)
		{
			return 0.0;
		}

		// If not using Pummel, attempt to set max hit to 0
		if (attackStyle != AttackStyle.ACCURATE)
		{
			return -1000.0;
		}


		//get str bonus of worn equipment
		for (EquipmentInventorySlot slot : EquipmentInventorySlot.values())
		{
			// Have to convert enum to int i.e. use ordinal
			Item item = equippedItems.getItem(slot.getSlotIdx());
			if (item == null)
			{
				continue;
			}
			int itemId = item.getId();
			final ItemStats itemStats = itemManager.getItemStats(itemId);
			if (itemStats == null)
			{
				continue;
			}
			final ItemEquipmentStats itemEquipmentStats = itemStats.getEquipment();
			stabBonus += itemEquipmentStats.getDstab();
			slashBonus += itemEquipmentStats.getDslash();
			crushBonus += itemEquipmentStats.getDcrush();
			rangedBonus += itemEquipmentStats.getDrange();
		}
		final double totalBonus = stabBonus + slashBonus + crushBonus + rangedBonus;
		return Math.max(0.0, ((totalBonus - 800.0) / 12.0) - 38.0);
	}

	public static double getStrengthBonus(Client client, ItemContainer equippedItems, ItemManager itemManager, Skill skill)
	{
		double strengthBonus = 0.0;

		if (equippedItems == null)
		{
			return strengthBonus;
		}

		// Check for quiver
		final Item cape = equippedItems.getItem(EquipmentInventorySlot.CAPE.getSlotIdx());
		boolean quiverEquipped = cape != null && itemManager.getItemComposition(cape.getId()).getIntValue(ParamID.QUIVER_AMMO_AVAILABLE) == 1;
		// Base skipped ammo off quiver equipped to begin with
		boolean skipAmmo = quiverEquipped;
		boolean isSelfContainedOrLightWeapon = false;

		int bowId = -1;
		int ammoId = -1;

		// Sum all equipment strength
		for (EquipmentInventorySlot slot : EquipmentInventorySlot.values())
		{
			// Check if weapon is a bow
			if (skill == Skill.RANGED && slot == EquipmentInventorySlot.WEAPON)
			{
				// Save off bow ID for checking registry later
				bowId = EquipmentFunctions.GetEquippedItemId(equippedItems, slot);

				// If it's a self-contained/charged bow, don't use ammo strength
				if (BowAmmoRegistry.isSelfContainedBow(bowId) ||
					BowAmmoRegistry.isLightWeapon(bowId))
				{
					isSelfContainedOrLightWeapon = true;
					skipAmmo = true;
				}
			}

			if (slot == EquipmentInventorySlot.AMMO)
			{
				// Save off ammo ID for checking registry later
				ammoId = EquipmentFunctions.GetEquippedItemId(equippedItems, slot);
				if (skipAmmo)
					continue;
			}

			strengthBonus += getItemStrength(skill, equippedItems, itemManager, slot);
		}

		double quiverAmmoStr = 0.0;
		// Quiver equipped and using ranged so get strength of ammo
		if (skill == Skill.RANGED && quiverEquipped)
		{
			quiverAmmoStr = getQuiverAmmoStrength(bowId, ammoId, client, equippedItems, itemManager);
			strengthBonus += quiverAmmoStr;
		}

		// If ammo doesn't match, quiver ammo str is 0, and not using self-contained bow or light weapon, set damage to 0.0
		if (skill == Skill.RANGED)
		{

			if (!BowAmmoRegistry.isAmmoCompatible(bowId, ammoId) &&
				quiverEquipped &&
				quiverAmmoStr == 0.0 &&
				!isSelfContainedOrLightWeapon)
			{
				strengthBonus = -100;
			}
		}

		return strengthBonus;
	}

	public static double getItemStrength(Skill skill, ItemContainer equippedItems, ItemManager itemManager, EquipmentInventorySlot slot)
	{
		int itemId = EquipmentFunctions.GetEquippedItemId(equippedItems, slot);

		if (itemId == -1)
		{
			return 0.0;
		}

		final ItemStats itemStats = itemManager.getItemStats(itemId);

		if (itemStats == null)
		{
			return 0.0;
		}

		final ItemEquipmentStats itemEquipmentStats = itemStats.getEquipment();

		if (skill == Skill.STRENGTH)
			return itemEquipmentStats.getStr();
		if (skill == Skill.RANGED)
			return itemEquipmentStats.getRstr();
		return itemEquipmentStats.getMdmg() / 100.0;
	}

	public static double getQuiverAmmoStrength(int bowId, int ammoId, Client client, ItemContainer equippedItems, ItemManager itemManager)
	{
		final int quiverAmmoId = client.getVarpValue(VarPlayerID.DIZANAS_QUIVER_TEMP_AMMO);

		double strengthBonus = 0.0;

		// If the player has a primary ammo slot that's compatible, use it
		if (BowAmmoRegistry.isAmmoCompatible(bowId, ammoId))
		{
			strengthBonus = getItemStrength(Skill.RANGED, equippedItems, itemManager, EquipmentInventorySlot.AMMO);
		}
		else if(BowAmmoRegistry.isAmmoCompatible(bowId, quiverAmmoId))
		{
			// Otherwise, if the quiver ammo is compatible, use it
			final ItemStats itemStats = itemManager.getItemStats(quiverAmmoId);
			
			if (itemStats == null)
			{
				return 0.0;
			}
			final ItemEquipmentStats itemEquipmentStats = itemStats.getEquipment();

			if (itemEquipmentStats == null)
			{
				return 0.0;
			}
			else
			{
				strengthBonus = itemEquipmentStats.getRstr();
			}

		}

		return strengthBonus;
	}
}
