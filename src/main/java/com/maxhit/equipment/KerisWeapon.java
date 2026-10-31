package com.maxhit.equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;

@AllArgsConstructor
public enum KerisWeapon
{
	KERIS(ItemID.CONTACT_KERIS, 1.33),
	KERIS_P(ItemID.CONTACT_KERIS_P,1.33),
	KERIS_P_(ItemID.CONTACT_KERIS_P_,1.33),
	KERIS_P__P(ItemID.CONTACT_KERIS_P__,1.33),
	KERIS_PARTISAN(ItemID.KERIS_PARTISAN,1.33),
	KERIS_PARTISAN_BREACH(ItemID.KERIS_PARTISAN_BREACH,1.33),
	KERIS_PARTISAN_CORRUPTION(ItemID.KERIS_PARTISAN_CORRUPTION,1.33),
	KERIS_PARTISAN_SUN(ItemID.KERIS_PARTISAN_SUN,1.33),
	KERIS_PARTISAN_AMASCUT(ItemID.KERIS_PARTISAN_AMASCUT, 1.15),
	;

	@Getter
	private final int itemId;
	@Getter
	private final double damageMultiplier;
}
