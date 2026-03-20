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
package de.lazybytez.gamingbytezenhancements.feature;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import org.bukkit.event.Listener;

/**
 * Abstract class that all features extend.
 * <p>
 * It provides a reference to the plugin instance.
 * It also provides empty implementations of onLoad() and onDisable(),
 * as they are not required for most features.
 * </p>
 */
public abstract class AbstractFeature implements Feature {
    protected final EnhancementsPlugin plugin;

    public AbstractFeature(EnhancementsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onDisable() {
    }

    /**
     * Registers an event listener.
     *
     * @param listener The listener to register.
     */
    protected void registerEvent(Listener listener) {
        this.plugin.getServer().getPluginManager().registerEvents(
                listener,
                this.getPlugin()
        );
    }

    public EnhancementsPlugin getPlugin() {
        return plugin;
    }
}
