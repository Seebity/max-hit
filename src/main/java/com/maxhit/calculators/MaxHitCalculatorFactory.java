package com.maxhit.calculators;

import com.maxhit.MaxHitPlugin;
import com.maxhit.styles.AttackStyle;
import com.maxhit.styles.CombatStyle;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;

public class MaxHitCalculatorFactory
{
	private final MaxHitPlugin plugin;
    private final Client client;
    private final ItemManager itemManager;

    public MaxHitCalculatorFactory(MaxHitPlugin plugin, Client client, ItemManager itemManager)
    {
		this.plugin = plugin;
        this.client = client;
        this.itemManager = itemManager;
    }
    public MaxHitCalculator create(CombatStyle combatStyle, AttackStyle attackStyle)
    {
        if (combatStyle == CombatStyle.MELEE)
            return new MeleeMaxHitCalculator(this.plugin, this.client, this.itemManager, attackStyle);
        if (combatStyle == CombatStyle.RANGED)
            return new RangedMaxHitCalculator(this.plugin, this.client, this.itemManager, attackStyle);
        if (combatStyle == CombatStyle.MAGE)
            return new MagicMaxHitCalculator(this.plugin, this.client, this.itemManager, attackStyle);
        return  null;
    }
}
