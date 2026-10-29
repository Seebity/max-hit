package com.maxhit.MaxHitTest;

import com.maxhit.MagicSpell;
import com.maxhit.calculators.MagicMaxHitCalculator;
import com.maxhit.calculators.MeleeMaxHitCalculator;
import com.maxhit.calculators.RangedMaxHitCalculator;
import com.maxhit.styles.AttackStyle;
import com.maxhit.styles.CombatStyle;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.Player;
import net.runelite.api.gameval.ItemID;

@Getter
public enum PlayerSetup
{
	BASE(
		createEquipment(Map.of()),
		CombatStyle.MELEE, AttackStyle.ACCURATE,  MeleeMaxHitCalculator.class,
		0.0f, 1.0, 11.0, 0.0),
	MELEE_VOID(
		createEquipment(Map.of(
			EquipmentInventorySlot.HEAD.getSlotIdx(), new Item(ItemID.GAME_PEST_MELEE_HELM, 1),
			EquipmentInventorySlot.BODY.getSlotIdx(), new Item(ItemID.PEST_VOID_KNIGHT_TOP, 1),
			EquipmentInventorySlot.LEGS.getSlotIdx(), new Item(ItemID.PEST_VOID_KNIGHT_ROBES, 1),
			EquipmentInventorySlot.GLOVES.getSlotIdx(), new Item(ItemID.PEST_VOID_KNIGHT_GLOVES, 1)
		)),
		CombatStyle.MELEE, AttackStyle.ACCURATE,  MeleeMaxHitCalculator.class,
		0.0f, 1.0, 12.0, 0.0),
	INQUISITORS(
		createEquipment(Map.of(
			EquipmentInventorySlot.HEAD.getSlotIdx(), new Item(ItemID.INQUISITORS_HELM, 1),
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.INQUISITORS_MACE, 1),
			EquipmentInventorySlot.BODY.getSlotIdx(), new Item(ItemID.INQUISITORS_BODY, 1),
			EquipmentInventorySlot.LEGS.getSlotIdx(), new Item(ItemID.INQUISITORS_SKIRT, 1)
		)),
		CombatStyle.MELEE, AttackStyle.AGGRESSIVE,  MeleeMaxHitCalculator.class,
		160.0f, 4.0, 39.0, 0.0),
	BARRONITE_INQUISITORS(
		createEquipment(Map.of(
			EquipmentInventorySlot.HEAD.getSlotIdx(), new Item(ItemID.INQUISITORS_HELM, 1),
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.BARRONITE_MACE, 1),
			EquipmentInventorySlot.BODY.getSlotIdx(), new Item(ItemID.INQUISITORS_BODY, 1),
			EquipmentInventorySlot.LEGS.getSlotIdx(), new Item(ItemID.INQUISITORS_SKIRT, 1)
		)),
		CombatStyle.MELEE, AttackStyle.AGGRESSIVE,  MeleeMaxHitCalculator.class,
		104.0f, 5.0, 35.0, 0.0, 412),
	FANG(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.OSMUMTENS_FANG, 1)
		)),
		CombatStyle.MELEE, AttackStyle.AGGRESSIVE,  MeleeMaxHitCalculator.class,
		181.0f, 5.0, 36.0, 42.0),
	DINHS(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.DINHS_BULWARK_ORNAMENT, 1)
		)),
		CombatStyle.MELEE, AttackStyle.ACCURATE,  MeleeMaxHitCalculator.class,
		129.0f, 3.0, 32.0, 0.0),
	DRAGONHUNTER_LANCE(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.DRAGONHUNTER_LANCE, 1)
		)),
		CombatStyle.MELEE, AttackStyle.CONTROLLED,  MeleeMaxHitCalculator.class,
		148.0f, 3.0, 43.0, 0.0, 260),
	DRAGONHUNTER_XBOW(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.DRAGONHUNTER_XBOW, 1),
			EquipmentInventorySlot.AMULET.getSlotIdx(), new Item(ItemID.NZONE_SALVE_AMULET_E, 1)
		)),
		CombatStyle.RANGED, AttackStyle.ACCURATE,  RangedMaxHitCalculator.class,
		141.0f, 5.0, 52.0, 0.0, 8060),
	SILVERLIGHT(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.AGRITH_SILVERLIGHT_DYED, 1)
		)),
		CombatStyle.MELEE, AttackStyle.AGGRESSIVE,  MeleeMaxHitCalculator.class,
		90.0f, 4.0, 41.0, 0.0, 3131),
	EMBERLIGHT(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.EMBERLIGHT, 1)
		)),
		CombatStyle.MELEE, AttackStyle.AGGRESSIVE,  MeleeMaxHitCalculator.class,
		91.0f, 5.0, 45.0, 0.0, 3131),
	SOUL_REAPER(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.SOULREAPER_AXE_ORN, 1)
		)),
		CombatStyle.MELEE, AttackStyle.AGGRESSIVE, MeleeMaxHitCalculator.class,
		195.0f, 5.0, 56.0, 0.0),
	MAX_OBSIDIAN(
		createEquipment(Map.of(
			EquipmentInventorySlot.HEAD.getSlotIdx(), new Item(ItemID.OBSIDIAN_HELMET, 1),
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.TZHAAR_SPLITSWORD, 1),
			EquipmentInventorySlot.BODY.getSlotIdx(), new Item(ItemID.OBSIDIAN_PLATEBODY, 1),
			EquipmentInventorySlot.LEGS.getSlotIdx(), new Item(ItemID.OBSIDIAN_PLATELEGS, 1),
			EquipmentInventorySlot.AMULET.getSlotIdx(), new Item(ItemID.JEWL_BESERKER_NECKLACE_ORNAMENT, 1)
		)),
		CombatStyle.MELEE, AttackStyle.AGGRESSIVE, MeleeMaxHitCalculator.class,
		103.0f, 3.0, 37.0, 0.0),
	MAX_DHAROK(
		createEquipment(Map.of(
			EquipmentInventorySlot.HEAD.getSlotIdx(), new Item(ItemID.BARROWS_DHAROK_HEAD, 1),
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.BARROWS_DHAROK_WEAPON, 1),
			EquipmentInventorySlot.BODY.getSlotIdx(), new Item(ItemID.BARROWS_DHAROK_BODY, 1),
			EquipmentInventorySlot.LEGS.getSlotIdx(), new Item(ItemID.BARROWS_DHAROK_LEGS, 1)
		)),
		CombatStyle.MELEE, AttackStyle.ACCURATE, MeleeMaxHitCalculator.class,
		157.0f, 3.0, 72.0, 0.0),
	ATLATL(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.ECLIPSE_ATLATL, 1)
		)),
		CombatStyle.RANGED, AttackStyle.ACCURATE, RangedMaxHitCalculator.class,
		99.0f, 3.0, 28.0, 0.0),
	BOWFA_AND_CRYSTAL(
		createEquipment(Map.of(
			EquipmentInventorySlot.HEAD.getSlotIdx(), new Item(ItemID.CRYSTAL_HELMET, 1),
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.BOW_OF_FAERDHINEN, 1),
			EquipmentInventorySlot.BODY.getSlotIdx(), new Item(ItemID.CRYSTAL_CHESTPLATE, 1),
			EquipmentInventorySlot.LEGS.getSlotIdx(), new Item(ItemID.CRYSTAL_PLATELEGS, 1)
		)),
		CombatStyle.RANGED, AttackStyle.ACCURATE, RangedMaxHitCalculator.class,
		124.0f, 4.0, 36.0, 0.0),
	CRYSTAL_AND_CRYSTAL(
		createEquipment(Map.of(
			EquipmentInventorySlot.HEAD.getSlotIdx(), new Item(ItemID.CRYSTAL_HELMET, 1),
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.NZONE_CRYSTAL_BOW_500, 1),
			EquipmentInventorySlot.BODY.getSlotIdx(), new Item(ItemID.CRYSTAL_CHESTPLATE, 1),
			EquipmentInventorySlot.LEGS.getSlotIdx(), new Item(ItemID.CRYSTAL_PLATELEGS, 1)
		)),
		CombatStyle.RANGED, AttackStyle.ACCURATE, RangedMaxHitCalculator.class,
		124.0f, 4.0, 36.0, 0.0),
	BOWFA_AND_CRYSTAL_COLORED(
		createEquipment(Map.of(
			EquipmentInventorySlot.HEAD.getSlotIdx(), new Item(ItemID.CRYSTAL_HELMET_AMLODD, 1),
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.BOW_OF_FAERDHINEN_INFINITE_AMLODD, 1),
			EquipmentInventorySlot.BODY.getSlotIdx(), new Item(ItemID.CRYSTAL_CHESTPLATE_AMLODD, 1),
			EquipmentInventorySlot.LEGS.getSlotIdx(), new Item(ItemID.CRYSTAL_PLATELEGS_AMLODD, 1)
		)),
		CombatStyle.RANGED, AttackStyle.ACCURATE, RangedMaxHitCalculator.class,
		121.0f, 3.0, 36.0, 0.0),
	MAX_RANGE_BALLISTA(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.HEAVY_BALLISTA, 1)
		)),
		CombatStyle.RANGED, AttackStyle.ACCURATE, RangedMaxHitCalculator.class,
		189.0f, 5.0, 43.0, 53.0),
	SCORCHING_BOW(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.SCORCHING_BOW, 1)
		)),
		CombatStyle.RANGED, AttackStyle.ACCURATE,  RangedMaxHitCalculator.class,
		127.0f, 5.0, 42.0, 0.0, 3131),
	PURGING_STAFF(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.PURGING_STAFF, 1)
		)),
		CombatStyle.MAGE, AttackStyle.CASTING, MagicSpell.DARK_DEMONBANE, MagicMaxHitCalculator.class,
		0.37f, 61.0, 61.0, 3131),
	DRAGONHUNTER_WAND_GREEN_DRAGON(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.DRAGONHUNTER_WAND, 1)
		)),
		CombatStyle.MAGE, AttackStyle.CASTING, MagicSpell.WATER_SURGE, MagicMaxHitCalculator.class,
		0.37f, 53.0, 56.0, 260), // DPS calculator will report 49 here but 53 is right (I think..)
	DRAGONHUNTER_WAND_VORKATH(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.DRAGONHUNTER_WAND, 1),
			EquipmentInventorySlot.AMULET.getSlotIdx(), new Item(ItemID.NZONE_SALVE_AMULET_E, 1)
		)),
		CombatStyle.MAGE, AttackStyle.CASTING, MagicSpell.FIRE_SURGE, MagicMaxHitCalculator.class,
		0.32f, 59.0, 59.0, 8060),
	VIRTUS_MAGE(
	createEquipment(Map.of(
		EquipmentInventorySlot.HEAD.getSlotIdx(), new Item(ItemID.VIRTUS_MASK, 1),
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.STAFF_OF_ZAROS, 1),
			EquipmentInventorySlot.BODY.getSlotIdx(), new Item(ItemID.VIRTUS_TOP, 1),
			EquipmentInventorySlot.LEGS.getSlotIdx(), new Item(ItemID.VIRTUS_LEGS, 1)
		)),
	CombatStyle.MAGE, AttackStyle.CASTING, MagicSpell.ICE_BARRAGE, MagicMaxHitCalculator.class,
	0.24f, 39.0, 39.0, 0.0),
	MAX_MAGIC_DART(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.SLAYER_STAFF, 1)
		)),
		CombatStyle.MAGE, AttackStyle.CASTING, MagicSpell.MAGIC_DART,MagicMaxHitCalculator.class,
		0.27f, 12.0, 24.0, 0.0),
	WARPED_SCEPTRE(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.WARPED_SCEPTRE, 1)
		)),
		CombatStyle.MAGE, AttackStyle.ACCURATE, MagicMaxHitCalculator.class,
		0.27f, 2.0, 30.0, 0.0),
	BONE_STAFF(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.RAT_BONE_STAFF, 1)
		)),
		CombatStyle.MAGE, AttackStyle.ACCURATE, MagicMaxHitCalculator.class,
		0.27f, 6.0, 48.0, 0.0),
	SANGUINE_STAFF(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.SANGUINESTI_STAFF_OR, 1)
		)),
		CombatStyle.MAGE, AttackStyle.ACCURATE, MagicMaxHitCalculator.class,
		0.27f, 6.0, 40.0, 0.0),
	EYE_OF_AYAK(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.EYE_OF_AYAK, 1)
		)),
		CombatStyle.MAGE, AttackStyle.ACCURATE, MagicMaxHitCalculator.class,
		0.27f, 1.0, 34.0, 44.0),
	SHADOW(
		createEquipment(Map.of(
			EquipmentInventorySlot.WEAPON.getSlotIdx(), new Item(ItemID.TUMEKENS_SHADOW, 1)
		)),
		CombatStyle.MAGE, AttackStyle.ACCURATE, MagicMaxHitCalculator.class,
		0.81f, 2.0, 68.0, 0.0);


	private static Item[] createEquipment(Map<Integer, Item> itemMap) {
		final Item[] equipment = new Item[14];
		itemMap.forEach((slot, item) -> {
			if (slot >= 0 && slot < equipment.length) {
				equipment[slot] = item;
			}
		});
		return equipment;
	}

	private final Item[] equippedItems;
	private final CombatStyle combatStyle;
	private final AttackStyle attackStyle;
	private final MagicSpell magicSpell;
	private final Class maxHitCalculatorClass;
	private final float strengthBonus;
	private final double standardMaxHit;
	private final double maxedMaxHit;
	private final double maxedSpecialHit;
	private final int npcId;

	PlayerSetup(Item[] equippedItems,
					   CombatStyle combatStyle,
					   AttackStyle attackStyle,
					   Class maxHitCalculatorClass,
					   float strengthBonus,
					   double standardMaxHit,
					   double maxedMaxHit,
					   double maxedSpecialHit)
	{
		this.equippedItems = equippedItems;
		this.combatStyle = combatStyle;
		this.attackStyle = attackStyle;
		this.magicSpell = null;
		this.maxHitCalculatorClass = maxHitCalculatorClass;
		this.strengthBonus = strengthBonus;
		this.standardMaxHit = standardMaxHit;
		this.maxedMaxHit = maxedMaxHit;
		this.maxedSpecialHit = maxedSpecialHit;
		this.npcId = -1;
	}

	PlayerSetup(Item[] equippedItems,
				CombatStyle combatStyle,
				AttackStyle attackStyle,
				Class maxHitCalculatorClass,
				float strengthBonus,
				double standardMaxHit,
				double maxedMaxHit,
				double maxedSpecialHit,
				int npcId)
	{
		this.equippedItems = equippedItems;
		this.combatStyle = combatStyle;
		this.attackStyle = attackStyle;
		this.magicSpell = null;
		this.maxHitCalculatorClass = maxHitCalculatorClass;
		this.strengthBonus = strengthBonus;
		this.standardMaxHit = standardMaxHit;
		this.maxedMaxHit = maxedMaxHit;
		this.maxedSpecialHit = maxedSpecialHit;
		this.npcId = npcId;
	}

	PlayerSetup(Item[] equippedItems,
				CombatStyle combatStyle,
				AttackStyle attackStyle,
				MagicSpell magicSpell,
				Class maxHitCalculatorClass,
				float strengthBonus,
				double standardMaxHit,
				double maxedMaxHit,
				double maxedSpecialHit)
	{
		this.equippedItems = equippedItems;
		this.combatStyle = combatStyle;
		this.attackStyle = attackStyle;
		this.magicSpell = magicSpell;
		this.maxHitCalculatorClass = maxHitCalculatorClass;
		this.strengthBonus = strengthBonus;
		this.standardMaxHit = standardMaxHit;
		this.maxedMaxHit = maxedMaxHit;
		this.maxedSpecialHit = maxedSpecialHit;
		this.npcId = -1;
	}

	PlayerSetup(Item[] equippedItems,
				CombatStyle combatStyle,
				AttackStyle attackStyle,
				MagicSpell magicSpell,
				Class maxHitCalculatorClass,
				float strengthBonus,
				double standardMaxHit,
				double maxedMaxHit,
				int npcId)
	{
		this.equippedItems = equippedItems;
		this.combatStyle = combatStyle;
		this.attackStyle = attackStyle;
		this.magicSpell = magicSpell;
		this.maxHitCalculatorClass = maxHitCalculatorClass;
		this.strengthBonus = strengthBonus;
		this.standardMaxHit = standardMaxHit;
		this.maxedMaxHit = maxedMaxHit;
		this.maxedSpecialHit = 0.0;
		this.npcId = npcId;
	}

}
