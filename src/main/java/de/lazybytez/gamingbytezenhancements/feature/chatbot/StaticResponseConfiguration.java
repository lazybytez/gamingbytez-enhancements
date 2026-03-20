package de.lazybytez.gamingbytezenhancements.feature.chatbot;

import de.lazybytez.gamingbytezenhancements.feature.chatbot.actions.StaticResponseAction;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

/**
 * Service that handles staic-response-action configuration.
 * <p>
 * This service deals with the configuration of static-response-action-chat-bots.
 */
public class StaticResponseConfiguration {
    /**
     * Name of the configuration file that will hold all static-response-actions.
     */
    protected static final String STATIC_RESPONSE_CONFIG_FILE = "static_response_chat_bot.yaml";

    /**
     * Key in the YAML configuration that stores the static-response-actions.
     */
    protected static final String STATIC_RESPONSE_CONFIG_KEY = "static_response";

    /**
     * Plugin instance used to locate the static_response configuration file.
     */
    private final Plugin plugin;

    /**
     * The config instance that was last loaded.
     */
    private final YamlConfiguration config;

    /**
     * The list of available static-response.
     */
    private volatile CopyOnWriteArrayList<StaticResponseAction> actions;

    /**
     * The value if the actions were loaded.
     */
    private boolean wasLoaded = false;

    public StaticResponseConfiguration(Plugin plugin) {
        this.plugin = plugin;
        this.config = new YamlConfiguration();
        this.actions = new CopyOnWriteArrayList<>();
    }

    /**
     * Load the configuration from disk synchronously.
     * <p>
     * This function loads the configuration synchronously from disk.
     * If the configuration file does not exist, it is created and prepared to store
     * portals.
     *
     * @throws IOException                   if any file operation fails
     * @throws InvalidConfigurationException when the configuration format is invalid
     */
    public synchronized void load() throws IOException, InvalidConfigurationException {
        // Load config in a thread safe manner
        try {
            this.plugin.getLogger().info("Loading static-response-chat-bot file...");

            File file = this.getConfigurationFile();
            this.ensureFileExists(file);

            // We do not use YamlConfiguration.loadConfiguration as we want to handle
            // exceptions our own. this.config is initialized in constructor
            this.config.load(file);

            this.plugin.getLogger().info("Loaded static-response-chat-bot file!");
        } catch (IOException | InvalidConfigurationException e) {
            this.plugin.getLogger().log(
                    Level.SEVERE,
                    "Failed to load static-response-chat-bot file!",
                    e
            );

            throw e;
        }

        // Handle fresh configuration that is still empty
        boolean staticResponsesConfigured = config.isSet(StaticResponseConfiguration.STATIC_RESPONSE_CONFIG_KEY);

        if (!staticResponsesConfigured) {
            this.plugin.getLogger().info("Static Response storage is missing!");
            return;
        }

        this.plugin.getLogger().info("Parsing static-response-action instances from configuration...");
        this.actions = this.getStaticResponseActionsFromConfig(config);
        this.plugin.getLogger().info("Finished parsing static-response-action instances from configuration!");
        this.wasLoaded = true;
    }

    /**
     * Get the list of actions
     *
     * @return the configured static-response actions
     */
    public CopyOnWriteArrayList<StaticResponseAction> getActions() {
        if (!wasLoaded) {
            this.plugin.getLogger().warning("Static Response Configuration wasn't loaded before. Please use the `loadSync` function before.");
        }
        return this.actions;
    }

    /**
     * Get portals from the configuration in a type safe manner.
     *
     * @param config the config to load the portals from
     * @return a list of minecart portal instances
     */
    private CopyOnWriteArrayList<StaticResponseAction> getStaticResponseActionsFromConfig(YamlConfiguration config) {
        List<?> rawActions = config.getList(StaticResponseConfiguration.STATIC_RESPONSE_CONFIG_KEY, new ArrayList<>());

        CopyOnWriteArrayList<StaticResponseAction> loadedActions = new CopyOnWriteArrayList<>();
        for (Object o : rawActions) {
            if (o instanceof LinkedHashMap<? ,?>) {
                StaticResponseAction current = this.getStaticResponseActionFromConfig((LinkedHashMap<?, ?>) o);
                if (current != null) {
                    loadedActions.add(this.getStaticResponseActionFromConfig((LinkedHashMap<?, ?>) o));
                }
            }
        }

        return loadedActions;
    }

    /**
     * Get the static-response-action from the configuration-part in a type safe manner.
     *
     * @param config the config to load the portals from
     * @return the static response action from the config or null if the config was not correct
     */
    private StaticResponseAction getStaticResponseActionFromConfig(LinkedHashMap<?, ?> config) {
        Object responseMessageRaw = config.get("responseMessage");
        if (!(responseMessageRaw instanceof String responseMessage)) {
            this.plugin.getLogger().warning("responseMessage not of type String, skip static-response");

            return null;
        }

        Object buzzwordsRaw = config.get("buzzwords");
        if (!(buzzwordsRaw instanceof ArrayList<?>)) {
            this.plugin.getLogger().warning("buzzwords not of type ArrayList, skip static-response");

            return null;
        }
        ArrayList<String> buzzwords = new ArrayList<>();
        for (Object buzzword: (ArrayList<?>) buzzwordsRaw) {
            if (!(buzzword instanceof String)) {
                this.plugin.getLogger().warning("buzzword in not of type string, skip buzzword");

                continue;
            }
            buzzwords.add((String) buzzword);
        }

        Object numeratorRaw = config.get("numerator");
        if (!(numeratorRaw instanceof Integer numerator)) {
            this.plugin.getLogger().warning("numerator not of type Integer, skip static-response");

            return null;
        }


        Object denominatorRaw = config.get("denominator");
        if (!(denominatorRaw instanceof Integer denominator)) {
            this.plugin.getLogger().warning("denominator not of type Integer, skip static-response");

            return null;
        }

        return new StaticResponseAction(responseMessage, buzzwords.toArray(new String[]{}), numerator, denominator);
    }

    /**
     * Ensure that the static-response-chat-bot configuration file exists.
     *
     * @param configFile is the file that should be created (if it does not exist)
     * @throws IOException thrown when the directory path or file could not be created
     */
    private void ensureFileExists(File configFile) throws IOException {
        // Ignore result of mkdirs, as in this case it does not matter if
        // directories where actually created or not.
        //noinspection ResultOfMethodCallIgnored
        configFile.getParentFile().mkdirs();

        if (!configFile.exists()) {
            // Here the same is true as for mkdirs, the result does not matter.
            // Only exceptions matter.
            //noinspection ResultOfMethodCallIgnored
            configFile.createNewFile();
        }
    }

    /**
     * Get a file instance pointing to the static-response-chat-bot configuration files location.
     *
     * @return the file instance pointing to the configuration file.
     */
    private File getConfigurationFile() {
        return new File(this.plugin.getDataFolder(), StaticResponseConfiguration.STATIC_RESPONSE_CONFIG_FILE);
    }
}
