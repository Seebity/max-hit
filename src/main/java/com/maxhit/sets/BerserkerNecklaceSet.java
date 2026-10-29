package com.maxhit.sets;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.gameval.ItemID;

public class BerserkerNecklaceSet extends EquipmentSet
{
	@Override
	protected Map<EquipmentInventorySlot, Collection<Integer>> getEquipment()
	{
		return Map.of(
			EquipmentInventorySlot.AMULET, Set.of(
				ItemID.JEWL_BESERKER_NECKLACE,
				ItemID.JEWL_BESERKER_NECKLACE_ORNAMENT),
			EquipmentInventorySlot.WEAPON, Set.of(
				ItemID.TZHAAR_SPLITSWORD,
				ItemID.BR_OBSIDIAN_SWORD,
				ItemID.TZHAAR_KNIFE,
				ItemID.TZHAAR_MACE,
				ItemID.TZHAAR_MAUL,
				ItemID.TZHAAR_MAUL_T
			)
		);
	}

	@Override
	public double getMultiplier()
	{
		return 0.2;
	}
}
