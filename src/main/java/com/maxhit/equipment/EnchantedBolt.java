package com.maxhit.equipment;

import com.maxhit.monsters.FieryMonsters;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Actor;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemContainer;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;


@AllArgsConstructor
public enum EnchantedBolt
{
	DIAMOND("Diamond", 1.15, 1.26),
	ONYX("Onyx", 1.2, 1.32),
	DRAGONSTONE("Dragonstone", -1.0, -1.0)
	{
		@Override
		public double getDamageMultiplier(double baseMaxHit, int rangedLevel, int npcId)
		{
			return baseMaxHit + Math.floor(rangedLevel * 0.2);
		}
		@Override
		public double getZaryteDamageMultiplier(double baseMaxHit, int rangedLevel, int npcId)
		{
			return baseMaxHit + Math.floor(rangedLevel * 0.22);
		}
	},
	RUBY("Ruby", -1.0, -1.0)
		{
			@Override
			public double getDamageMultiplier(double baseMaxHit, int rangedLevel, int npcId)
			{
				return 100;
			}
			@Override
			public double getZaryteDamageMultiplier(double baseMaxHit, int rangedLevel, int npcId)
			{
				return 110;
			}
		},
	PEARL("Pearl", -1.0, -1.0)
	{
		@Override
		public double getDamageMultiplier(double baseMaxHit, int rangedLevel, int npcId)
		{
			if (FieryMonsters.ID_LIST.contains(npcId))
			{
				return Math.floor(baseMaxHit) + Math.floor(rangedLevel * (1.0 / 15.0));
			}
			return Math.floor(baseMaxHit) + Math.floor(rangedLevel * (1.0 / 20.0));
		}
		@Override
		public double getZaryteDamageMultiplier(double baseMaxHit, int rangedLevel, int npcId)
		{
			if (FieryMonsters.ID_LIST.contains(npcId))
			{
				return Math.floor(baseMaxHit) + Math.floor(rangedLevel * (1.0 / 13.0));
			}
			return Math.floor(baseMaxHit) + Math.floor(rangedLevel * (1.0 / 18.0));
		}
	},
	OPAL("Opal", -1.0, -1.0)
	{
		@Override
		public double getDamageMultiplier(double baseMaxHit, int rangedLevel, int npcId)
		{
			return Math.floor(baseMaxHit) + Math.floor(rangedLevel * (1.0 / 10.0));
		}
		@Override
		public double getZaryteDamageMultiplier(double baseMaxHit, int rangedLevel, int npcId)
		{
			return Math.floor(baseMaxHit) + Math.floor(rangedLevel * (1.0 / 9.0));
		}
	}
	//TODO add logic for Pearl Bolts
	;
	@Getter
	private final String name;
	private final double regularDamageMultiplier;
	private final double zaryteDamageMultiplier;

	public double getDamageMultiplier(double baseMaxHit, int rangedLevel, int npcId)
	{
		return Math.floor(baseMaxHit * regularDamageMultiplier);
	}

	public double getZaryteDamageMultiplier(double baseMaxHit, int rangedLevel, int npcId)
	{
		return Math.floor(baseMaxHit * zaryteDamageMultiplier);
	}

	public boolean isEquipped(Client client, ItemContainer equippedItems)
	{
		String ammoName = EquipmentFunctions.GetEquippedItemString(client, equippedItems, EquipmentInventorySlot.AMMO);
		return ammoName.contains(name) && ammoName.contains("(e)");
	}
}
