package com.maxhit;

import com.maxhit.calculators.SpecialAttackCalculator;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import javax.inject.Inject;
import java.util.HashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Prayer;
import net.runelite.api.Projectile;
import net.runelite.api.Skill;
import net.runelite.api.Actor;
import net.runelite.api.Hitsplat;
import net.runelite.api.NPC;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import com.maxhit.calculators.MaxHitCalculator;
import com.maxhit.calculators.MaxHitCalculatorFactory;
import com.maxhit.styles.AttackStyle;
import com.maxhit.styles.StyleFactory;
import com.maxhit.styles.CombatStyle;



@PluginDescriptor(
	name = "Max Hit",
	description = "Displays current max hit",
	enabledByDefault = true,
	tags = {"max, hit, spec, pvp, magic, spell, combat, gear"}
)

@Slf4j
public class MaxHitPlugin extends Plugin
{

	private static final int WEAPON_SPECIAL_REQS = 906;
	private static final Duration WAIT = Duration.ofSeconds(5);


	@Inject
	private Client client;
	@Inject
	private ClientThread clientThread;
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private ItemManager itemManager;
	@Inject
	private MaxHitOverlay myOverlay;
	@Inject
	private MaxHitConfig config;

	@Provides
	MaxHitConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MaxHitConfig.class);
	}

	private Instant lastTime;
	private ItemContainer equippedItems;
	private MaxHitCalculatorFactory maxHitCalculatorFactory;
	@Getter
	private MaxHitCalculator maxHitCalculator;
	@Getter
	private SpecialAttackCalculator specialAttackCalculator;
	//Might be better to break this out into own class
	@Getter
	private boolean isWieldingSpecialAttackWeapon = false;
	@Getter
	public HashMap<String, InventoryWeapon> map;

	private Actor interactingTarget;

	private Projectile projectile;

	@Getter
	private MagicSpell activeSpell;

	@Override
	public void startUp() throws Exception
	{
		overlayManager.add(myOverlay);
		clientThread.invokeLater(() ->
		{
			maxHitCalculatorFactory = new MaxHitCalculatorFactory(this, client, itemManager);
			specialAttackCalculator = new SpecialAttackCalculator(client);
			if (!client.getGameState().equals(GameState.LOGGED_IN))
			{
				return;
			}

			equippedItems = client.getItemContainer(InventoryID.WORN);
			if (equippedItems == null)
			{
				return;
			}
			// Safe to assume combat Varbits have been set here?
			replaceCalculatorAndMaxHit();

		});
	}

	@Override
	public void shutDown() throws Exception
	{
		overlayManager.remove(myOverlay);
		lastTime = null;
		isWieldingSpecialAttackWeapon = false;
		maxHitCalculatorFactory = null;
		specialAttackCalculator = null;
		maxHitCalculator = null;
		activeSpell = null;
	}

	@Subscribe
	public void onItemContainerChanged(final ItemContainerChanged event)
	{
		//If equipment is changed, recalculate
		if (event.getItemContainer() != client.getItemContainer(InventoryID.WORN))
		{
			return;
		}

		if (maxHitCalculator == null)
		{
			return;
		}
		equippedItems = event.getItemContainer();
		maxHitCalculator.setEquippedItems(equippedItems);
		maxHitCalculator.calculateMaxHit();
		specialAttackCalculator.setEquippedItems(equippedItems);
		checkIsWieldingSpecialAttackWeapon();
	}

	//Update on stat change
	@Subscribe
	public void onStatChanged(StatChanged event)
	{
		// Called upon XP gain, level gained, or boosted level gained
		// Question is, should this be called every time i.e. every attack or just upon level up?
		if (maxHitCalculator == null)
		{
			return;
		}
		Skill[] skills = {
			Skill.STRENGTH, Skill.RANGED, Skill.MAGIC, Skill.HITPOINTS
		};
		for (Skill skill : skills)
		{
			if (event.getSkill() != maxHitCalculator.getSkill())
			{
				continue;
			}
			maxHitCalculator.calculateMaxHit();
			return;
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		// COM_MODE = Attack Style
		// COMBAT_WEAPON_CATEGORY = Weapon Style
		if (event.getVarpId() == VarPlayerID.COM_MODE
			|| event.getVarbitId() == VarPlayerID.COM_STANCE
			|| event.getVarbitId() == VarbitID.COMBAT_WEAPON_CATEGORY
			|| event.getVarbitId() == VarbitID.AUTOCAST_DEFMODE)
		{
			replaceCalculatorAndMaxHit();
		}
		else if (event.getVarbitId() == VarbitID.PRAYER_ALLACTIVE)
		{
			// Only re-calculate for select prayers
			for (PrayerType prayer : PrayerType.values())
			{
				if (!prayer.isActive(client))
				{
					continue;
				}
				maxHitCalculator.calculateMaxHit();
				return;
			}
		}
		else if (event.getVarbitId() == VarbitID.AUTOCAST_SPELL)
		{
			maxHitCalculator.calculateMaxHit();
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		// Check if user has reset
		if (!config.resetMaxHit()) return;

		if (maxHitCalculator.opponent  != null
			&& lastTime != null
			&& client.getLocalPlayer().getInteracting() == null)
		{
			if (Duration.between(lastTime, Instant.now()).compareTo(WAIT) > 0)
			{
				activeSpell = null;
				maxHitCalculator.opponent  = null;
				maxHitCalculator.calculateMaxHit();
			}
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
	{
		if (maxHitCalculator == null)
		{
			return;
		}
		Actor actor = hitsplatApplied.getActor();
		Hitsplat hitsplat = hitsplatApplied.getHitsplat();
		if (actor == null)
		{
			return;
		}
		if (!(actor instanceof NPC))
		{
			return;
		}
		if (hitsplat.isOthers())
		{
			return;
		}
		lastTime = Instant.now();
		maxHitCalculator.opponent = actor;
		maxHitCalculator.calculateMaxHit();
	}

	@Subscribe
	public void onInteractingChanged(InteractingChanged event)
	{
		final Actor source = event.getSource();

		if (source == null)
			return;

		if (source != client.getLocalPlayer())
			return;

		if (event.getTarget() == null)
			return;

		interactingTarget = event.getTarget();
	}

	@Subscribe
	public void onProjectileMoved(ProjectileMoved event)
	{
		final Projectile eventProjectile = event.getProjectile();

		if (eventProjectile == null)
			return;

		// Get the actor who the projectile is going to
		Actor eventTarget = eventProjectile.getTargetActor();

		if (eventTarget == null)
			return;

		// Check that event target is who we're interacting with
		if (eventTarget != interactingTarget)
			return;

		// Okay, we've found a match for our target
		// Now Iterate over standard spellbook spells looking to see if the projectile is a spell
		for (MagicSpell spell : MagicSpell.values())
		{
			// Skip non-standard spells
			if (spell.getSpellbook() != Spellbook.STANDARD)
				continue;

			// Looks for matching id
			if (spell.getProjectileId() != eventProjectile.getId())
			{
				continue;
			}
			// Match has been found, assuming we're attacking the target at this point

			// Check if projectile has been set
			if (projectile == null)
			{
				projectile = eventProjectile;
				activeSpell = spell;
				maxHitCalculator.opponent = interactingTarget;
				maxHitCalculator.calculateMaxHit();
				return;
			}

			// Don't need to set projectile if we already have the same projectile
			if (projectile.getId() == eventProjectile.getId())
			{
				return;
			}

			projectile = eventProjectile;
			activeSpell = spell;
			maxHitCalculator.opponent = interactingTarget;
			maxHitCalculator.calculateMaxHit();
		}
	}

	private void replaceCalculatorAndMaxHit()
	{
		AttackStyle attackStyle = StyleFactory.getAttackStyle(client);
		if (attackStyle == null || attackStyle == AttackStyle.OTHER)
		{
			return;
		}

		CombatStyle combatStyle = StyleFactory.getCombatType(attackStyle);
		maxHitCalculator = maxHitCalculatorFactory.create(combatStyle, attackStyle);
		maxHitCalculator.calculateMaxHit();
	}

	private void checkIsWieldingSpecialAttackWeapon()
	{
		if (equippedItems == null)
		{
			return;
		}
		Item weapon = equippedItems.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
		if (weapon == null)
		{
			isWieldingSpecialAttackWeapon = false;
			return;
		}
		isWieldingSpecialAttackWeapon = client.getEnum(WEAPON_SPECIAL_REQS).getIntValue(weapon.getId()) > 0;
	}
}