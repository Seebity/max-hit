package com.maxhit;

import java.util.Map;

public class Prayers
{

	public static final Map<PrayerType, Double> STRENGTH_PRAYERS = Map.of(
		PrayerType.PIETY, 1.23,
		PrayerType.CHIVALRY, 1.18,
		PrayerType.ULTIMATE_STRENGTH, 1.15,
		PrayerType.SUPERHUMAN_STRENGTH, 1.1,
		PrayerType.BURST_OF_STRENGTH, 1.05
	);

	public static final Map<PrayerType, Double> RANGED_PRAYERS = Map.of(
		PrayerType.RIGOUR, 1.23,
		PrayerType.DEADEYE, 1.18,
		PrayerType.EAGLE_EYE, 1.15,
		PrayerType.HAWK_EYE, 1.1,
		PrayerType.SHARP_EYE, 1.05
	);
	public static final Map<PrayerType, Double> MAGIC_PRAYERS = Map.of(
		PrayerType.AUGURY, 0.04,
		PrayerType.MYSTIC_VIGOUR, 0.03,
		PrayerType.MYSTIC_MIGHT, 0.02,
		PrayerType.MYSTIC_LORE, 0.01
	);
}
