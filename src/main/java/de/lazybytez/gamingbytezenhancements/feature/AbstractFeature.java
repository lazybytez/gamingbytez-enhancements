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
