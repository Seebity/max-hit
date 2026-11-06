package com.maxhit.equipment;
import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemVariationMapping;

public class BowAmmoRegistry
{
	// High-performance direct lookups
	private static final Map<Integer, Set<Integer>> BOW_AMMO_MAP = new HashMap<>();
	private static final Set<Integer> SELF_CONTAINED_BOWS = ImmutableSet.<Integer>builder()
		.add(ItemID.WILD_CAVE_BOW_UNCHARGED)       // Craw's Bow
		.add(ItemID.WILD_CAVE_BOW_CHARGED)         // Craw's Bow
		.add(ItemID.WILD_CAVE_WEBWEAVER_UNCHARGED) // Webweaver Bow
		.add(ItemID.WILD_CAVE_WEBWEAVER_CHARGED)   // Webweaver Bow
		.add(ItemID.GAUNTLET_RANGED_T1_HM)
		.add(ItemID.GAUNTLET_RANGED_T2_HM)
		.add(ItemID.GAUNTLET_RANGED_T3_HM)
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.CRYSTAL_BOW)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.ROVING_CRYSTAL_BOW_1000)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.BOW_OF_FAERDHINEN)))

		.build();

	//region Light / Throwing Weapons
	private static final Set<Integer> LIGHT_WEAPONS = ImmutableSet.<Integer>builder()
		.add(ItemID.BLISTERWOOD_STAKE)
		.add(ItemID.HOLY_WATER)
		.add(ItemID.SOTFA_FOREST_TALON)
		.add(ItemID.TZHAAR_THROWINGRING)
		.add(ItemID.TOXIC_BLOWPIPE_LOADED)
		.add(ItemID.TOXIC_BLOWPIPE_LOADED_ORNAMENT)
		.add(ItemID.CAMPHOR_BLOWPIPE)
		.add(ItemID.IRONWOOD_BLOWPIPE)
		.add(ItemID.ROSEWOOD_BLOWPIPE)
		.add(ItemID.HG_HUNTER_SPEAR)
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.TONALZTICS_OF_RALOS_UNCHARGED)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.BRONZE_DART)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.IRON_DART)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.STEEL_DART)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.MITHRIL_DART)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.ADAMANT_DART)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.RUNE_DART)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.AMETHYST_DART)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DRAGON_DART)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.BRONZE_KNIFE)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.IRON_KNIFE)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.STEEL_KNIFE)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.BLACK_KNIFE)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.MITHRIL_KNIFE)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.ADAMANT_KNIFE)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.RUNE_KNIFE)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DRAGON_KNIFE)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.BRONZE_THROWNAXE)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.IRON_THROWNAXE)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.STEEL_THROWNAXE)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.MITHRIL_THROWNAXE)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.ADAMNT_THROWNAXE)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.RUNE_THROWNAXE)))
		.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DRAGON_THROWNAXE))).build();
	//endregion

	static
	{
		//region Regular bow ammo
		Set<Integer> ironArrowsOrLower = ImmutableSet.<Integer>builder()
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.BRONZE_ARROW)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.IRON_ARROW))).build();
		Set<Integer> steelArrowsOrLower = ImmutableSet.<Integer>builder()
			.addAll(ironArrowsOrLower)
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.STEEL_ARROW))).build();
		Set<Integer> mithrilArrowsOrLower = ImmutableSet.<Integer>builder()
			.addAll(steelArrowsOrLower)
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.MITHRIL_ARROW))).build();
		Set<Integer> adamantArrowsOrLower = ImmutableSet.<Integer>builder()
			.addAll(mithrilArrowsOrLower)
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.ADAMANT_ARROW))).build();
		Set<Integer> runeArrowsOrLower = ImmutableSet.<Integer>builder()
			.addAll(adamantArrowsOrLower)
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.RUNE_ARROW))).build();
		Set<Integer> amethystArrowsOrLower = ImmutableSet.<Integer>builder()
			.addAll(runeArrowsOrLower)
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.AMETHYST_ARROW))).build();
		Set<Integer> dragonArrowsOrLower = ImmutableSet.<Integer>builder()
			.addAll(amethystArrowsOrLower)
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DRAGON_ARROW))).build();
		//endregion

		//region Niche bow ammo
		Set<Integer> brutalMithrilArrowsOrLower = ImmutableSet.<Integer>builder()
			.add(ItemID.OGRE_ARROW)
			.add(ItemID.ZOGRE_BRUTAL_BRONZE)
			.add(ItemID.ZOGRE_BRUTAL_IRON)
			.add(ItemID.ZOGRE_BRUTAL_STEEL)
			.add(ItemID.ZOGRE_BRUTAL_BLACK)
			.add(ItemID.ZOGRE_BRUTAL_MITHRIL).build();
		Set<Integer> brutalRuneArrowsOrLower = ImmutableSet.<Integer>builder()
			.addAll(brutalMithrilArrowsOrLower)
			.add(ItemID.ZOGRE_BRUTAL_ADAMANT)
			.add(ItemID.ZOGRE_BRUTAL_RUNE).build();
		//endregion

		//region Regular Bolts
		Set<Integer> bronzeBoltsOrLower = ImmutableSet.<Integer>builder()
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.OPAL_BOLT)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.BOLT))).build();
		Set<Integer> bluriteBoltsOrLower = ImmutableSet.<Integer>builder()
			.addAll(bronzeBoltsOrLower)
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.XBOWS_CROSSBOW_BOLTS_BLURITE_TIPPED_JADE)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.XBOWS_CROSSBOW_BOLTS_BLURITE))).build();
		Set<Integer> ironBoltsOrLower = ImmutableSet.<Integer>builder()
			.addAll(bluriteBoltsOrLower)
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.PEARL_BOLT)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.XBOWS_CROSSBOW_BOLTS_SILVER)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.XBOWS_CROSSBOW_BOLTS_IRON))).build();
		Set<Integer> steelBoltsOrLower = ImmutableSet.<Integer>builder()
			.addAll(ironBoltsOrLower)
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.XBOWS_CROSSBOW_BOLTS_STEEL_TIPPED_REDTOPAZ)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.XBOWS_CROSSBOW_BOLTS_STEEL))).build();
		Set<Integer> mithrilBoltsOrLower = ImmutableSet.<Integer>builder()
			.addAll(steelBoltsOrLower)
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.XBOWS_CROSSBOW_BOLTS_MITHRIL_TIPPED_SAPPHIRE)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.XBOWS_CROSSBOW_BOLTS_MITHRIL_TIPPED_EMERALD)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.XBOWS_CROSSBOW_BOLTS_MITHRIL))).build();
		Set<Integer> adamantBoltsOrLower = ImmutableSet.<Integer>builder()
			.addAll(mithrilBoltsOrLower)
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.XBOWS_CROSSBOW_BOLTS_ADAMANTITE_TIPPED_RUBY)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.XBOWS_CROSSBOW_BOLTS_ADAMANTITE_TIPPED_DIAMOND)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.XBOWS_CROSSBOW_BOLTS_ADAMANTITE))).build();
		Set<Integer> runeBoltsOrLower = ImmutableSet.<Integer>builder()
			.addAll(adamantBoltsOrLower)
			.add(ItemID.SLAYER_BROAD_BOLT)
			.add(ItemID.SLAYER_BROAD_BOLT_AMETHYST)
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.XBOWS_CROSSBOW_BOLTS_RUNITE_TIPPED_DRAGONSTONE)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.XBOWS_CROSSBOW_BOLTS_RUNITE_TIPPED_ONYX)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.XBOWS_CROSSBOW_BOLTS_RUNITE))).build();
		Set<Integer> dragonBoltsOrLower = ImmutableSet.<Integer>builder()
			.addAll(runeBoltsOrLower)
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DRAGON_BOLTS_UNENCHANTED_OPAL)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DRAGON_BOLTS_UNENCHANTED_JADE)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DRAGON_BOLTS_UNENCHANTED_PEARL)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DRAGON_BOLTS_UNENCHANTED_TOPAZ)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DRAGON_BOLTS_UNENCHANTED_SAPPHIRE)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DRAGON_BOLTS_UNENCHANTED_EMERALD)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DRAGON_BOLTS_UNENCHANTED_RUBY)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DRAGON_BOLTS_UNENCHANTED_DIAMOND)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DRAGON_BOLTS_UNENCHANTED_DRAGONSTONE)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DRAGON_BOLTS_UNENCHANTED_ONYX)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DRAGON_BOLTS))).build();
		//endregion

		//region Niche bolts
		Set<Integer> boneBoltsOrLower = ImmutableSet.<Integer>builder()
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.BOLT)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.XBOWS_CROSSBOW_BOLTS_BLURITE)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.XBOWS_CROSSBOW_BOLTS_IRON)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DTTD_BONE_CROSSBOW_BOLT))).build();
		Set<Integer> kebbitBolts = ImmutableSet.<Integer>builder()
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.HUNTINGBOW_BOLTS)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.HUNTINGBOW_BIGBOLTS))).build();
		Set<Integer> antlerBolts = ImmutableSet.<Integer>builder()
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.SUNLIGHT_ANTELOPE_BOLT)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.MOONLIGHT_ANTELOPE_BOLT))).build();
		Set<Integer> boltRacks = ImmutableSet.<Integer>builder()
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.BARROWS_KARIL_AMMO))).build();
		Set<Integer> ascensionBolts = ImmutableSet.<Integer>builder()
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.ASCENSION_BOLTS))).build();
		//endregion

		//region Javelins
		Set<Integer> javelins = ImmutableSet.<Integer>builder()
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.BRONZE_JAVELIN)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.IRON_JAVELIN)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.STEEL_JAVELIN)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.MITHRIL_JAVELIN)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.ADAMANT_JAVELIN)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.RUNE_JAVELIN)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.AMETHYST_JAVELIN)))
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.DRAGON_JAVELIN))).build();

		Set<Integer> silvJavelin = ImmutableSet.<Integer>builder()
			.addAll(ItemVariationMapping.getVariations(ItemVariationMapping.map(ItemID.MYQ6_JAVELIN))).build();
		//endregion

		//region Regular Bows
		// Goblinraid = Cursed goblin bow
		registerBows(new Integer[]{ItemID.SHORTBOW, ItemID.LONGBOW, ItemID.RAIN_BOW, ItemID.GOBLINRAID_BOW}, ironArrowsOrLower);
		registerBows(new Integer[]{ItemID.OAK_SHORTBOW, ItemID.OAK_LONGBOW, ItemID.OAK_LONGBOW_SIGNED}, steelArrowsOrLower);
		registerBows(new Integer[]{ItemID.WILLOW_SHORTBOW, ItemID.WILLOW_LONGBOW, ItemID.TRAIL_COMPOSITE_BOW_WILLOW}, mithrilArrowsOrLower);
		registerBows(new Integer[]{ItemID.MAPLE_SHORTBOW,  ItemID.MAPLE_LONGBOW}, adamantArrowsOrLower);
		registerBows(new Integer[]{ItemID.YEW_SHORTBOW,    ItemID.YEW_LONGBOW, ItemID.TRAIL_COMPOSITE_BOW_YEW}, runeArrowsOrLower);
		registerBows(new Integer[]{
			ItemID.MAGIC_SHORTBOW,
			ItemID.MAGIC_SHORTBOW_I,
			ItemID.MAGIC_LONGBOW,
			ItemID.TRAIL_COMPOSITE_BOW_MAGIC,
			ItemID.DAGANOTH_CAVE_MAGIC_SHORTBOW,
			ItemID.RAT_BONE_BOW
		}, amethystArrowsOrLower);
		registerBows(new Integer[]{
			ItemID.DARKBOW,
			ItemID.DARKBOW_BLUE,
			ItemID.DARKBOW_GREEN,
			ItemID.DARKBOW_WHITE,
			ItemID.DARKBOW_YELLOW,
			ItemID.BH_DARKBOW_IMBUE,
			ItemID.TWISTED_BOW,
			ItemID.VENATOR_BOW,
			ItemID.VENATOR_BOW_ORNAMENT,
			ItemID.SCORCHING_BOW,
			ItemID.TRAIL_RANGER_BOW}, dragonArrowsOrLower);
		//endregion

		//region Niche Bows
		// Training Bow
		registerBows(new Integer[]{ItemID.AIDE_SHORTBOW}, ImmutableSet.<Integer>builder().add(ItemID.AIDE_ARROW).build());
		// Ogre Bow
		registerBows(new Integer[]{ItemID.OGRE_BOW}, brutalMithrilArrowsOrLower);
		// Ogre Comp Bow
		registerBows(new Integer[]{ItemID.ZOGRE_BOW}, brutalRuneArrowsOrLower);
		//endregion

		//region Regular Crossbows
		registerBows(new Integer[]{ItemID.CROSSBOW, ItemID.PHOENIX_CROSSBOW, ItemID.XBOWS_CROSSBOW_BRONZE}, bronzeBoltsOrLower);
		registerBows(new Integer[]{ItemID.XBOWS_CROSSBOW_BLURITE}, bluriteBoltsOrLower);
		registerBows(new Integer[]{ItemID.XBOWS_CROSSBOW_IRON}, ironArrowsOrLower);
		registerBows(new Integer[]{ItemID.XBOWS_CROSSBOW_STEEL}, steelBoltsOrLower);
		registerBows(new Integer[]{ItemID.XBOWS_CROSSBOW_MITHRIL}, mithrilBoltsOrLower);
		registerBows(new Integer[]{ItemID.XBOWS_CROSSBOW_ADAMANTITE}, adamantBoltsOrLower);
		registerBows(new Integer[]{ItemID.XBOWS_CROSSBOW_RUNITE, ItemID.LEAGUE_3_RUNE_XBOW}, runeBoltsOrLower);
		registerBows(new Integer[]{
			ItemID.XBOWS_CROSSBOW_DRAGON,
			ItemID.BH_XBOWS_CROSSBOW_DRAGON_CORRUPTED,
			ItemID.DRAGONHUNTER_XBOW,
			ItemID.DRAGONHUNTER_XBOW_KBD,
			ItemID.DRAGONHUNTER_XBOW_VORKATH,
			ItemID.ACB,
			ItemID.ZARYTE_XBOW}, dragonBoltsOrLower);
		//endregion

		//region Niche Crossbows
		registerBows(new Integer[]{ItemID.DTTD_BONE_CROSSBOW}, boneBoltsOrLower);
		registerBows(new Integer[]{ItemID.HUNTING_CROSSBOW}, kebbitBolts);
		registerBows(new Integer[]{ItemID.HUNTING_CROSSBOW_SUNLIGHT}, antlerBolts);
		registerBows(new Integer[]{
			ItemID.BARROWS_KARIL_WEAPON,
			ItemID.BARROWS_KARIL_WEAPON_100,
			ItemID.BARROWS_KARIL_WEAPON_75,
			ItemID.BARROWS_KARIL_WEAPON_50,
			ItemID.BARROWS_KARIL_WEAPON_25
		}, boltRacks);
		registerBows(new Integer[]{ItemID.ASCENSION_CROSSBOWS}, ascensionBolts);
		//endregion

		//region Ballistae
		registerBows(new Integer[]{ItemID.LIGHT_BALLISTA}, javelins);
		registerBows(new Integer[]{ItemID.HEAVY_BALLISTA, ItemID.HEAVY_BALLISTA_ORNAMENT}, javelins);
		registerBows(new Integer[]{ItemID.MYQ6_BALLISTA}, silvJavelin);
		//endregion
	}

	private static void registerBows(Integer[] bowIds, Set<Integer> allowedAmmo)
	{
		for (int bowId : bowIds)
		{
			BOW_AMMO_MAP.put(bowId, allowedAmmo);
		}
	}

	public static boolean isSelfContainedBow(int weaponId)
	{
		return SELF_CONTAINED_BOWS.contains(weaponId);
	}

	public static boolean isLightWeapon(int weaponId)
	{
		return LIGHT_WEAPONS.contains(weaponId);
	}

	public static boolean isAmmoCompatible(int weaponId, int ammoId)
	{
		if (SELF_CONTAINED_BOWS.contains(weaponId))
		{
			return false;
		}

		Set<Integer> allowedAmmo = BOW_AMMO_MAP.get(weaponId);
		return allowedAmmo != null && allowedAmmo.contains(ammoId);
	}

	public static boolean isABow(int weaponId)
	{
		return SELF_CONTAINED_BOWS.contains(weaponId) || BOW_AMMO_MAP.containsKey(weaponId);
	}
}
