package com.maxhit.equipment;

import java.util.Collection;
import lombok.AllArgsConstructor;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemVariationMapping;

@FunctionalInterface
interface IMultiHits
{
	double split();
}

@AllArgsConstructor
public enum MultihitWeapons
{
	SULPHUR_BLADES(ItemID.SULPHUR_BLADES),
	GLACIAL_TEMOTLI(ItemID.GLACIAL_TEMOTLI),
	EARTHBOUND_TECPATL(ItemID.EARTHBOUND_TECPATL),
	TORAGS_HAMMERS(ItemID.BARROWS_TORAG_WEAPON),
	DUAL_MACUAHUITL(ItemID.DUAL_MACUAHUITL),
	DARK_BOW(ItemID.DARKBOW),
	TWINFLAME_STAFF(ItemID.TWINFLAME_STAFF),
	TONALZTICS_OF_RALOS(ItemID.TONALZTICS_OF_RALOS_CHARGED),
	SCYTHE_OF_VITUR(ItemID.SCYTHE_OF_VITUR),
	;

	private final int itemId;

	public Collection<Integer> getVarients()
	{
		int baseId = ItemVariationMapping.map(itemId);
		return ItemVariationMapping.getVariations(baseId);
	}
}
