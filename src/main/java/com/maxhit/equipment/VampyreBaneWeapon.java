package com.maxhit.equipment;

import com.maxhit.monsters.Vampyres;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemVariationMapping;

@AllArgsConstructor
public enum VampyreBaneWeapon
{
	// Silver weaponry
	BLESSED_AXE(ItemID.ANMA_AXE, 1, 1.1),
	SILVER_SICKLE(ItemID.SILVER_SICKLE, 1, 1.1),
	// For some reason these sickles aren't variation mapped
	EMERALD_SICKLE(ItemID.SILVER_SICKLE_EMERALD, 1, 1.1),
	EMERALD_SICKLE_BLESSED(ItemID.SILVER_SICKLE_ENCHANTED, 1, 1.1),
	RUBY_SICKLE(ItemID.SILVER_SICKLE_RUBY, 1, 1.1),
	RUBY_SICKLE_BLESSED(ItemID.SILVER_SICKLE_RUBY_ENCHANTED, 1, 1.1),
	DIAMOND_SICKLE(ItemID.SILVER_SICKLE_DIAMOND, 1, 1.1),
	DIAMOND_SICKLE_BLESSED(ItemID.SILVER_SICKLE_DIAMOND_ENCHANTED, 1, 1.1),
	WOLFBANE(ItemID.DAGGER_WOLFBANE, 1, 1.1),
	SILVERLIGHT(ItemID.SILVERLIGHT, 1,1.1),
	DARKLIGHT(ItemID.DARKLIGHT, 1, 1.1),
	ARCLIGHT(ItemID.ARCLIGHT, 1,1.1),
	EMBERLIGHT(ItemID.EMBERLIGHT, 1,1.1),
	// Tier 2 weaponry
	ROD_OF_IVANDIS(ItemID.BURGH_ROD_COMMAND_FINAL_10, 2, 1.1),
	// Tier 3 weaponry
	BLISTERWOOD_SICKLE(ItemID.BLISTERWOOD_SICKLE, 3, 1.15),
	BLISTERWOOD_FLAIL(ItemID.BLISTERWOOD_FLAIL, 3, 1.25),
	BLISTERWOOD_STAKE(ItemID.BLISTERWOOD_STAKE, 3, 1.25),
	IVANDIS_FLAIL(ItemID.IVANDIS_FLAIL, 3, 1.2),
	HALLOWED_FLAIL(ItemID.HALLOWED_FLAIL, 3, 1.25),
	SUNSPEAR(ItemID.SUNSPEAR, 3, 1.5),
	;

	@Getter
	private final int itemId;
	@Getter
	private final int vampyreLevelLimit;
	@Getter
	private final double damageMultiplier;

	public static double GetDamageMultiplier(Client client, ItemContainer equippedItems, int level)
	{
		// At this point, we've already established we're attacking a vampyre via MaxHitCalculator
		// So don't need to check

		// Iterate over enum
		for (VampyreBaneWeapon weapon : values()) {

			// Get variations
			int baseId = ItemVariationMapping.map(weapon.itemId);
			Collection<Integer> variations = ItemVariationMapping.getVariations(baseId);

			for (Integer itemId : variations)
			{
				if (!EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, itemId))
				{
					continue;
				}

				// For tier 3 vampyres, weapon must be at that tier
				// Return 1.0 for now, and Max Hit Calculator will adjust at the end
				if (level == 3 && weapon.getVampyreLevelLimit() < 3)
				{
					return 0.0;
				}

				// For tier 2 weapons, need to use silver weaponry
				if (level == 2 && weapon.getVampyreLevelLimit() == 1)
				{
					return 1.0;
				}

				return weapon.getDamageMultiplier();
			}
		}

		if (level == 1)
		{
			return 1.0;
		}

		return 0.0;
	}
}
