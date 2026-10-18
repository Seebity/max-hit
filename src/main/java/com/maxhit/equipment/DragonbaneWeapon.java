package com.maxhit.equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.gameval.ItemID;

@AllArgsConstructor
public enum DragonbaneWeapon
{
	CROSSBOW(ItemID.DRAGONHUNTER_XBOW, 0.3),
	LANCE(ItemID.DRAGONHUNTER_LANCE, 0.2),
	WAND(ItemID.DRAGONHUNTER_WAND, 0.4),
	;

	@Getter
	private final int itemId;
	@Getter
	private final double damageMultiplier;
}
