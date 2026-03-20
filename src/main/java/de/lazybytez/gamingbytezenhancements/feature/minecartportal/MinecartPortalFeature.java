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
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.IOException;
import java.util.List;

/**
 * Feature that allows to create portals using Minecarts.
 */
public class MinecartPortalFeature extends AbstractFeature {
    /**
     * The configuration that holds the Minecart Portals
     */
    private PortalConfiguration portalConfig;

    public MinecartPortalFeature(EnhancementsPlugin plugin) {
        super(plugin);
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
        this.plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(
                    "minecartportals",
                    "Manage the Minecart Portals of the GamingBytez Enhancements plugin",
                    List.of("gbmcp"),
                    new MinecartPortalCommand(this)
            );
        });
    }

    /**
     * Register events of the feature
     */
    private void registerEvents() {
        this.registerEvent(new MinecartPortalActivationListener(this));
        this.registerEvent(new MinecartPortalDestructionListener(this.portalConfig));
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
    public String getName() {
        return "Minecart Portal";
    }

    public PortalConfiguration getPortalConfig() {
        return portalConfig;
    }
}
