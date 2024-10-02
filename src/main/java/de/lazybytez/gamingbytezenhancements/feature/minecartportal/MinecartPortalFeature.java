package de.lazybytez.gamingbytezenhancements.feature.minecartportal;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.AbstractFeature;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.command.MinecartPortalCommand;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.configuration.InvalidConfigurationException;

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
        this.portalConfig = this.loadPortals();
        if (portalConfig == null) {
            this.plugin.getLogger().severe("Failed to prepare configuration of Minecart Portals.");
            this.plugin.getLogger().severe("Initialization of Minecart Portals has been aborted.");
            this.plugin.getLogger().severe("The Minecart Portals feature won't be available!");

            return;
        }

        this.registerCommands();
    }

    /**
     * Register commands of the plugin
     */
    private void registerCommands() {
        this.plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(
                    "minecartportals",
                    "Manage the Minecart Portals of the GamingBytez Enhancements plugin",
                    List.of("gbmcp"),
                    new MinecartPortalCommand(this.portalConfig)
            );
        });
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
        } catch (IOException|InvalidConfigurationException e) {
            return null;
        }

        return config;
    }

    @Override
    public String getName() {
        return "Minecart Portal";
    }
}
