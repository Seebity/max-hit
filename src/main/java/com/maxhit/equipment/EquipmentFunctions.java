package com.maxhit.equipment;

import javax.annotation.Nonnull;
import net.runelite.api.Item;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.EquipmentInventorySlot;


public class EquipmentFunctions
{
	public static int GetEquippedItemId(ItemContainer equippedItems, EquipmentInventorySlot slot)
	{
		// Have to convert enum to int i.e. use ordinal
		Item item = equippedItems.getItem(slot.getSlotIdx());

		if (item == null)
		{
			return -1;
		}
		return item.getId();
	}

	public static boolean HasEquipped(ItemContainer equippedItems, EquipmentInventorySlot slot, int itemID)
	{
		if (equippedItems == null)
		{
			return false;
		}
		Item equippedItem = equippedItems.getItem(slot.ordinal());
		if (equippedItem == null)
		{
			return false;
		}
		return equippedItem.getId() == itemID;
	}

	@Nonnull
	public static String GetEquippedItemString(Client client, ItemContainer equippedItems, EquipmentInventorySlot slot)
	{
		if (equippedItems == null)
		{
			return "";
		}
		Item equippedItem = equippedItems.getItem(slot.getSlotIdx());
		if (equippedItem == null)
		{
			return "";
		}
		return client.getItemDefinition(equippedItem.getId()).getName();
	}
}
