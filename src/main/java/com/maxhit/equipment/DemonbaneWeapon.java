package com.maxhit.equipment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;

@AllArgsConstructor
public enum DemonbaneWeapon
{
	BURNING_CLAWS(ItemID.BONE_CLAWS, Skill.STRENGTH, 0.05),
	SILVERLIGHT(ItemID.SILVERLIGHT, Skill.STRENGTH,0.6),
	DYED_SILVERLIGHT(ItemID.AGRITH_SILVERLIGHT_DYED, Skill.STRENGTH,0.6),
	DARKLIGHT(ItemID.DARKLIGHT, Skill.STRENGTH,0.6),
	ARCLGIHT_INACTIVE(ItemID.ARCLIGHT_INACTIVE, Skill.STRENGTH,0.6),
	ARCLIGHT(ItemID.ARCLIGHT, Skill.STRENGTH,0.7),
	EMBERLIGHT(ItemID.EMBERLIGHT, Skill.STRENGTH,0.7),
	SCORCHING_BOW(ItemID.SCORCHING_BOW, Skill.RANGED,0.3),
	PURGING_STAFF(ItemID.PURGING_STAFF, Skill.MAGIC,0.25),
	HOLY_WATER(ItemID.HOLY_WATER, Skill.RANGED,0.6)
	;

	@Getter
	private final int itemId;
	@Getter
	private final Skill skill;
	@Getter
	private final double damageMultiplier;
}
