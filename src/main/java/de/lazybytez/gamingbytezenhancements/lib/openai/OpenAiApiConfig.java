package de.lazybytez.gamingbytezenhancements.lib.openai;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.Plugin;

public class OpenAiApiConfig {
    public static final String OPENAI_URL = "openai.apiUrl";
    public static final String OPENAI_API_KEY = "openai.apiKey";
    public static final String OPENAI_ORGANIZATION = "openai.organizationId";
    public static final String OPENAI_MODEL = "openai.model";

    private static final String DEFAULT_ERROR = "The configuration for OpenAI is missing property: ";

    public static String getConfigValue(Plugin plugin, String path) throws InvalidConfigurationException {
        String apiUrl = plugin.getConfig().getString(path);

        if (apiUrl == null) {
            throw new InvalidConfigurationException(DEFAULT_ERROR + path);
        }

        return apiUrl;
    }
}
