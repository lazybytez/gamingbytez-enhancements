package de.lazybytez.gamingbytezenhancements;

import de.lazybytez.gamingbytezenhancements.feature.Feature;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotFeature;
import de.lazybytez.gamingbytezenhancements.feature.farmlandprotection.FarmlandProtectionFeature;
import de.lazybytez.gamingbytezenhancements.feature.temporarycart.TemporaryCartFeature;
import de.lazybytez.gamingbytezenhancements.lib.openai.OpenAiApiConfig;
import de.lazybytez.gamingbytezenhancements.lib.openai.OpenAiClient;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

public final class EnhancementsPlugin extends JavaPlugin {
    private OpenAiClient openAiClient;

    private final Feature[] features = new Feature[]{
            new TemporaryCartFeature(this),
            new ChatBotFeature(this),
            new FarmlandProtectionFeature(this)
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
        this.getLogger().info("Preparing plugin configuration...");
        this.saveDefaultConfig();
        this.getLogger().info("Finished preparing plugin configuration...");

        this.initializeChatGptClient();

        this.getLogger().info(String.format("Enabling %d features...", this.getFeatures().length));

        for (Feature feature : this.getFeatures()) {
            this.getLogger().info(String.format("Enabling feature %s...", feature.getName()));
            feature.onEnable();
            this.getLogger().info(String.format("Enabled feature %s!", feature.getName()));
        }

        this.getLogger().info("All features have been enabled!");
    }

    private void initializeChatGptClient() {
        this.getLogger().info("Preparing OpenAI client...");

        try {
            this.openAiClient = new OpenAiClient(
                    OpenAiApiConfig.getConfigValue(this, OpenAiApiConfig.OPENAI_URL),
                    OpenAiApiConfig.getConfigValue(this, OpenAiApiConfig.OPENAI_API_KEY),
                    OpenAiApiConfig.getConfigValue(this, OpenAiApiConfig.OPENAI_ORGANIZATION),
                    OpenAiApiConfig.getConfigValue(this, OpenAiApiConfig.OPENAI_MODEL)
            );
        } catch (InvalidConfigurationException e) {
            this.getLogger().severe("Failed to initialize OpenAI client: " + e.getMessage());
        }

        this.getLogger().info("Successfully prepared OpenAI client...");
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

    public OpenAiClient getOpenAiClient() {
        return openAiClient;
    }

    private Feature[] getFeatures() {
        return this.features;
    }
}
