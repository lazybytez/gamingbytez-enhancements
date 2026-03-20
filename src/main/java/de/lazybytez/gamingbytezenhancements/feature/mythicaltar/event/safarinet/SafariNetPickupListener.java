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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.safarinet;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.safarinet.SafariNetManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener for picking up Safari Nets.
 * <p>
 * Logs when players pick up Safari Nets, including whether they contain entities.
 */
public class SafariNetPickupListener implements Listener {

    private final MythicAltarFeature mythicAltarFeature;

    public SafariNetPickupListener(MythicAltarFeature mythicAltarFeature) {
        this.mythicAltarFeature = mythicAltarFeature;
    }

    /**
     * Handle entity pickup events for Safari Nets.
     * <p>
     * Logs when a player picks up a Safari Net, distinguishing between empty nets
     * and nets containing entities.
     *
     * @param event The entity pickup item event
     */
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack item = event.getItem().getItemStack();
        SafariNetManager safariNetManager = this.mythicAltarFeature
                .getCustomItemManagerRegistry()
                .getCustomItemManager(SafariNetManager.class);

        if (!safariNetManager.isCustomItem(item)) {
            return;
        }

        if (safariNetManager.hasEntity(item)) {
            this.logPickupWithEntity(player, safariNetManager.getEntityType(item));
        } else {
            this.logPickupEmpty(player);
        }
    }

    /**
     * Log when a player picks up a Safari Net containing an entity.
     *
     * @param player     The player who picked up the item
     * @param entityType The type of entity in the Safari Net
     */
    private void logPickupWithEntity(Player player, EntityType entityType) {
        this.mythicAltarFeature.getPlugin().getLogger().info("A player picked up a Safari Net containing a "
                + entityType
                + " at "
                + player.getLocation());
    }

    /**
     * Log when a player picks up an empty Safari Net.
     *
     * @param player The player who picked up the item
     */
    private void logPickupEmpty(Player player) {
        this.mythicAltarFeature.getPlugin().getLogger().info("A player picked up an empty Safari Net at "
                + player.getLocation());
    }
}
