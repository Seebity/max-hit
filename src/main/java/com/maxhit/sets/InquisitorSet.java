package com.maxhit.sets;

import com.maxhit.equipment.EquipmentFunctions;
import com.maxhit.styles.AttackType;
import com.maxhit.styles.StyleFactory;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.ItemID;

public class InquisitorSet
{

	public static double getMultiplier(Client client, ItemContainer equippedItems)
	{
		double bonus = 0.0;
		AttackType attackType = StyleFactory.getAttackType(client);
		if (attackType != AttackType.CRUSH)
			return bonus;

		if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, ItemID.INQUISITORS_HELM))
		{
			bonus += 0.005;
		}

		if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, ItemID.INQUISITORS_BODY))
		{
			bonus += 0.01;
		}

		if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, ItemID.INQUISITORS_SKIRT))
		{
			bonus += 0.01;
		}

		return bonus;
	}
}
