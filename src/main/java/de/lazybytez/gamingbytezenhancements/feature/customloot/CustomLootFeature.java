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
package de.lazybytez.gamingbytezenhancements.feature.customloot;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.AbstractFeature;
import de.lazybytez.gamingbytezenhancements.feature.customloot.listener.ParchedCustomLootListener;
import de.lazybytez.gamingbytezenhancements.feature.customloot.listener.EndermanCustomLootListener;
import de.lazybytez.gamingbytezenhancements.feature.customloot.listener.HuskCustomLootListener;
import de.lazybytez.gamingbytezenhancements.feature.customloot.service.EnchantmentLevelOnItemDeterminer;

/**
 * Feature that adds listeners that provide custom loot to entities.
 */
public class CustomLootFeature extends AbstractFeature {
    public CustomLootFeature(EnhancementsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        this.registerEvents();
    }

    private void registerEvents() {
        this.registerEvent(new HuskCustomLootListener(new EnchantmentLevelOnItemDeterminer()));
        this.registerEvent(new EndermanCustomLootListener(new EnchantmentLevelOnItemDeterminer()));
        this.registerEvent(new ParchedCustomLootListener(new EnchantmentLevelOnItemDeterminer()));
    }

    @Override
    public String getName() {
        return "Custom Loot";
    }
}
