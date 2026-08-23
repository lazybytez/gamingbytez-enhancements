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
    private static final double PROTECTION_SCALE = 42.0;
    private static final double ENCHANTMENT_WEIGHT = 0.25;
    private static final double MIN_PROTECTION = 22.0;
    private static final double MIN_LUCK = 0.15;
    private static final double MAX_LUCK = 1.0;
    private static final double MAX_BLAST_STRENGTH = 29.0;

    private final Random random = new Random();

    /**
     * Calculates the damage a player takes from a creeper blast.
     *
     * @param equipment      The player's equipped armor pieces.
     * @param armorPoints    The player's armor attribute value.
     * @param armorToughness The player's armor toughness attribute value.
     * @param baseDamage     The damage the blast deals before any reduction.
     * @return The damage the player should take.
     */
    public double calculateDamage(
            ItemStack[] equipment,
            double armorPoints,
            double armorToughness,
            double baseDamage
    ) {
        return this.damageFor(armorPoints, armorToughness, this.calculateEnchantmentFactor(equipment), baseDamage);
    }

    /**
     * Calculates the damage a blast deals against the given protection.
     * <p>
     * An enchantment level counts a quarter of what an armor point counts, so enchanting a set
     * that is already heavy raises the danger without doubling it. Blast strength is capped, so a
     * creeper that goes off against a player is about as likely to kill as one a few steps away
     * rather than certain to. Protection is floored, so a player wearing little or nothing still
     * takes a serious hit.
     *
     * @param armorPoints       The player's armor attribute value.
     * @param armorToughness    The player's armor toughness attribute value.
     * @param enchantmentLevels The summed Protection and Blast Protection levels.
     * @param baseDamage        The damage the blast deals before any reduction.
     * @return The damage the player should take.
     */
    double damageFor(
            double armorPoints,
            double armorToughness,
            double enchantmentLevels,
            double baseDamage
    ) {
        double blast = Math.min(baseDamage, ArmorBasedCreeperDamageCalculator.MAX_BLAST_STRENGTH);
        double protection = Math.max(
                armorPoints
                        + armorToughness
                        + ArmorBasedCreeperDamageCalculator.ENCHANTMENT_WEIGHT * enchantmentLevels,
                ArmorBasedCreeperDamageCalculator.MIN_PROTECTION
        );

        return blast
                * (protection / ArmorBasedCreeperDamageCalculator.PROTECTION_SCALE)
                * this.rollLuck();
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
