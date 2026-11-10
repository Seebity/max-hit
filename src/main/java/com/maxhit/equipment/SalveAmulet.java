package com.maxhit.equipment;

import java.util.List;
import lombok.AllArgsConstructor;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;

@AllArgsConstructor
public enum SalveAmulet
{
	REGULAR(1.1667, 0.0, 0.0, ItemID.CRYSTALSHARD_NECKLACE),
	ENCHANTED(1.2, 0.0, 0.0, ItemID.LOTR_CRYSTALSHARD_NECKLACE_UPGRADE),
	IMBUED(1.1667, 1.1667, 1.15, ItemID.NZONE_SALVE_AMULET, ItemID.SW_SALVE_AMULET, ItemID.PVPA_SALVE_AMULET),
	ENCHANTED_IMBUED(1.2, 1.2, 1.2, ItemID.NZONE_SALVE_AMULET_E, ItemID.SW_SALVE_AMULET_E, ItemID.PVPA_SALVE_AMULET_E),
	;

	private final double meleeBonus;
	private final double rangedBonus;
	private final double magicBonus;
	private final List<Integer> itemIds;

	SalveAmulet(double meleeBonus, double rangedBonus, double magicBonus, Integer... itemIds)
	{
		this.meleeBonus = meleeBonus;
		this.rangedBonus = rangedBonus;
		this.magicBonus = magicBonus;
		this.itemIds = List.of(itemIds);
	}

	public boolean isEquipped(ItemContainer equippedItems)
	{
		for (Integer itemId : itemIds)
		{
			if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.AMULET, itemId))
			{
				return true;
			}
		}
		return false;
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
