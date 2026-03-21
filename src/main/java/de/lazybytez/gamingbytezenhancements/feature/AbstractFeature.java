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
 * Base class for all features.
 * <p>
 * Provides a plugin reference, no-op defaults for {@link #onLoad()} and {@link #onDisable()},
 * and a config-backed {@link #isEnabled()} implementation that reads
 * {@code features.<configKey>} from {@code config.yml}, defaulting to {@code true}.
 * </p>
 * <p>
 * Subclasses must implement {@link #onEnable()} and {@link #getName()} at minimum.
 * Override {@link #getConfigKey()} only when the derived key (spaces stripped from the name)
 * does not match the desired config entry.
 * </p>
 */
public abstract class AbstractFeature implements Feature {
    protected final EnhancementsPlugin plugin;
    private Boolean enabled;

    /**
     * @param plugin the owning plugin instance, used for config access and event registration
     */
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
     * Returns the configuration key used to toggle this feature under the {@code features} section.
     * <p>
     * Derived from {@link #getName()} with spaces removed (e.g. "Farmland Protection" → "FarmlandProtection").
     * Override if a feature requires a custom key.
     * </p>
     */
    protected String getFeatureConfigKey() {
        return this.getName().replace(" ", "");
    }

    /**
     * {@inheritDoc}
     * <p>
     * The result is read from config on the first call and cached for the lifetime of the feature,
     * since toggling features at runtime is not supported.
     * </p>
     */
    @Override
    public boolean isEnabled() {
        if (this.enabled == null) {
            this.enabled = this.plugin.getConfig().getBoolean(
                    String.format("features.%s", this.getFeatureConfigKey()),
                    true
            );
        }

        return this.enabled;
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

    /**
     * @return the owning plugin instance
     */
    public EnhancementsPlugin getPlugin() {
        return plugin;
    }
}
