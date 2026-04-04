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

    /**
     * Reads an optional string config value, returning {@code null} if the key is absent or empty.
     *
     * @param plugin the plugin instance
     * @param path   the config path
     * @return the value, or {@code null} if absent/blank
     */
    public static String getOptionalStringConfigValue(Plugin plugin, String path) {
        String value = plugin.getConfig().getString(path);
        if (value == null || value.isBlank()) {
            return null;
        }

        return value;
    }

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
