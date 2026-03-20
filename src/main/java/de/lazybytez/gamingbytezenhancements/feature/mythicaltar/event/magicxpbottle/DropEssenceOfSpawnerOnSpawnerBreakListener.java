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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.magicxpbottle;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle.EssenceOfSpawnerManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Event listener to add "Essence of Spawner" as drops when a spawner is broken.
 */
public class DropEssenceOfSpawnerOnSpawnerBreakListener implements Listener {

    private final MythicAltarFeature mythicAltarFeature;

    public DropEssenceOfSpawnerOnSpawnerBreakListener(MythicAltarFeature mythicAltarFeature) {
        this.mythicAltarFeature = mythicAltarFeature;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getBlock().getType().equals(Material.SPAWNER)) {
            return;
        }

        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }

        ItemStack additionalDrop = this.mythicAltarFeature
                .getCustomItemManagerRegistry()
                .getCustomItemManager(EssenceOfSpawnerManager.class)
                .createCustomItem();

        event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), additionalDrop);

    }
}
