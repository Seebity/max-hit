package com.maxhit.calculators;

import com.maxhit.MaxHitPlugin;
import com.maxhit.NextMaxHitReqs;
import com.maxhit.PrayerType;
import com.maxhit.equipment.DemonbaneWeapon;
import com.maxhit.equipment.EquipmentFunctions;
import com.maxhit.monsters.DemonicMonsters;
import com.maxhit.monsters.MonsterFlatArmour;
import com.maxhit.sets.EquipmentSet;
import com.maxhit.styles.AttackStyle;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarPlayerID;
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
    protected double voidBonus;
	protected double avariceBonus;
	protected double salveBonus;
	protected double slayerBonus;
	protected double dragonbaneBonus;
	protected double demonbaneBonus;
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
		prayerBonus = 1.0;
		voidBonus = 1.0;
		avariceBonus = 1.0;
		salveBonus = 1.0;
		slayerBonus = 1.0;
		dragonbaneBonus = 1.0;
		demonbaneBonus = 1.0;
		flatArmour = 0.0;
		updatePrayerBonus();
		updateEffectiveLevel();
		updateStyleBonus();
		updateVoidBonus();
	}

	protected abstract void updateStyleBonus();

	protected void updateVoidBonus()
	{
		voidBonus = bonusCalculator.getVoidBonus();
		if (skill == Skill.MAGIC)
			voidBonus -= 1.0;
	}

	protected void updateEffectiveStrength()
	{
		// getValue() gets boosted value
		effectiveStrength = Math.floor((effectiveLevel + styleBonus + 8) * voidBonus);
	}

	protected void updatePrayerBonus()
	{
		prayerBonus = bonusCalculator.getPrayerBonus();
	}

	protected void updateEffectiveLevel()
	{
		effectiveLevel = client.getBoostedSkillLevel(this.skill);

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

		effectiveLevel = Math.floor(effectiveLevel * prayerBonus);
	}

    protected void updateStrengthBonus()
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

	protected void updateFlatArmour()
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

	public void getAvariceBonus()
	{
		if (!EquipmentFunctions.GetEquippedItemString(client, equippedItems, EquipmentInventorySlot.AMULET).contains(("Avarice")))
		{
			return;
		}
		NPC npc = (NPC) opponent;
		if (npc == null)
		{
			return;
		}
		String npcName = npc.getName();
		if (npcName == null)
		{
			return;
		}
		if (!npc.getName().contains("Revenant"))
		{
			return;
		}
		if (client.getVarbitValue(VarPlayerID.REVENANT_REWARD_TIME_REMAINING) > 0)
		{
			avariceBonus = 1.35;
			return;
		}
		avariceBonus = 1.2;
	}

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

	/**
	 * Multiplies base by factor
	 * @param base base value
	 * @param factor multiplier factor
	 * @throws AssertionError if factor is less than 1.0
	 * @return Math.max(0.0, Math.floor(base * factor))
	 */
	public double factor(double base, double factor)
	{
		assert factor >= 1.0;
		return Math.max(0.0, Math.floor(base * factor));
	}

	/**
	 * Multiplies base by factor (factor must be <= 1.0)
	 * Then adds previous factor to base
	 * @param base base value
	 * @param factor multiplier factor
	 * @throws AssertionError if factor is greater than 1.0
	 * @return base + Math.floor(base * addedFactor)
	 */
	public double addFactor(double base, double factor)
	{
		assert factor <= 1.0;
		double addedFactor = Math.max(0.0, Math.floor(base * factor));
		return base + addedFactor;
	}

	protected void getSalveBonus()
	{
		salveBonus = bonusCalculator.getSalveBonus(equippedItems, opponent);
	}

	protected void getSlayerBonus() {slayerBonus = bonusCalculator.getSlayerBonus(this.plugin, equippedItems, opponent);}

	protected abstract void calculateNextMaxHitRequirements();
}
