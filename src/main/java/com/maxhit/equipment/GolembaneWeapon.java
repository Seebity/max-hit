package com.maxhit.equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;

@AllArgsConstructor
public enum GolembaneWeapon
{
	BARRONITE_MACE(ItemID.BARRONITE_MACE, 0.15),
	GRANITE_HAMMER(ItemID.GRANITE_HAMMER, 0.3),
	;

	@Getter
	private final int itemId;
	@Getter
	private final double damageMultiplier;
	}
