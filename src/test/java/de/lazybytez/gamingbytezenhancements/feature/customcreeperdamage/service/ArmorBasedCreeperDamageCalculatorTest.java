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
 * The calculator returns health lost rather than a value the server reduces again, so the rates
 * below are the share of a hit that kills a player at full health. Blast strength is clamped into
 * a narrow band, so the reference blast of 22 is what a creeper lands from most positions.
 */
class ArmorBasedCreeperDamageCalculatorTest {
    private static final int SAMPLES = 200_000;
    private static final double RATE_TOLERANCE = 0.02;
    private static final double DAMAGE_TOLERANCE = 0.15;

    private static final double PLAYER_HEALTH = 20.0;
    private static final double REFERENCE_BLAST = 24.0;
    private static final double DISTANT_BLAST = 5.0;
    private static final double POINT_BLANK_BLAST = 43.0;

    private static final double NETHERITE_ARMOR = 20.0;
    private static final double NETHERITE_TOUGHNESS = 12.0;
    private static final double DIAMOND_ARMOR = 20.0;
    private static final double DIAMOND_TOUGHNESS = 8.0;
    private static final double IRON_ARMOR = 15.0;
    private static final double PROTECTION_IV_FULL_SET = 16.0;

    private final ArmorBasedCreeperDamageCalculator calculator = new ArmorBasedCreeperDamageCalculator();

    @Test
    void damageFor_fullDiamond_killsOutrightAboutOneHitInFive() {
        double rate = this.oneShotRate(
                ArmorBasedCreeperDamageCalculatorTest.DIAMOND_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.DIAMOND_TOUGHNESS,
                0.0
        );

        assertEquals(0.200, rate, ArmorBasedCreeperDamageCalculatorTest.RATE_TOLERANCE);
    }

    @Test
    void damageFor_fullNetherite_killsOutrightMoreOftenThanDiamond() {
        double netheriteRate = this.oneShotRate(
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_TOUGHNESS,
                0.0
        );
        double diamondRate = this.oneShotRate(
                ArmorBasedCreeperDamageCalculatorTest.DIAMOND_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.DIAMOND_TOUGHNESS,
                0.0
        );

        assertEquals(0.233, netheriteRate, ArmorBasedCreeperDamageCalculatorTest.RATE_TOLERANCE);
        assertTrue(diamondRate < netheriteRate, "netherite must stay the more dangerous set");
    }

    @Test
    void damageFor_protectionEnchantments_raiseTheRateOnlySlightly() {
        double enchantedRate = this.oneShotRate(
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_TOUGHNESS,
                ArmorBasedCreeperDamageCalculatorTest.PROTECTION_IV_FULL_SET
        );
        double plainRate = this.oneShotRate(
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_TOUGHNESS,
                0.0
        );

        assertEquals(0.249, enchantedRate, ArmorBasedCreeperDamageCalculatorTest.RATE_TOLERANCE);
        assertTrue(
                enchantedRate - plainRate < 0.1,
                "a full set of Protection IV must not transform the odds"
        );
    }

    @Test
    void damageFor_ironOrLighter_neverKillsOutright() {
        double iron = this.oneShotRate(ArmorBasedCreeperDamageCalculatorTest.IRON_ARMOR, 0.0, 0.0);
        double bare = this.oneShotRate(0.0, 0.0, 0.0);

        assertEquals(0.0, iron, "iron must not be able to kill in one hit");
        assertEquals(0.0, bare, "no armour must not be able to kill in one hit");
    }

    @Test
    void damageFor_anUnarmoredPlayer_losesAboutThreeHearts() {
        double mean = this.meanDamage(0.0, 0.0, 0.0);

        assertEquals(6.43, mean, ArmorBasedCreeperDamageCalculatorTest.DAMAGE_TOLERANCE);
    }

    @Test
    void damageFor_moreArmor_dealsMoreDamage() {
        double bare = this.meanDamage(0.0, 0.0, 0.0);
        double iron = this.meanDamage(ArmorBasedCreeperDamageCalculatorTest.IRON_ARMOR, 0.0, 0.0);
        double diamond = this.meanDamage(
                ArmorBasedCreeperDamageCalculatorTest.DIAMOND_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.DIAMOND_TOUGHNESS,
                0.0
        );
        double netherite = this.meanDamage(
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_TOUGHNESS,
                0.0
        );

        assertTrue(bare < iron, "iron must be more dangerous than no armour");
        assertTrue(iron < diamond, "diamond must be more dangerous than iron");
        assertTrue(diamond < netherite, "netherite must be more dangerous than diamond");
    }

    @Test
    void damageFor_resistance_keepsEvenTheHeaviestBlastSurvivable() {
        double highest = 0.0;

        for (int sample = 0; sample < ArmorBasedCreeperDamageCalculatorTest.SAMPLES; sample++) {
            highest = Math.max(highest, this.calculator.damageFor(
                    ArmorBasedCreeperDamageCalculatorTest.NETHERITE_ARMOR,
                    ArmorBasedCreeperDamageCalculatorTest.NETHERITE_TOUGHNESS,
                    ArmorBasedCreeperDamageCalculatorTest.PROTECTION_IV_FULL_SET,
                    1,
                    ArmorBasedCreeperDamageCalculatorTest.POINT_BLANK_BLAST
            ));
        }

        assertTrue(
                highest < ArmorBasedCreeperDamageCalculatorTest.PLAYER_HEALTH,
                "one level of Resistance must rule out a one shot entirely"
        );
    }

    @Test
    void damageFor_deepResistance_stopsTheBlastCompletely() {
        double damage = this.calculator.damageFor(
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_ARMOR,
                ArmorBasedCreeperDamageCalculatorTest.NETHERITE_TOUGHNESS,
                ArmorBasedCreeperDamageCalculatorTest.PROTECTION_IV_FULL_SET,
                3,
                ArmorBasedCreeperDamageCalculatorTest.POINT_BLANK_BLAST
        );

        assertEquals(0.0, damage, "Resistance III must leave nothing to deal");
    }

    @Test
    void damageFor_aDistantBlast_isRaisedToTheFloorOfTheBand() {
        double distant = this.meanDamageFrom(ArmorBasedCreeperDamageCalculatorTest.DISTANT_BLAST, 0.0, 0.0, 0.0);
        double floored = this.meanDamageFrom(20.0, 0.0, 0.0, 0.0);

        assertEquals(floored, distant, ArmorBasedCreeperDamageCalculatorTest.DAMAGE_TOLERANCE);
    }

    @Test
    void damageFor_aPointBlankBlast_isCappedToTheCeilingOfTheBand() {
        double pointBlank = this.meanDamageFrom(
                ArmorBasedCreeperDamageCalculatorTest.POINT_BLANK_BLAST,
                0.0,
                0.0,
                0.0
        );
        double capped = this.meanDamageFrom(ArmorBasedCreeperDamageCalculatorTest.REFERENCE_BLAST, 0.0, 0.0, 0.0);

        assertEquals(capped, pointBlank, ArmorBasedCreeperDamageCalculatorTest.DAMAGE_TOLERANCE);
    }

    private double oneShotRate(double armorPoints, double armorToughness, double enchantmentLevels) {
        int kills = 0;

        for (int sample = 0; sample < ArmorBasedCreeperDamageCalculatorTest.SAMPLES; sample++) {
            if (this.damageFrom(
                    ArmorBasedCreeperDamageCalculatorTest.REFERENCE_BLAST,
                    armorPoints,
                    armorToughness,
                    enchantmentLevels
            ) >= ArmorBasedCreeperDamageCalculatorTest.PLAYER_HEALTH) {
                kills++;
            }
        }

        return (double) kills / ArmorBasedCreeperDamageCalculatorTest.SAMPLES;
    }

    private double meanDamage(double armorPoints, double armorToughness, double enchantmentLevels) {
        return this.meanDamageFrom(
                ArmorBasedCreeperDamageCalculatorTest.REFERENCE_BLAST,
                armorPoints,
                armorToughness,
                enchantmentLevels
        );
    }

    private double meanDamageFrom(
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
        return this.calculator.damageFor(armorPoints, armorToughness, enchantmentLevels, 0, blast);
    }
}
