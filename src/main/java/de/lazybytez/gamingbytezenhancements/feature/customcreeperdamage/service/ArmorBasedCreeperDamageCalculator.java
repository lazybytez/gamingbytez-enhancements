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

public class ArmorBasedCreeperDamageCalculator {
    private final Random random = new Random();

    public double calculateDamage(
            ItemStack[] equipment,
            double armorPoints,
            double armorToughness,
            double baseDamage
    ) {
        double armorFactor = (armorPoints + armorToughness + this.calculateEnchantmentFactor(equipment)) / 5;
        if (armorFactor == 0) {
            armorFactor = 1;
        }

        double adjustedArmorFactor = (armorFactor > 0 ? armorFactor : 1) / 10;

        double luck = this.random.nextDouble(0.5, 2);
        double armorLuckFactor = luck * adjustedArmorFactor;

        double adjustedDamage = baseDamage * armorLuckFactor;

        if (adjustedDamage < 0) {
            adjustedDamage = baseDamage;
        }

        return adjustedDamage;
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
