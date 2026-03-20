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
package de.lazybytez.gamingbytezenhancements.feature.minecartportal.listener;

import de.lazybytez.gamingbytezenhancements.feature.minecartportal.MinecartPortalFeature;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.model.MinecartPortal;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collection;
import java.util.List;

/**
 * Listener that handles the use of Minecart Portals.
 * <p>
 * This listener handles the BlockRedstoneEvent. The event is triggered when redstone
 * levels change, which is the case when a detector rail is used.
 * This allows a somewhat performant detection of players that enter Minecart Portals,
 * as we only need to further process any redstone activity involving detector rails.
 */
public class MinecartPortalActivationListener implements Listener {
    private final MinecartPortalFeature feature;

    public MinecartPortalActivationListener(MinecartPortalFeature feature) {
        this.feature = feature;
    }

    @EventHandler
    public void onDetectorRailActivated(BlockRedstoneEvent event) {
        // Only trigger on detector rails
        Block block = event.getBlock();
        if (!block.getType().equals(Material.DETECTOR_RAIL)) {
            return;
        }

        // Check if detector rail was triggered (and not deactivated)
        // Currently this event is somehow broken. Therefore, check has been simplified.
        // This unfortunately may trigger portals to happen multiple times at a single activation.
        // If Paper does not fix this issue, we should implement a cooldown (like done for temporary carts)
        // in the future.
//        int oldCurrent = event.getOldCurrent();
        int newCurrent = event.getNewCurrent();
//        if (oldCurrent != 0 || newCurrent == 0) {
        if (newCurrent != 15) {
            return;
        }

        // Check if current detector rail is a portal
        MinecartPortal portal = this.feature.getPortalConfig().getPortalAtLocation(block.getLocation());
        if (portal == null || portal.getDestination() == null) {
            return;
        }

        // Get entities
        Collection<Entity> entities = block.getWorld().getNearbyEntities(
                block.getLocation(),
                1,
                1,
                1,
                e -> e instanceof Minecart
        );
        for (Entity entity : entities) {
            if (!entity.getType().equals(EntityType.MINECART)) {
                continue;
            }

            Minecart minecart = (Minecart) entity;
            List<Entity> players = minecart.getPassengers().stream().filter(e -> e instanceof Player).toList();

            // Portals only support Minecarts with exactly a single player
            if (players.size() != 1) {
                continue;
            }
            Player player = (Player) players.getFirst();

            boolean success = player.teleport(
                    portal.getDestination(),
                    PlayerTeleportEvent.TeleportCause.PLUGIN
            );

            if (!success) {
                this.feature.getPlugin().getLogger().info("The player "
                        + player.getName()
                        + " could not be teleported by the Minecart Portal at "
                        + portal.getPortal()
                        + " to "
                        + portal.getDestination()
                );
                continue;
            }

            this.feature.getPlugin().getLogger().info("The player "
                    + player.getName()
                    + " has used the Minecart Portal at "
                    + portal.getPortal()
                    + " and has been teleported to "
                    + portal.getDestination()
            );
        }
    }
}
