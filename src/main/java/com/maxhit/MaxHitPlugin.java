package com.maxhit;

import com.google.common.annotations.VisibleForTesting;
import com.maxhit.calculators.SpecialAttackCalculator;
import com.maxhit.slayer.Task;
import com.maxhit.slayer.TaskLocation;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.IntStream;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPCComposition;
import net.runelite.api.Projectile;
import net.runelite.api.Skill;
import net.runelite.api.Actor;
import net.runelite.api.Hitsplat;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.gameval.DBTableID;
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

	@Getter
	private final List<NPC> targets = new ArrayList<>();

	@Getter
	@Setter
	private int amount;

	@Getter
	@Setter
	private int initialAmount;

	@Getter
	@Setter
	private String taskLocation;

	@Getter
	@Setter
	private String taskName;

	private Instant infoTimer;
	private boolean loginFlag;
	private final List<Pattern> targetNames = new ArrayList<>();
	private int regionID = -1;

	@Override
	public void startUp() throws Exception
	{
		overlayManager.add(myOverlay);

		if (client.getGameState() == GameState.LOGGED_IN)
		{
			loginFlag = true;
			clientThread.invoke(this::updateTask);
		}


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
		targets.clear();
		lastTime = null;
		isWieldingSpecialAttackWeapon = false;
		maxHitCalculatorFactory = null;
		specialAttackCalculator = null;
		maxHitCalculator = null;
		activeSpell = null;
	}

	private void updateTask()
	{
		int amount = client.getVarpValue(VarPlayerID.SLAYER_COUNT);
		if (amount > 0)
		{
			int taskId = client.getVarpValue(VarPlayerID.SLAYER_TARGET);

			int taskDBRow;
			if (taskId == 98 /* Bosses, from [proc,helper_slayer_current_assignment] */)
			{
				var bossRows = client.getDBRowsByValue(
					DBTableID.SlayerTaskSublist.ID,
					DBTableID.SlayerTaskSublist.COL_TASK_SUBTABLE_ID,
					0,
					client.getVarbitValue(VarbitID.SLAYER_TARGET_BOSSID));

				if (bossRows.isEmpty())
				{
					return;
				}
				taskDBRow = (Integer) client.getDBTableField(bossRows.get(0), DBTableID.SlayerTaskSublist.COL_TASK, 0)[0];
			}
			else
			{
				var taskRows = client.getDBRowsByValue(DBTableID.SlayerTask.ID, DBTableID.SlayerTask.COL_ID, 0, taskId);
				if (taskRows.isEmpty())
				{
					return;
				}
				taskDBRow = taskRows.get(0);
			}

			var taskName = (String) client.getDBTableField(taskDBRow, DBTableID.SlayerTask.COL_NAME_UPPERCASE, 0)[0];

			int areaId = client.getVarpValue(VarPlayerID.SLAYER_AREA);
			String taskLocation = null;
			if (areaId > 0)
			{
				var areaRows = client.getDBRowsByValue(DBTableID.SlayerArea.ID, DBTableID.SlayerArea.COL_AREA_ID, 0, areaId);
				if (areaRows.isEmpty())
				{
					return;
				}

				taskLocation = (String) client.getDBTableField(areaRows.get(0), DBTableID.SlayerArea.COL_AREA_NAME_IN_HELPER, 0)[0];
			}

			int initialAmount = client.getVarpValue(VarPlayerID.SLAYER_COUNT_ORIGINAL);
			if (client.getVarbitValue(VarbitID.SLAYER_MODIFIER_ID) == 2)
			{
				boolean isNegative = client.getVarbitValue(VarbitID.SLAYER_MODIFIER_NEGATIVE) == 1;
				int modifierValue = client.getVarbitValue(VarbitID.SLAYER_MODIFIER_VALUE);
				initialAmount += isNegative ? -modifierValue : modifierValue;
			}

			if (loginFlag)
			{
				setTask(taskName, amount, initialAmount, taskLocation, false);
			}
			else if (!Objects.equals(taskName, this.taskName) || !Objects.equals(taskLocation, this.taskLocation))
			{
				setTask(taskName, amount, initialAmount, taskLocation, true);
			}
		}
		else
		{
			setTask("", 0, 0);
		}
	}

	public boolean isTarget(NPC npc)
	{
		if (targetNames.isEmpty())
		{
			return false;
		}

		final NPCComposition composition = npc.getTransformedComposition();
		if (composition == null)
		{
			return false;
		}

		final String name = composition.getName()
			.replace('\u00A0', ' ')
			.toLowerCase();

		boolean matchingTarget = false;
		for (Pattern target : targetNames)
		{
			final Matcher targetMatcher = target.matcher(name);
			if (targetMatcher.find())
			{
				matchingTarget = true;
			}
		}

		if (!matchingTarget)
		{
			return false;
		}

		for (TaskLocation location : TaskLocation.values())
		{
			if (!location.getName().equals(taskLocation))
			{
				continue;
			}


			int region = WorldPoint.fromLocalInstance(client, client.getLocalPlayer().getLocalLocation()).getRegionID();

			if ( IntStream.of(location.getRegionIds()).anyMatch(x -> x == region))
			{
				return true;
			}
		}

		return false;
	}

	private void rebuildTargetNames(Task task)
	{
		targetNames.clear();

		if (task != null)
		{
			Arrays.stream(task.getTargetNames())
				.map(MaxHitPlugin::targetNamePattern)
				.forEach(targetNames::add);

			targetNames.add(targetNamePattern(taskName.replaceAll("s$", "")));
		}
	}

	private static Pattern targetNamePattern(final String targetName)
	{
		return Pattern.compile("(?:\\s|^)" + targetName + "(?:\\s|$)", Pattern.CASE_INSENSITIVE);
	}

	@VisibleForTesting
	void setTask(String name, int amt, int initAmt)
	{
		setTask(name, amt, initAmt, null, true);
	}

	private void setTask(String name, int amt, int initAmt, String location, boolean addCounter)
	{
		taskName = name;
		amount = amt;
		initialAmount = initAmt;
		taskLocation = location;

		Task task = Task.getTask(name);
		rebuildTargetNames(task);
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
		int varpId = event.getVarpId();
		int varbitId = event.getVarbitId();

		if (varpId == VarPlayerID.COM_MODE
			|| varpId == VarPlayerID.COM_STANCE
			|| varbitId == VarbitID.COMBAT_WEAPON_CATEGORY
			|| varbitId == VarbitID.AUTOCAST_DEFMODE)
		{
			replaceCalculatorAndMaxHit();
		}
		else if (varbitId == VarbitID.PRAYER_ALLACTIVE)
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
		else if (varbitId == VarbitID.AUTOCAST_SPELL)
		{
			maxHitCalculator.calculateMaxHit();
		}
		else if (varpId == VarPlayerID.SLAYER_COUNT
				|| varpId == VarPlayerID.SLAYER_AREA
				|| varpId == VarPlayerID.SLAYER_TARGET
				|| varbitId == VarbitID.SLAYER_TARGET_BOSSID
				|| varpId == VarPlayerID.SLAYER_COUNT_ORIGINAL
				|| varbitId == VarbitID.SLAYER_MODIFIER_ID
				|| varbitId == VarbitID.SLAYER_MODIFIER_VALUE
				|| varbitId == VarbitID.SLAYER_MODIFIER_NEGATIVE)
		{
			clientThread.invokeLater(this::updateTask);
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
	public void onGraphicChanged(GraphicChanged event)
	{
		final Actor eventActor = event.getActor();

		if (eventActor == null)
			return;

		// Check that event target is who we're interacting with
		if (eventActor != client.getLocalPlayer())
			return;


		// Okay, we've found a match for our target
		// Now Iterate over standard spellbook spells looking to see if the projectile is a spell
		for (MagicSpell spell : MagicSpell.getStandardSpells())
		{
			// Looks for matching id
			if (!eventActor.hasSpotAnim(spell.getProjectileId()))
			{
				continue;
			}

			// Don't recalc if we're already using the same spell
			if (activeSpell == spell)
				return;


			activeSpell = spell;
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