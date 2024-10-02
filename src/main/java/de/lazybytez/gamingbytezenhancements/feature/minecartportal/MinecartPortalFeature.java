package de.lazybytez.gamingbytezenhancements.feature.minecartportal;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.AbstractFeature;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Feature that allows to create portals using Minecarts.
 */
public class MinecartPortalFeature extends AbstractFeature {
    public MinecartPortalFeature(EnhancementsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        PortalConfiguration config = this.loadPortals();
        if (config == null) {
            this.plugin.getLogger().severe("Failed to prepare configuration of Minecart Portals.");
            this.plugin.getLogger().severe("Initialization of Minecart Portals has been aborted.");
            this.plugin.getLogger().severe("The Minecart Portals feature won't be available!");

            return;
        }
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
