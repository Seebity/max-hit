package com.maxhit.MaxHitTest;

import com.maxhit.MagicSpell;
import com.maxhit.MockedTest;
import com.maxhit.calculators.MaxHitCalculator;
import com.maxhit.calculators.MaxHitCalculatorFactory;
import com.maxhit.calculators.SpecialAttackCalculator;
import com.maxhit.calculators.StrengthBonusCalculator;
import com.maxhit.equipment.SpecialAttackWeapon;
import com.maxhit.styles.AttackStyle;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.gameval.VarbitID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@Slf4j
public class PlayerSetupsTest extends MockedTest
{
	private MaxHitCalculatorFactory maxHitCalculatorFactory;
	private ItemContainer mockedItemContainer;

	@BeforeEach
	protected void setUp()
	{
		super.setUp();
		// Consolidate common setup logic that runs before each test
		maxHitCalculatorFactory = new MaxHitCalculatorFactory(client, itemManager);
		mockedItemContainer = mock(ItemContainer.class);
		when(client.getItemContainer(anyInt())).thenReturn(mockedItemContainer);
	}

	private void testPlayerSetups(PlayerSetup[] setups, boolean standard)
	{
		MaxHitCalculator maxHitCalculator;
		for (var setup : setups)
		{
			when(mockedItemContainer.getItems()).thenReturn(setup.getEquippedItems());
			when(mockedItemContainer.getItem(anyInt())).thenAnswer(invocation -> {
				int slotIndex = invocation.getArgument(0); // Get the slot index passed to the method
				return setup.getEquippedItems()[slotIndex];  // Return the item from the corresponding array index
			});
			maxHitCalculator = maxHitCalculatorFactory.create(setup.getCombatStyle(), setup.getAttackStyle());

			try(MockedStatic<StrengthBonusCalculator> strengthBonusCalculatorMockedStatic = mockStatic(StrengthBonusCalculator.class))
			{
				final Skill skill = maxHitCalculator.getSkill();
				if (setup.getAttackStyle() == AttackStyle.CASTING)
				{
					when(client.getVarbitValue(VarbitID.AUTOCAST_SPELL)).thenReturn(setup.getMagicSpell().getVarbValue());
				}
				strengthBonusCalculatorMockedStatic.when(() -> StrengthBonusCalculator.getDinhsBonus(setup.getAttackStyle(), mockedItemContainer, itemManager)).thenCallRealMethod();
				strengthBonusCalculatorMockedStatic.when(() -> StrengthBonusCalculator.getStrengthBonus(mockedItemContainer, itemManager, skill)).thenReturn(setup.getStrengthBonus());
				assertEquals(setup.getMaxHitCalculatorClass(), maxHitCalculator.getClass());
				maxHitCalculator.calculateMaxHit();
				if (standard)
					assertEquals(setup.getStandardMaxHit(), maxHitCalculator.getMaxHit(),
						() -> String.format("%s standard max hit should be %.0f", setup.name(), setup.getStandardMaxHit()));
				else
				{
					assertEquals(setup.getMaxedMaxHit(), maxHitCalculator.getMaxHit(),
						() -> String.format("%s maxed max hit should be %.0f", setup.name(), setup.getMaxedMaxHit()));

					Item weapon = setup.getEquippedItems()[EquipmentInventorySlot.WEAPON.getSlotIdx()];

					// Check for special max hit
					if (weapon == null)
						continue;

					SpecialAttackCalculator calc = new SpecialAttackCalculator(client);
					calc.setEquippedItems(mockedItemContainer);
					for (SpecialAttackWeapon specialAttackWeapon : SpecialAttackWeapon.values())
					{
						if (specialAttackWeapon.getItemId() == weapon.getId())
						{
							assertEquals(setup.getMaxedSpecialHit(), calc.getSpecialMaxHit(maxHitCalculator.getMaxHit()),
								() -> String.format("%s special max hit should be %.0f", setup.name(), setup.getMaxedMaxHit()));
						}
					}
				}
			}


		}
	}

	@Test
	void testMaxHitStandard()
	{
		when(client.getBoostedSkillLevel(any())).thenReturn(1);
		when(client.getBoostedSkillLevel(Skill.HITPOINTS)).thenReturn(99);
		testPlayerSetups(PlayerSetup.values(), true);
	}

	@Test
	void testMaxHitMax()
	{
		when(client.getBoostedSkillLevel(any())).thenReturn(99);
		when(client.getBoostedSkillLevel(Skill.HITPOINTS)).thenReturn(1);
		testPlayerSetups(PlayerSetup.values(), false);
	}
}
