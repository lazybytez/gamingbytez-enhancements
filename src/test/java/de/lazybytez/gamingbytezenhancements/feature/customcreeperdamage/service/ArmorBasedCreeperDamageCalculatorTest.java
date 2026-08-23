/*
 * Gaming Bytez Enhancements - Gameplay enhancements used on our SMP servers.
 * Copyright (C) 2026 Lazy Bytez (Pascal Zarrad, Elias Knodel) and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.lazybytez.gamingbytezenhancements.feature.customcreeperdamage.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Covers the damage curve of {@link ArmorBasedCreeperDamageCalculator}.
 * <p>
 * The reference hit is a creeper detonating about two blocks from a player at full health, which
 * lands a blast of 28 before reduction. The rates below are the share of that hit that kills
 * outright, sampled over the luck roll.
 */
class ArmorBasedCreeperDamageCalculatorTest {
    private static final int SAMPLES = 200_000;
    private static final double RATE_TOLERANCE = 0.02;

    private static final double PLAYER_HEALTH = 20.0;
    private static final double CLOSE_BLAST = 28.0;
    private static final double NETHERITE_ARMOR = 20.0;
    private static final double NETHERITE_TOUGHNESS = 12.0;
    private static final double PROTECTION_IV_FULL_SET = 16.0;

    private final ArmorBasedCreeperDamageCalculator calculator = new ArmorBasedCreeperDamageCalculator();

    @Test
    void damageFor_fullEnchantedNetherite_killsOutrightAboutATenthOfTheTime() {
        double rate = this.oneShotRate(
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_TOUGHNESS,
                ArmorBasedCreeperDamageCalculatorTest.PROTECTION_IV_FULL_SET
        );

        assertEquals(0.344, rate, ArmorBasedCreeperDamageCalculatorTest.RATE_TOLERANCE);
    }

    @Test
    void damageFor_plainNetherite_killsOutrightLessOftenThanEnchantedNetherite() {
        double plainRate = this.oneShotRate(
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_TOUGHNESS,
                0.0
        );
        double enchantedRate = this.oneShotRate(
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_TOUGHNESS,
                ArmorBasedCreeperDamageCalculatorTest.PROTECTION_IV_FULL_SET
        );

        assertEquals(0.179, plainRate, ArmorBasedCreeperDamageCalculatorTest.RATE_TOLERANCE);
        assertTrue(plainRate < enchantedRate, "enchanted armour must stay the more dangerous set");
    }

    @Test
    void damageFor_fullEnchantedNetherite_staysWithinTheLuckWindow() {
        double lowest = Double.MAX_VALUE;
        double highest = 0.0;

        for (int sample = 0; sample < ArmorBasedCreeperDamageCalculatorTest.SAMPLES; sample++) {
            double damage = this.closeBlastAgainst(
                    ArmorBasedCreeperDamageCalculatorTest.NETHERITE_ARMOR,
                    ArmorBasedCreeperDamageCalculatorTest.NETHERITE_TOUGHNESS,
                    ArmorBasedCreeperDamageCalculatorTest.PROTECTION_IV_FULL_SET
            );

            lowest = Math.min(lowest, damage);
            highest = Math.max(highest, damage);
        }

        assertTrue(lowest >= 10.08, "the weakest roll must not fall below half the armour factor");
        assertTrue(highest <= 25.2, "the strongest roll must not exceed the armour factor ceiling");
    }

    @Test
    void damageFor_anUnarmoredPlayer_takesATenthOfTheBlast() {
        double damage = this.closeBlastAgainst(0.0, 0.0, 0.0);

        assertTrue(damage >= 1.4 && damage <= 3.5, "an unarmoured player takes a tenth of the blast");
    }

    @Test
    void damageFor_moreArmor_dealsMoreDamage() {
        double diamond = this.meanDamage(20.0, 8.0, 0.0);
        double netherite = this.meanDamage(
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_TOUGHNESS,
                0.0
        );

        assertTrue(diamond < netherite, "netherite must stay more dangerous than diamond");
    }

    private double oneShotRate(double armorPoints, double armorToughness, double enchantmentLevels) {
        int kills = 0;

        for (int sample = 0; sample < ArmorBasedCreeperDamageCalculatorTest.SAMPLES; sample++) {
            if (this.closeBlastAgainst(armorPoints, armorToughness, enchantmentLevels)
                    >= ArmorBasedCreeperDamageCalculatorTest.PLAYER_HEALTH) {
                kills++;
            }
        }

        return (double) kills / ArmorBasedCreeperDamageCalculatorTest.SAMPLES;
    }

    private double meanDamage(double armorPoints, double armorToughness, double enchantmentLevels) {
        double total = 0.0;

        for (int sample = 0; sample < ArmorBasedCreeperDamageCalculatorTest.SAMPLES; sample++) {
            total += this.closeBlastAgainst(armorPoints, armorToughness, enchantmentLevels);
        }

        return total / ArmorBasedCreeperDamageCalculatorTest.SAMPLES;
    }

    private double closeBlastAgainst(double armorPoints, double armorToughness, double enchantmentLevels) {
        return this.calculator.damageFor(
                armorPoints,
                armorToughness,
                enchantmentLevels,
                ArmorBasedCreeperDamageCalculatorTest.CLOSE_BLAST
        );
    }
}
