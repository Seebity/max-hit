package com.maxhit.equipment;

import lombok.Getter;
import net.runelite.api.gameval.ItemID;

@Getter
public enum ElementalAmulet
{
	AMULET_OF_AIR(ItemID.AMULET_OF_AIR, "AIR"),
	AMULET_OF_WATER(ItemID.AMULET_OF_WATER, "WATER"),
	AMULET_OF_EARTH(ItemID.AMULET_OF_EARTH, "EARTH"),
	AMULET_OF_FIRE(ItemID.AMULET_OF_FIRE, "FIRE"),
	ELEMENTAL_AMULET(ItemID.ELEMENTAL_AMULET, "AIR", "WATER",  "EARTH", "FIRE"),
	;

	private final int itemId;

	private final String[] elements;

	ElementalAmulet(int itemId, String... elements)
	{
		this.itemId = itemId;
		this.elements = elements;
	}
}
