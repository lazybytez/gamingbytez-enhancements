package de.lazybytez.gamingbytezenhancements.lib.openai;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;

public class OpenAiApiConfig {
    public static final String OPENAI_URL = "openai.apiUrl";
    public static final String OPENAI_API_KEY = "openai.apiKey";
    public static final String OPENAI_ORGANIZATION = "openai.organizationId";
    public static final String OPENAI_MODEL = "openai.model";
    public static final String OPENAI_TEMPERATURE = "openai.temperature";

    private static final String DEFAULT_ERROR = "The configuration for OpenAI is missing property: ";

    public static String getStringConfigValue(Plugin plugin, String path) throws InvalidConfigurationException {
        String value = plugin.getConfig().getString(path);

        if (value == null) {
            throw new InvalidConfigurationException(DEFAULT_ERROR + path);
        }

        return value;
    }

    public static double getDoubleConfigValue(Plugin plugin, String path) throws InvalidConfigurationException {
        return plugin.getConfig().getDouble(path);
    }
}
