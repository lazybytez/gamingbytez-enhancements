package de.lazybytez.gamingbytezenhancements.feature;

/**
 * Interface that provides the entry point for all features.
 */
public interface Feature {
    /**
     * Called when the plugin is loaded.
     */
    void onLoad();

    /**
     * Called when the plugin is enabled.
     */
    void onEnable();

    /**
     * Called when the plugin is disabled.
     */
    void onDisable();

    /**
     * Returns the name of the feature.
     */
    String getName();
}
