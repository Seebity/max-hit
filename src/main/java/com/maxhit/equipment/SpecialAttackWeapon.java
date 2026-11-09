package com.maxhit.equipment;

import com.maxhit.calculators.StrengthBonusCalculator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.Client;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.game.ItemManager;

@RequiredArgsConstructor
public enum SpecialAttackWeapon
{
	ABYSSAL_BLUDGEON(ItemID.ABYSSAL_BLUDGEON, -1.0)
		{
			@Override
			public double getSpecialAttackDamage(Client client, ItemManager itemManager, ItemContainer equippedItems, double maxHitBase)
			{
				int maxPrayerPoints = client.getRealSkillLevel(Skill.PRAYER);
				int actualPrayerPoints = client.getBoostedSkillLevel(Skill.PRAYER);
				int prayerPointsMissing = maxPrayerPoints - actualPrayerPoints;
				double damageMultiplier = (1.0 + (0.005 * prayerPointsMissing));
				return Math.floor(maxHitBase * damageMultiplier);
			}
		},
	ABYSSAL_DAGGER(ItemID.ABYSSAL_DAGGER, 0.85),
	AGS(ItemID.AGS, 1.375),
	ARKAN_BLADE(ItemID.ARKAN_BLADE, 1.5),
	BGS(ItemID.BGS, 1.21),
	BLESSED_SARADOMIN_SWORD(ItemID.BLESSED_SARADOMIN_SWORD, 1.25),
	BRAIN_ANCHOR(ItemID.BRAIN_ANCHOR, 1.1),
	CRIMSON_KISTEN(ItemID.CRIMSON_KISTEN, 1.7),
	CRYSTAL_HALBERD(ItemID.CRYSTAL_HALBERD, 1.1),
	DARKBOW(ItemID.DARKBOW, 1.3),
	DAWNBRINGER(ItemID.VERZIK_SPECIAL_WEAPON, -1.0)
		{
			@Override
			public double getSpecialAttackDamage(Client client, ItemManager itemManager, ItemContainer equippedItems, double maxHitBase)
			{
				return 150.0;
			}
		},
	DRAGON_CLAWS(ItemID.DRAGON_CLAWS, 1.98),
	DRAGON_CROSSBOW(ItemID.XBOWS_CROSSBOW_DRAGON, 1.2),
	DRAGON_DAGGER(ItemID.DRAGON_DAGGER, -1.0)
		{
			@Override
			public double getSpecialAttackDamage(Client client, ItemManager itemManager, ItemContainer equippedItems, double maxHitBase)
			{
				return 2.0 * Math.floor(maxHitBase * 1.15);
			}
		},
	DRAGON_HALBERD(ItemID.DRAGON_HALBERD, 1.1),
	DRAGON_HASTA(ItemID.BRUT_DRAGON_SPEAR, -1.0)
		{
			@Override
			public double getSpecialAttackDamage(Client client, ItemManager itemManager, ItemContainer equippedItems, double maxHitBase)
			{
				return (1.0 + (0.5 * client.getVarbitValue(VarPlayerID.SA_ENERGY) / 1000.0));
			}
		},
	DRAGON_LONGSWORD(ItemID.DRAGON_LONGSWORD, 1.25),
	DRAGON_MACE(ItemID.DRAGON_MACE, 1.5),
	DRAGON_SHORTSWORD(ItemID.DRAGON_SHORTSWORD, 1.25),
	DRAGON_WARHAMMER(ItemID.DRAGON_WARHAMMER, 1.5),
	DUAL_MACUAHUITL(ItemID.DUAL_MACUAHUITL, 1.25),
	EYE_OF_AYAK(ItemID.EYE_OF_AYAK, 1.3),
	GRANITE_HAMMER(ItemID.GRANITE_HAMMER, -1.0)
		{
			@Override
			public double getSpecialAttackDamage(Client client, ItemManager itemManager, ItemContainer equippedItems, double maxHitBase)
			{
				return maxHitBase + 5.0;
			}
		},
	MSB(ItemID.MAGIC_SHORTBOW, -1.0)
		{
			@Override
			public double getSpecialAttackDamage(Client client, ItemManager itemManager, ItemContainer equippedItems, double maxHitBase)
			{
				double rangedLevel = client.getBoostedSkillLevel(Skill.RANGED);
				if (maxHitBase == 0.0)
				{
					return 0.0;
				}
				double ammoStrength = StrengthBonusCalculator.getItemStrength(Skill.RANGED, equippedItems, itemManager, EquipmentInventorySlot.AMMO);
				double maxHit = Math.floor(0.5 + ((rangedLevel + 10.0) * (ammoStrength + 64) / 640));
				return 2.0 * maxHit;
			}
		},
	MSB_COMP(ItemID.TRAIL_COMPOSITE_BOW_MAGIC, -1.0)
		{
			@Override
			public double getSpecialAttackDamage(Client client, ItemManager itemManager, ItemContainer equippedItems, double maxHitBase)
			{
				double rangedLevel = client.getBoostedSkillLevel(Skill.RANGED);
				if (maxHitBase == 0.0)
				{
					return 0.0;
				}
				double ammoStrength = StrengthBonusCalculator.getItemStrength(Skill.RANGED, equippedItems, itemManager, EquipmentInventorySlot.AMMO);
				double maxHit = Math.floor(0.5 + ((rangedLevel + 10.0) * (ammoStrength + 64) / 640));
				return 2.0 * maxHit;
			}
		},
	HEAVY_BALLISTA(ItemID.HEAVY_BALLISTA, 1.25),
	LIGHT_BALLISTA(ItemID.LIGHT_BALLISTA, 1.25),
	OSMUMTENS_FANG(ItemID.OSMUMTENS_FANG, 1.1765),
	SARADOMIN_SWORD(ItemID.SARADOMIN_SWORD, 1.1),
	SGS(ItemID.SGS, 1.1),
	SUNSPEAR(ItemID.SUNSPEAR, -1.0)
		{
			@Override
			public double getSpecialAttackDamage(Client client, ItemManager itemManager, ItemContainer equippedItems, double maxHitBase)
			{
				return Math.max(0.0, Math.floor(maxHitBase * 0.7));
			}
		},
	TOXIC_BLOWPIPE(ItemID.TOXIC_BLOWPIPE, 1.5),
	VOIDWAKER(ItemID.VOIDWAKER, 1.5),
	WEBWEAVER(ItemID.WILD_CAVE_WEBWEAVER_CHARGED, -1.0)
		{
			@Override
			public double getSpecialAttackDamage(Client client, ItemManager itemManager, ItemContainer equippedItems, double maxHitBase)
			{
				double maxDamagePerHit = Math.ceil(maxHitBase * 0.4);
				return Math.floor(maxDamagePerHit * 4.0);
			}
		},
	XBOWS_CROSSBOW_DRAGON(ItemID.XBOWS_CROSSBOW_DRAGON, 1.2),
	ZGS(ItemID.ZGS, 1.1),
	ZORYAS_TOME(ItemID.ZORYAS_TOME, 1.6),
	;

	@Getter
	private final int itemId;
	@Getter
	private final double damageMultiplier;

	public double getSpecialAttackDamage(Client client,
										 ItemManager itemManager,
										 ItemContainer equippedItems,
										 double maxHitBase)
	{
		return Math.floor(maxHitBase * damageMultiplier);
	}
}