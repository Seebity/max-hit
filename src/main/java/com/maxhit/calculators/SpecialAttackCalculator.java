package com.maxhit.calculators;

import com.maxhit.equipment.EnchantedBolt;
import com.maxhit.equipment.EquipmentFunctions;
import com.maxhit.equipment.SpecialAttackWeapon;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemVariationMapping;

public class SpecialAttackCalculator
{
	private final Client client;

	private final ItemManager itemManager;
	@Setter
	private ItemContainer equippedItems;

	public SpecialAttackCalculator(Client client, ItemManager itemManager)
	{
		this.client = client;
		this.itemManager = itemManager;
	}

	public double getSpecialMaxHit(double maxHitBase, int npcId)
	{
		Item weapon = equippedItems.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());

		if (weapon == null)
		{
			return maxHitBase;
		}

		for (SpecialAttackWeapon specialAttackWeapon : SpecialAttackWeapon.values())
		{
			if (ItemVariationMapping.getVariations(ItemVariationMapping.map(specialAttackWeapon.getItemId())).contains(weapon.getId()))
			{
				return specialAttackWeapon.getSpecialAttackDamage(client, itemManager, equippedItems, maxHitBase);
			}
		}

		String weaponName = EquipmentFunctions.GetEquippedItemString(client, equippedItems, EquipmentInventorySlot.WEAPON);

		if (weaponName.contains("rossbow"))
		{
			int rangedLevel = client.getBoostedSkillLevel(Skill.RANGED);
			boolean zaryteEquipped = EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, ItemID.ZARYTE_XBOW);
			for (EnchantedBolt bolt : EnchantedBolt.values())
			{
				if (bolt.isEquipped(client, equippedItems))
				{
					if (zaryteEquipped)
					{
						return bolt.getZaryteDamageMultiplier(maxHitBase, rangedLevel, npcId);
					}
					else
					{
						return bolt.getDamageMultiplier(maxHitBase, rangedLevel, npcId);
					}
				}
			}
		}
		return maxHitBase;
	}
}
