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
package de.lazybytez.gamingbytezenhancements.feature.customloot.service;

import org.bukkit.damage.DamageSource;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantmentLevelOnItemDeterminer {

    /**
     * Return the enchantment level if the direct damage is done with a weapon with the enchantment.
     * Return 0 otherwise.
     *
     * @return Level of the enchantment on the weapon
     */
    public int getEnchantmentLevelOnMeleeWeapon(EntityDeathEvent event, Enchantment enchantment) {
        DamageSource damageSource = event.getDamageSource();
        if (damageSource.isIndirect()) {
            return 0;
        }

        Entity directEntity = damageSource.getDirectEntity();

        if ( !(directEntity instanceof Player)) {
            return 0;
        }

        ItemStack itemInMainHand = ((Player) directEntity).getInventory().getItemInMainHand();

        return itemInMainHand.getEnchantmentLevel(enchantment);
    }
}
