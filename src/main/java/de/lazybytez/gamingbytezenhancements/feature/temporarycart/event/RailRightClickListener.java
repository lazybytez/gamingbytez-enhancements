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

import de.lazybytez.gamingbytezenhancements.feature.temporarycart.TemporaryCartManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Rail;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Event that handles spawning a temporary cart when a player right-clicks a rail.
 */
public class RailRightClickListener implements Listener {
    private final TemporaryCartManager temporaryCartManager;

    private final List<Material> minecartTypes = new ArrayList<>(List.of(
            Material.MINECART,
            Material.CHEST_MINECART,
            Material.FURNACE_MINECART,
            Material.HOPPER_MINECART,
            Material.TNT_MINECART
    ));

    public RailRightClickListener(TemporaryCartManager temporaryCartManager) {
        this.temporaryCartManager = temporaryCartManager;
    }

    @EventHandler
    public void onRailRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null || !(block.getBlockData() instanceof Rail)) {
            return;
        }

        Player p = event.getPlayer();
        ItemStack item = event.getItem();
        if (item != null && this.minecartTypes.contains(item.getType())) {
            return;
        }

        event.setCancelled(true);
        this.temporaryCartManager.spawnTemporaryCart(p, block.getLocation());
    }

}
