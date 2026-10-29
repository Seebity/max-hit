package com.maxhit.calculators;

import com.maxhit.MaxHitPlugin;
import com.maxhit.NextMaxHitReqs;
import com.maxhit.PrayerType;
import com.maxhit.monsters.MonsterFlatArmour;
import com.maxhit.sets.EquipmentSet;
import com.maxhit.styles.AttackStyle;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.game.ItemManager;

public abstract class MaxHitCalculator
{
	protected final MaxHitPlugin plugin;
    protected final Client client;
    protected final ItemManager itemManager;
	protected ItemContainer equippedItems;
	@Getter
    protected Skill skill;
	protected double effectiveLevel;
	@Setter
	protected AttackStyle attackStyle;
	protected BonusCalculator bonusCalculator;
    protected double effectiveStrength;
    protected double strengthBonus;
    protected double prayerBonus;
    protected double styleBonus;
	protected double salveBonus;
	protected double slayerBonus;
	protected double dragonbaneBonus;
	protected double demonbaneBonus;
    protected double voidBonus;
	protected double flatArmour;

	@Nullable
    public Actor opponent;
	@Getter
    protected double maxHit;
	@Getter
	protected NextMaxHitReqs nextMaxHitReqs;

	@Inject
    protected MaxHitCalculator (MaxHitPlugin plugin, Client client, ItemManager itemManager, Skill skill, AttackStyle attackStyle)
    {
		this.plugin = plugin;
		this.client = client;
		this.itemManager = itemManager;
        this.skill = skill;
		this.attackStyle = attackStyle;
		this.equippedItems = client.getItemContainer(InventoryID.WORN);
		EquipmentSet.setEquippedItems(equippedItems);
		bonusCalculator = new BonusCalculator(client, skill);
        maxHit = 0.0;
        voidBonus = 1.0;
		salveBonus = 0.0;
		flatArmour = 0.0;
    }

	public void setEquippedItems(ItemContainer equippedItems)
	{
		this.equippedItems = equippedItems;
		EquipmentSet.setEquippedItems(equippedItems);
	}

	protected void reset()
	{
		styleBonus = 0.0;
		voidBonus = 1.0;
		prayerBonus = 1.0;
		demonbaneBonus = 1.0;
		flatArmour = 0.0;
		dragonbaneBonus = 0.0;
		demonbaneBonus = 0.0;
		getEffectiveLevel();
	}

	protected abstract void getStyleBonus();

	protected void getVoidBonus()
	{
		voidBonus = bonusCalculator.getVoidBonus();
		if (skill == Skill.MAGIC)
			voidBonus -= 1.0;
	}

	protected void getEffectiveStrength()
	{
		getStyleBonus();
		getVoidBonus();

		// getValue() gets boosted value
		effectiveStrength = Math.floor((effectiveLevel + styleBonus + 8) * voidBonus);
	}

	protected void getPrayerBonus()
	{
		prayerBonus = bonusCalculator.getPrayerBonus();
	}

	protected void getEffectiveLevel()
	{
		effectiveLevel = client.getBoostedSkillLevel(this.skill);
		getPrayerBonus();

		// First Melee and Ranged Prayers always increment by at least one
		if (skill == Skill.STRENGTH)
		{
			if (PrayerType.BURST_OF_STRENGTH.isActive(client) && effectiveLevel <= 20)
			{
				effectiveLevel += 1;
				return;
			}
		}
		else if (skill == Skill.RANGED)
		{
			if (PrayerType.SHARP_EYE.isActive(client) && effectiveLevel <= 20)
			{
				effectiveLevel += 1;
				return;
			}
		}
		else if (skill == Skill.MAGIC)
		{
			// Can stop here for magic
			return;
		}

		effectiveLevel = (int) Math.floor(effectiveLevel * prayerBonus);
	}

    protected void getStrengthBonus()
	{
		strengthBonus = StrengthBonusCalculator.getStrengthBonus(equippedItems, itemManager, skill);
	}

	public int getNpcId()
	{
		if (opponent == null)
		{
			return -1;
		}

		NPC npc = (NPC) opponent;
		String npcName = npc.getName();

		if (npcName == null)
		{
			return -1;
		}

		return npc.getId();
	}

	protected void getFlatArmour()
	{
		flatArmour = 0.0;

		int npcId = getNpcId();

		if (npcId == -1)
			return;

		for (MonsterFlatArmour monster : MonsterFlatArmour.values())
		{
			if (monster.getId() != npcId)
			{
				continue;
			}

			flatArmour = monster.getFlatArmour();
			return;
		}
	}

    public abstract void calculateMaxHit();

	public abstract void getDragonbaneBonus();

	public abstract void getDemonbaneBonus();
	public void getDemonbaneBonus()
	{
		// Check if opponent is a demon
		int npcId = getNpcId();

		if (npcId == -1)
		{
			demonbaneBonus = 0.0;
			return;
		}

		if (!DemonicMonsters.ID_LIST.contains(npcId))
		{
			demonbaneBonus = 0.0;
			return;
		}

		for (DemonbaneWeapon weapon : DemonbaneWeapon.values())
		{
			// Make sure skill matches
			if (weapon.getSkill() != this.skill)
				continue;

			if (EquipmentFunctions.HasEquipped(equippedItems, EquipmentInventorySlot.WEAPON, weapon.getItemId()))
			{
				demonbaneBonus = weapon.getDamageMultiplier();
				return;
			}
		}

		demonbaneBonus = 0.0;
	}

	protected void getSalveBonus()
	{
		salveBonus = bonusCalculator.getSalveBonus(equippedItems, opponent);
	}

	protected void getSlayerBonus() {slayerBonus = bonusCalculator.getSlayerBonus(this.plugin, equippedItems, opponent);}

	protected abstract void calculateNextMaxHitRequirements();
}
