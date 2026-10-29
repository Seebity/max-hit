package com.maxhit.equipment;

import lombok.AllArgsConstructor;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;

@AllArgsConstructor
public enum SalveAmulet
{
	REGULAR(ItemID.CRYSTALSHARD_NECKLACE, 1.1667, 0.0, 0.0),
	ENCHANTED(ItemID.LOTR_CRYSTALSHARD_NECKLACE_UPGRADE, 1.2, 0.0, 0.0),
	IMBUED(ItemID.NZONE_SALVE_AMULET, 1.1667, 1.1667, 1.15),
	ENCHANTED_IMBUED(ItemID.NZONE_SALVE_AMULET_E, 1.2, 1.2, 1.2);

	private final int itemId;
	private final double meleeBonus;
	private final double rangedBonus;
	private final double magicBonus;

	public boolean isEquipped(ItemContainer equippedItems)
	{
		return EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.AMULET, itemId);
	}

	public double getBonus(Skill skill)
	{
		if (skill == Skill.STRENGTH)
		{
			return meleeBonus;
		}
		if (skill == Skill.RANGED)
		{
			return rangedBonus;
		}
		return magicBonus;
	}


}
