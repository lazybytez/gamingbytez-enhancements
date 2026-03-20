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
package de.lazybytez.gamingbytezenhancements.feature.temporarycart.event;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.temporarycart.TemporaryCartManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

/**
 * Listener that handles preserving minecarts during player teleportation.
 * <p>
 * Cross-world teleports automatically dismount players. This listener ensures both
 * temporary and normal minecarts are properly handled during teleportation:
 * - Temporary carts: Removed at origin, respawned at destination
 * - Normal carts: Teleported to destination and player remounted
 * </p>
 */
public class MinecartTeleportListener implements Listener {
    private final TemporaryCartManager temporaryCartManager;
    private final EnhancementsPlugin plugin;

    public MinecartTeleportListener(TemporaryCartManager temporaryCartManager, EnhancementsPlugin plugin) {
        this.temporaryCartManager = temporaryCartManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Entity vehicle = player.getVehicle();

        if (!(vehicle instanceof Minecart minecart)) {
            return;
        }

        double speed = minecart.getVelocity().length();

        if (this.temporaryCartManager.isTemporaryCart(minecart)) {
            this.handleTemporaryCart(player, minecart, event.getTo(), speed);
            return;
        }

        this.handleNormalCart(player, minecart, event.getTo(), speed);
    }

    private void handleTemporaryCart(Player player, Minecart minecart, Location destination, double speed) {
        minecart.remove();
        this.temporaryCartManager.removeCoolDown(player);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (!player.isValid()) {
                return;
            }

            this.temporaryCartManager.spawnTemporaryCart(player, destination);

            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                Entity vehicle = player.getVehicle();
                if (!(vehicle instanceof Minecart newCart)) {
                    return;
                }

                Vector direction = player.getLocation().getDirection();
                Vector velocity = direction.normalize().multiply(speed);
                newCart.setVelocity(velocity);
            }, 3L);
        }, 5L);
    }

    private void handleNormalCart(Player player, Minecart minecart, Location destination, double speed) {
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (!minecart.isValid() || !player.isValid()) {
                return;
            }

            minecart.teleport(destination);

            if (!minecart.isValid() || !player.isValid()) {
                return;
            }

            minecart.addPassenger(player);

            Vector direction = player.getLocation().getDirection();
            Vector velocity = direction.normalize().multiply(speed);
            minecart.setVelocity(velocity);
        }, 5L);
    }
}
