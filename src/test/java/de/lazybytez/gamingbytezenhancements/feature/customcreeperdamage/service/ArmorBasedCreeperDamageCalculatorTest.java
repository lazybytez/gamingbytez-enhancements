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
 * Two reference hits are used: a creeper detonating a few steps from the player, which lands a
 * blast of 28 before reduction, and one detonating against the player, which lands 43. The rates
 * below are the share of a hit that kills a player at full health, sampled over the luck roll.
 */
class ArmorBasedCreeperDamageCalculatorTest {
    private static final int SAMPLES = 200_000;
    private static final double RATE_TOLERANCE = 0.02;

    private static final double PLAYER_HEALTH = 20.0;
    private static final double CLOSE_BLAST = 28.0;
    private static final double POINT_BLANK_BLAST = 43.0;
    private static final double NETHERITE_ARMOR = 20.0;
    private static final double NETHERITE_TOUGHNESS = 12.0;
    private static final double DIAMOND_ARMOR = 20.0;
    private static final double DIAMOND_TOUGHNESS = 8.0;
    private static final double PROTECTION_IV_FULL_SET = 16.0;

    private final ArmorBasedCreeperDamageCalculator calculator = new ArmorBasedCreeperDamageCalculator();

    @Test
    void damageFor_fullEnchantedNetherite_killsOutrightAboutAThirdOfTheTime() {
        double rate = this.oneShotRate(
                ArmorBasedCreeperDamageCalculatorTest.CLOSE_BLAST,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_TOUGHNESS,
                ArmorBasedCreeperDamageCalculatorTest.PROTECTION_IV_FULL_SET
        );

        assertEquals(0.344, rate, ArmorBasedCreeperDamageCalculatorTest.RATE_TOLERANCE);
    }

    @Test
    void damageFor_plainNetherite_killsOutrightLessOftenThanEnchantedNetherite() {
        double plainRate = this.oneShotRate(
                ArmorBasedCreeperDamageCalculatorTest.CLOSE_BLAST,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_TOUGHNESS,
                0.0
        );
        double enchantedRate = this.oneShotRate(
                ArmorBasedCreeperDamageCalculatorTest.CLOSE_BLAST,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_TOUGHNESS,
                ArmorBasedCreeperDamageCalculatorTest.PROTECTION_IV_FULL_SET
        );

        assertEquals(0.179, plainRate, ArmorBasedCreeperDamageCalculatorTest.RATE_TOLERANCE);
        assertTrue(plainRate < enchantedRate, "enchanted armour must stay the more dangerous set");
    }

    @Test
    void damageFor_aPointBlankBlast_killsAboutAsOftenAsOneAFewStepsAway() {
        double pointBlankRate = this.oneShotRate(
                ArmorBasedCreeperDamageCalculatorTest.POINT_BLANK_BLAST,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_TOUGHNESS,
                ArmorBasedCreeperDamageCalculatorTest.PROTECTION_IV_FULL_SET
        );
        double closeRate = this.oneShotRate(
                ArmorBasedCreeperDamageCalculatorTest.CLOSE_BLAST,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_TOUGHNESS,
                ArmorBasedCreeperDamageCalculatorTest.PROTECTION_IV_FULL_SET
        );

        assertEquals(0.389, pointBlankRate, ArmorBasedCreeperDamageCalculatorTest.RATE_TOLERANCE);
        assertTrue(
                pointBlankRate - closeRate < 0.1,
                "a blast against the player must not be far deadlier than one a few steps away"
        );
    }

    @Test
    void damageFor_aBlastStrongerThanTheCap_dealsWhatTheCapDeals() {
        double cappedRate = this.oneShotRate(
                ArmorBasedCreeperDamageCalculatorTest.POINT_BLANK_BLAST * 4.0,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_TOUGHNESS,
                ArmorBasedCreeperDamageCalculatorTest.PROTECTION_IV_FULL_SET
        );

        assertEquals(0.389, cappedRate, ArmorBasedCreeperDamageCalculatorTest.RATE_TOLERANCE);
    }

    @Test
    void damageFor_fullEnchantedNetherite_staysWithinTheLuckWindow() {
        double lowest = Double.MAX_VALUE;
        double highest = 0.0;

        for (int sample = 0; sample < ArmorBasedCreeperDamageCalculatorTest.SAMPLES; sample++) {
            double damage = this.damageFrom(
                    ArmorBasedCreeperDamageCalculatorTest.POINT_BLANK_BLAST,
                    ArmorBasedCreeperDamageCalculatorTest.NETHERITE_ARMOR,
                    ArmorBasedCreeperDamageCalculatorTest.NETHERITE_TOUGHNESS,
                    ArmorBasedCreeperDamageCalculatorTest.PROTECTION_IV_FULL_SET
            );

            lowest = Math.min(lowest, damage);
            highest = Math.max(highest, damage);
        }

        assertTrue(lowest >= 10.44, "the weakest roll must not fall below half the armour factor");
        assertTrue(highest <= 26.1, "the strongest roll must not exceed the capped ceiling");
    }

    @Test
    void damageFor_anUnarmoredPlayer_takesATenthOfTheBlast() {
        double damage = this.damageFrom(ArmorBasedCreeperDamageCalculatorTest.CLOSE_BLAST, 0.0, 0.0, 0.0);

        assertTrue(damage >= 1.4 && damage <= 3.5, "an unarmoured player takes a tenth of the blast");
    }

    @Test
    void damageFor_moreArmor_dealsMoreDamage() {
        double diamond = this.meanDamage(
                ArmorBasedCreeperDamageCalculatorTest.CLOSE_BLAST,
                ArmorBasedCreeperDamageCalculatorTest.DIAMOND_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.DIAMOND_TOUGHNESS,
                0.0
        );
        double netherite = this.meanDamage(
                ArmorBasedCreeperDamageCalculatorTest.CLOSE_BLAST,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_TOUGHNESS,
                0.0
        );

        assertTrue(diamond < netherite, "netherite must stay more dangerous than diamond");
    }

    private double oneShotRate(
            double blast,
            double armorPoints,
            double armorToughness,
            double enchantmentLevels
    ) {
        int kills = 0;

        for (int sample = 0; sample < ArmorBasedCreeperDamageCalculatorTest.SAMPLES; sample++) {
            if (this.damageFrom(blast, armorPoints, armorToughness, enchantmentLevels)
                    >= ArmorBasedCreeperDamageCalculatorTest.PLAYER_HEALTH) {
                kills++;
            }
        }

        return (double) kills / ArmorBasedCreeperDamageCalculatorTest.SAMPLES;
    }

    private double meanDamage(
            double blast,
            double armorPoints,
            double armorToughness,
            double enchantmentLevels
    ) {
        double total = 0.0;

        for (int sample = 0; sample < ArmorBasedCreeperDamageCalculatorTest.SAMPLES; sample++) {
            total += this.damageFrom(blast, armorPoints, armorToughness, enchantmentLevels);
        }

        return total / ArmorBasedCreeperDamageCalculatorTest.SAMPLES;
    }

    private double damageFrom(
            double blast,
            double armorPoints,
            double armorToughness,
            double enchantmentLevels
    ) {
        return this.calculator.damageFor(armorPoints, armorToughness, enchantmentLevels, blast);
    }
}
