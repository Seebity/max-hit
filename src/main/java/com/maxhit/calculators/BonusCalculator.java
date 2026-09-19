package com.maxhit.calculators;

import com.maxhit.MaxHitPlugin;
import com.maxhit.PrayerType;
import com.maxhit.Prayers;
import com.maxhit.equipment.EquipmentFunctions;
import com.maxhit.equipment.SalveAmulet;
import com.maxhit.monsters.UndeadMonsters;
import com.maxhit.sets.EliteVoidSet;
import com.maxhit.sets.VoidSet;
import java.util.Map;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BonusCalculator
{


	private final VoidSet voidSet;
	private final EliteVoidSet eliteVoidSet;
	private final Client client;
	private final Skill skill;

	public BonusCalculator(Client client, Skill skill)
	{
		this.client = client;
		this.skill = skill;
		voidSet = new VoidSet(client, skill);
		eliteVoidSet = new EliteVoidSet(client, skill);
	}

	public double getSalveBonus(ItemContainer equippedItems, Actor opponent)
	{
		// Check if opponent is undead
		NPC npc = (NPC) opponent;

		if (npc == null)
		{
			return 0.0;
		}

		if (!UndeadMonsters.ID_LIST.contains(npc.getId()))
		{
			return 0.0;
		}

		for (SalveAmulet amuletType : SalveAmulet.values())
		{
			if (amuletType.isEquipped(equippedItems))
			{
				return amuletType.getBonus(skill);
			}
		}
		return 0.0;
	}

	public double getVoidBonus()
	{
		if (voidSet.isWearingSet())
			return voidSet.getMultiplier();

		if (eliteVoidSet.isWearingSet())
			return eliteVoidSet.getMultiplier();
		return 1.0;
	}

	public double getPrayerBonus()
	{
		switch (skill)
		{
			case STRENGTH:
				for (Map.Entry<PrayerType, Double> entry : Prayers.STRENGTH_PRAYERS.entrySet())
				{
					if (entry.getKey().isActive(client))
					{
						return entry.getValue(); // Return the first active prayer found
					}
				}
				break;
			case RANGED:
				for (Map.Entry<PrayerType, Double> entry : Prayers.RANGED_PRAYERS.entrySet())
				{
					if (entry.getKey().isActive(client))
					{
						return entry.getValue(); // Return the first active prayer found
					}
				}
				break;
			case MAGIC:
				for (Map.Entry<PrayerType, Double> entry : Prayers.MAGIC_PRAYERS.entrySet())
				{
					if (entry.getKey().isActive(client))
					{
						return entry.getValue(); // Return the first active prayer found
					}
				}
				return 0.0;
		}
		return 1.0;
	}

	public double getSlayerBonus(MaxHitPlugin plugin, ItemContainer equippedItems, Actor opponent)
	{
		// Check if opponent is in slayer task
		NPC npc = (NPC) opponent;

		if (npc == null)
		{
			return 0.0;
		}

		if (!plugin.isTarget(npc))
		{
			return 0.0;
		}

		// Check head slot

		Item headSlot = equippedItems.getItem(EquipmentInventorySlot.HEAD.ordinal());

		if (headSlot == null)
		{
			return 0.0;
		}

		String headSlotName = EquipmentFunctions.GetEquippedItemString(client, equippedItems, EquipmentInventorySlot.HEAD).toLowerCase();

		// Check for black mask
		if (headSlotName.contains("black mask") ||  headSlotName.contains("slayer helmet"))
		{
			switch (skill)
			{
				case STRENGTH:
					return 0.1667;
				case RANGED:
				case MAGIC:
					if (headSlotName.contains("(i)"))
					{
						return 0.15;
					}
					else
					{
						return 0.0;
					}
			}
		}

		return 0.0;
	}
}
