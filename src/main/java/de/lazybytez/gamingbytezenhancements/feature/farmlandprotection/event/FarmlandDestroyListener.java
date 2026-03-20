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
package de.lazybytez.gamingbytezenhancements.feature.farmlandprotection.event;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class FarmlandDestroyListener implements Listener {
    @EventHandler
    public void onPlayerFarmlandDestroy(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.PHYSICAL)) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) {
            return;
        }

        if (!clickedBlock.getType().equals(Material.FARMLAND)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityFarmlandDestroy(EntityInteractEvent event) {
        if (!event.getBlock().getType().equals(Material.FARMLAND)) {
            return;
        }

        event.setCancelled(true);
    }
}
