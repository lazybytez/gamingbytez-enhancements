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
package de.lazybytez.gamingbytezenhancements.feature.minecartportal;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.AbstractFeature;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.command.MinecartPortalCommand;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.listener.MinecartPortalActivationListener;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.listener.MinecartPortalDestructionListener;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.model.MinecartPortal;
import de.lazybytez.gamingbytezenhancements.lib.command.CommandRegistrar;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePrefix;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.IOException;

/**
 * Feature that allows to create portals using Minecarts.
 */
public class MinecartPortalFeature extends AbstractFeature {
    private static final String FEATURE_NAME = "MinecartPortals";
    private static final NamedTextColor BRAND_COLOR = NamedTextColor.LIGHT_PURPLE;

    /**
     * The configuration that holds the Minecart Portals
     */
    private PortalConfiguration portalConfig;

    /**
     * The messenger carrying the Minecart Portals prefix, shared by every
     * player facing component this feature sends.
     */
    private final Messenger messenger;

    public MinecartPortalFeature(EnhancementsPlugin plugin) {
        super(plugin);

        this.messenger = new Messenger(MessagePrefix.of(
                MinecartPortalFeature.FEATURE_NAME, MinecartPortalFeature.BRAND_COLOR));
    }

    @Override
    public void onEnable() {
        // Register portal model as configuration serializable
        ConfigurationSerialization.registerClass(MinecartPortal.class);
        // Load portals for the first time
        this.portalConfig = this.loadPortals();
        if (portalConfig == null) {
            this.plugin.getLogger().severe("Failed to prepare configuration of Minecart Portals.");
            this.plugin.getLogger().severe("Initialization of Minecart Portals has been aborted.");
            this.plugin.getLogger().severe("The Minecart Portals feature won't be available!");

            return;
        }

        this.registerCommands();
        this.registerEvents();
    }

    /**
     * Register commands of the feature
     */
    private void registerCommands() {
        new CommandRegistrar(this.plugin)
                .register(new MinecartPortalCommand(this.plugin, this.portalConfig, this.messenger));
    }

    /**
     * Register events of the feature
     */
    private void registerEvents() {
        this.registerEvent(new MinecartPortalActivationListener(this));
        this.registerEvent(new MinecartPortalDestructionListener(this.portalConfig, this.messenger));
    }

    /**
     * Load the portals from configuration for the first time during server startup.
     *
     * @return the loaded configuration or null if loading failed.
     */
    private PortalConfiguration loadPortals() {
        PortalConfiguration config = new PortalConfiguration(this.plugin);
        try {
            config.loadSync();
        } catch (IOException | InvalidConfigurationException e) {
            return null;
        }

        return config;
    }

    @Override
    public void onDisable() {
        if (this.portalConfig == null) {
            return;
        }

        try {
            this.portalConfig.saveSync();
        } catch (IOException e) {
            this.plugin.getLogger().severe("Failed to save Minecart Portals on shutdown: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "Minecart Portal";
    }

    public PortalConfiguration getPortalConfig() {
        return portalConfig;
    }
}
