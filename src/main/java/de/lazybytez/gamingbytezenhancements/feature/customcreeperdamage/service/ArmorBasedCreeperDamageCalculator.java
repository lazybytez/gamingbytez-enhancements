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

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/**
 * Turns a creeper blast into the damage an armoured player takes.
 * <p>
 * Damage rises with the protection a player wears, so heavier armour makes a creeper more
 * dangerous rather than less. A random roll on every hit keeps the outcome variable.
 */
public class ArmorBasedCreeperDamageCalculator {
    private static final double MIN_BLAST_STRENGTH = 20.0;
    private static final double MAX_BLAST_STRENGTH = 24.0;
    private static final double PROTECTION_SCALE = 26.0;
    private static final double MIN_PROTECTION = 13.0;
    private static final double ENCHANTMENT_WEIGHT = 0.125;
    private static final double MIN_LUCK = 0.15;
    private static final double MAX_LUCK = 1.0;
    private static final double RESISTANCE_REDUCTION_PER_LEVEL = 0.4;

    private final Random random = new Random();

    /**
     * Calculates the damage a player takes from a creeper blast.
     *
     * @param equipment       The player's equipped armor pieces.
     * @param armorPoints     The player's armor attribute value.
     * @param armorToughness  The player's armor toughness attribute value.
     * @param resistanceLevel The player's Resistance effect level, zero when the effect is absent.
     * @param baseDamage      The damage the blast deals before any reduction.
     * @return The health the player should lose.
     */
    public double calculateDamage(
            ItemStack[] equipment,
            double armorPoints,
            double armorToughness,
            int resistanceLevel,
            double baseDamage
    ) {
        return this.damageFor(
                armorPoints,
                armorToughness,
                this.calculateEnchantmentFactor(equipment),
                resistanceLevel,
                baseDamage
        );
    }

    /**
     * Calculates the health lost to a blast against the given protection.
     * <p>
     * The result is what the player loses, not a value the server reduces again. Armor raises it
     * and Resistance lowers it, an enchantment level counting an eighth of what an armor point
     * counts. Blast strength is clamped, so distance decides how hard a hit lands only within a
     * narrow band and a creeper against the player is no more certain to kill than one a few steps
     * away.
     *
     * @param armorPoints       The player's armor attribute value.
     * @param armorToughness    The player's armor toughness attribute value.
     * @param enchantmentLevels The summed Protection and Blast Protection levels.
     * @param resistanceLevel   The player's Resistance effect level, zero when the effect is absent.
     * @param baseDamage        The damage the blast deals before any reduction.
     * @return The health the player should lose.
     */
    double damageFor(
            double armorPoints,
            double armorToughness,
            double enchantmentLevels,
            int resistanceLevel,
            double baseDamage
    ) {
        double blast = Math.clamp(
                baseDamage,
                ArmorBasedCreeperDamageCalculator.MIN_BLAST_STRENGTH,
                ArmorBasedCreeperDamageCalculator.MAX_BLAST_STRENGTH
        );
        double protection = Math.max(
                armorPoints
                        + armorToughness
                        + ArmorBasedCreeperDamageCalculator.ENCHANTMENT_WEIGHT * enchantmentLevels,
                ArmorBasedCreeperDamageCalculator.MIN_PROTECTION
        );
        double resistance = Math.max(
                0.0,
                1.0 - ArmorBasedCreeperDamageCalculator.RESISTANCE_REDUCTION_PER_LEVEL * resistanceLevel
        );

        return blast
                * (protection / ArmorBasedCreeperDamageCalculator.PROTECTION_SCALE)
                * this.rollLuck()
                * resistance;
    }

    private double rollLuck() {
        return this.random.nextDouble(
                ArmorBasedCreeperDamageCalculator.MIN_LUCK,
                ArmorBasedCreeperDamageCalculator.MAX_LUCK
        );
    }

    private double calculateEnchantmentFactor(ItemStack[] equipment) {
        double enchantmentFactor = 0;
        for (ItemStack item : equipment) {
            if (item == null) {
                continue;
            }

            enchantmentFactor += item.getEnchantmentLevel(Enchantment.PROTECTION);
            enchantmentFactor += item.getEnchantmentLevel(Enchantment.BLAST_PROTECTION);
        }

        return enchantmentFactor;
    }
}
