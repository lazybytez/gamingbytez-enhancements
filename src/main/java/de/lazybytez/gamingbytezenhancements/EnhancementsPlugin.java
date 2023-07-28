package de.lazybytez.gamingbytezenhancements;

import de.lazybytez.gamingbytezenhancements.feature.Feature;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotFeature;
import de.lazybytez.gamingbytezenhancements.feature.temporarycart.TemporaryCartFeature;
import org.bukkit.plugin.java.JavaPlugin;

public final class EnhancementsPlugin extends JavaPlugin {
    private final Feature[] features = new Feature[]{
        new TemporaryCartFeature(this),
        new ChatBotFeature(this),
    };

    @Override
    public void onLoad() {
        this.getLogger().info(String.format("Loading %d features...", this.getFeatures().length));

        for (Feature feature : this.getFeatures()) {
            this.getLogger().info(String.format("Loading feature %s...", feature.getName()));
            feature.onLoad();
            this.getLogger().info(String.format("Loaded feature %s!", feature.getName()));
        }

        this.getLogger().info("All features have been loaded!");
    }

    @Override
    public void onEnable() {
        this.getLogger().info(String.format("Enabling %d features...", this.getFeatures().length));

        for (Feature feature : this.getFeatures()) {
            this.getLogger().info(String.format("Enabling feature %s...", feature.getName()));
            feature.onEnable();
            this.getLogger().info(String.format("Enabled feature %s!", feature.getName()));
        }

        this.getLogger().info("All features have been enabled!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info(String.format("Disabling %d features...", this.getFeatures().length));

        for (Feature feature : this.getFeatures()) {
            this.getLogger().info(String.format("Disabling feature %s...", feature.getName()));
            feature.onDisable();
            this.getLogger().info(String.format("Disabled feature %s!", feature.getName()));
        }

        this.getLogger().info("All features have been disabled!");
    }

    private Feature[] getFeatures() {
        return this.features;
    }
}
