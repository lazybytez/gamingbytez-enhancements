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

import de.lazybytez.gamingbytezenhancements.feature.minecartportal.PortalConfiguration;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.model.MinecartPortal;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * This event handler ensures that Minecart Portals are guarded against most common destruction.
 * <p>
 * Note that this handler assumes that the "Anti Mob Griefing" feature is enabled and does only handle
 * special events like player breaking the blocks or pistons causing movement.
 */
public class MinecartPortalDestructionListener implements Listener {
    private static final double LOCATION_SIMILARITY_DISTANCE = 1.0;

    private final PortalConfiguration config;

    public MinecartPortalDestructionListener(PortalConfiguration config) {
        this.config = config;
    }

    @EventHandler
    public void onMinecartPortalDestroyedByExplosion(EntityExplodeEvent event) {
        List<Block> portalBlocks = new ArrayList<>();

        for (Block block : event.blockList()) {
            if (!block.getType().equals(Material.DETECTOR_RAIL) && !block.getType().equals(Material.RAIL)) {
                continue;
            }

            for (MinecartPortal portal : this.config.getPortals()) {
                // Custom implementation for explosions, as this works with a list of blocks
                // where the portal block must be removed.
                Location entry = portal.getPortal();
                Location exit = portal.getDestination();

                if (block.getType().equals(Material.DETECTOR_RAIL)
                        && this.isSimilarLocation(block.getLocation(), entry)
                ) {
                    portalBlocks.add(block);
                    continue;
                }

                if (block.getType().equals(Material.RAIL) && this.isSimilarLocation(block.getLocation(), exit)) {
                    portalBlocks.add(block);
                }
            }
        }

        event.blockList().removeAll(portalBlocks);
    }

    @EventHandler
    public void onMinecartPortalDestroyedByPlayer(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (!block.getType().equals(Material.DETECTOR_RAIL) && !block.getType().equals(Material.RAIL)) {
            return;
        }

        for (MinecartPortal portal : this.config.getPortals()) {
            if (this.handleSingleBlockEvent(event, block, portal)) {
                player.sendMessage("Please remove the Minecart Portal first before breaking this block!");
            }
        }
    }

    @EventHandler
    public void onMinecartPortalDestroyedByPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();

        if (!block.getType().equals(Material.DETECTOR_RAIL) && !block.getType().equals(Material.RAIL)) {
            return;
        }

        for (MinecartPortal portal : this.config.getPortals()) {
            this.handleSingleBlockEvent(event, block, portal);
        }
    }

    @EventHandler
    public void onMinecartPortalExtendedByPiston(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (!block.getType().equals(Material.DETECTOR_RAIL) && !block.getType().equals(Material.RAIL)) {
                return;
            }

            for (MinecartPortal portal : this.config.getPortals()) {
                this.handleSingleBlockEvent(event, block, portal);
            }
        }
    }

    @EventHandler
    public void onMinecartPortalRetractedByPiston(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (!block.getType().equals(Material.DETECTOR_RAIL) && !block.getType().equals(Material.RAIL)) {
                return;
            }

            for (MinecartPortal portal : this.config.getPortals()) {
                this.handleSingleBlockEvent(event, block, portal);
            }
        }
    }

    /**
     * Handle potential destruction of a specific portal at a specific block.
     *
     * @param cancellable the cancellable event instance used to cancel the event on a match
     * @param block       the block that has been destroyed / affected by the event
     * @param portal      the portal that should be checked
     * @return whether the affected block was a portal (and event has been cancelled) or not
     */
    private boolean handleSingleBlockEvent(Cancellable cancellable, Block block, MinecartPortal portal) {
        Location entry = portal.getPortal();
        Location exit = portal.getDestination();

        if (block.getType().equals(Material.DETECTOR_RAIL)
                && this.isSimilarLocation(block.getLocation(), entry)
        ) {
            cancellable.setCancelled(true);
            return true;
        }

        if (block.getType().equals(Material.RAIL) && this.isSimilarLocation(block.getLocation(), exit)) {
            cancellable.setCancelled(true);
            return true;
        }

        return false;
    }

    /**
     * Compare whether the base location is similar to the comparison location.
     * <p>
     * A location is similar, if:
     * - It is in the same world
     * - The total (coordinate) distance is less than 1.0
     *
     * @param base    the base location to compare against
     * @param compare the location to compare
     * @return whether the location is similar or not
     */
    private boolean isSimilarLocation(Location base, Location compare) {
        return base != null
                && compare != null
                && base.getWorld().getUID().equals(compare.getWorld().getUID())
                && base.distance(compare) < MinecartPortalDestructionListener.LOCATION_SIMILARITY_DISTANCE;
    }
}
