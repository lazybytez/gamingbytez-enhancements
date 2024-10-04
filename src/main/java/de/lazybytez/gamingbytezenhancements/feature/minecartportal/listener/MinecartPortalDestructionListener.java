package de.lazybytez.gamingbytezenhancements.feature.minecartportal.listener;

import de.lazybytez.gamingbytezenhancements.feature.minecartportal.PortalConfiguration;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.model.MinecartPortal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
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
    private final PortalConfiguration config;

    public MinecartPortalDestructionListener(PortalConfiguration config) {
        this.config = config;
    }

    @EventHandler
    public void onMinecartPortalDestroyedByPlayer(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (!block.getType().equals(Material.DETECTOR_RAIL) && !block.getType().equals(Material.RAIL)) {
            return;
        }

        for (MinecartPortal portal : this.config.getPortals()) {
            Location entry = portal.getPortal();
            Location exit = portal.getDestination();

            if (block.getType().equals(Material.DETECTOR_RAIL)
                    && entry != null
                    && block.getLocation().distance(entry) < 1.0
            ) {
                event.setCancelled(true);
                player.sendMessage("Please remove the Minecart Portal first before breaking this detector rail!");
                return;
            }

            if (block.getType().equals(Material.RAIL)
                    && exit != null
                    && block.getLocation().distance(exit) < 1.0
            ) {
                event.setCancelled(true);
                player.sendMessage("Please remove the Minecart Portal first before breaking this rail!");
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
            Location entry = portal.getPortal();
            Location exit = portal.getDestination();

            if (block.getType().equals(Material.DETECTOR_RAIL)
                    && entry != null
                    && block.getLocation().distance(entry) < 1.0
            ) {
                event.setCancelled(true);
                return;
            }

            if (block.getType().equals(Material.RAIL)
                    && exit != null
                    && block.getLocation().distance(exit) < 1.0
            ) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMinecartPortalDestroyedByExplosion(EntityExplodeEvent event) {
        List<Block> portalBlocks = new ArrayList<>();

        for (Block block : event.blockList()) {
            if (!block.getType().equals(Material.DETECTOR_RAIL) && !block.getType().equals(Material.RAIL)) {
                continue;
            }

            for (MinecartPortal portal : this.config.getPortals()) {
                Location entry = portal.getPortal();
                Location exit = portal.getDestination();

                if (block.getType().equals(Material.DETECTOR_RAIL)
                        && entry != null
                        && block.getLocation().distance(entry) < 1.0
                ) {
                    portalBlocks.add(block);
                    continue;
                }

                if (block.getType().equals(Material.RAIL)
                        && exit != null
                        && block.getLocation().distance(exit) < 1.0
                ) {
                    portalBlocks.add(block);
                }
            }
        }

        event.blockList().removeAll(portalBlocks);
    }

    @EventHandler
    public void onMinecartPortalExtendedByPiston(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (!block.getType().equals(Material.DETECTOR_RAIL) && !block.getType().equals(Material.RAIL)) {
                return;
            }

            for (MinecartPortal portal : this.config.getPortals()) {
                Location entry = portal.getPortal();
                Location exit = portal.getDestination();

                if (block.getType().equals(Material.DETECTOR_RAIL)
                        && entry != null
                        && block.getLocation().distance(entry) < 1.0
                ) {
                    event.setCancelled(true);
                    return;
                }

                if (block.getType().equals(Material.RAIL)
                        && exit != null
                        && block.getLocation().distance(exit) < 1.0
                ) {
                    event.setCancelled(true);
                }
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
                Location entry = portal.getPortal();
                Location exit = portal.getDestination();

                if (block.getType().equals(Material.DETECTOR_RAIL)
                        && entry != null
                        && block.getLocation().distance(entry) < 1.0
                ) {
                    event.setCancelled(true);
                    return;
                }

                if (block.getType().equals(Material.RAIL)
                        && exit != null
                        && block.getLocation().distance(exit) < 1.0
                ) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
