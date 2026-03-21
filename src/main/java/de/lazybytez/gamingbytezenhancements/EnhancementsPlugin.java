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
package de.lazybytez.gamingbytezenhancements;

import de.lazybytez.gamingbytezenhancements.feature.Feature;
import de.lazybytez.gamingbytezenhancements.feature.antimobgriefing.AntiMobGriefingFeature;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotFeature;
import de.lazybytez.gamingbytezenhancements.feature.customcreeperdamage.CustomCreeperDamageFeature;
import de.lazybytez.gamingbytezenhancements.feature.customloot.CustomLootFeature;
import de.lazybytez.gamingbytezenhancements.feature.farmlandprotection.FarmlandProtectionFeature;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.MinecartPortalFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.temporarycart.TemporaryCartFeature;
import de.lazybytez.gamingbytezenhancements.lib.openai.OpenAiApiConfig;
import de.lazybytez.gamingbytezenhancements.lib.openai.OpenAiClient;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

public final class EnhancementsPlugin extends JavaPlugin {
    private final Feature[] features = new Feature[]{
            new TemporaryCartFeature(this),
            new ChatBotFeature(this),
            new FarmlandProtectionFeature(this),
            new AntiMobGriefingFeature(this),
            new CustomCreeperDamageFeature(this),
            new MythicAltarFeature(this),
            new CustomLootFeature(this),
            new MinecartPortalFeature(this),
    };
    private OpenAiClient openAiClient;

    @Override
    public void onLoad() {
        this.getLogger().info("Preparing plugin configuration...");
        this.saveDefaultConfig();
        this.getLogger().info("Finished preparing plugin configuration.");

        this.getLogger().info(String.format("Loading %d features...", this.getFeatures().length));

        for (Feature feature : this.getFeatures()) {
            if (!feature.isEnabled()) {
                this.getLogger().info(String.format("Skipping disabled feature %s.", feature.getName()));
                continue;
            }

            this.getLogger().info(String.format("Loading feature %s...", feature.getName()));
            feature.onLoad();
            this.getLogger().info(String.format("Loaded feature %s!", feature.getName()));
        }

        this.getLogger().info("All features have been loaded!");
    }

    @Override
    public void onEnable() {
        this.initializeOpenAiClient();

        this.getLogger().info(String.format("Enabling %d features...", this.getFeatures().length));

        for (Feature feature : this.getFeatures()) {
            if (!feature.isEnabled()) {
                this.getLogger().info(String.format("Feature %s is disabled and will not be enabled.", feature.getName()));
                continue;
            }

            this.getLogger().info(String.format("Enabling feature %s...", feature.getName()));
            feature.onEnable();
            this.getLogger().info(String.format("Enabled feature %s!", feature.getName()));
        }

        this.getLogger().info("All features have been enabled!");
    }

    private void initializeOpenAiClient() {
        this.getLogger().info("Preparing OpenAI client...");

        try {
            this.openAiClient = new OpenAiClient(
                    OpenAiApiConfig.getStringConfigValue(this, OpenAiApiConfig.OPENAI_URL),
                    OpenAiApiConfig.getStringConfigValue(this, OpenAiApiConfig.OPENAI_API_KEY),
                    OpenAiApiConfig.getStringConfigValue(this, OpenAiApiConfig.OPENAI_ORGANIZATION),
                    OpenAiApiConfig.getStringConfigValue(this, OpenAiApiConfig.OPENAI_MODEL),
                    OpenAiApiConfig.getDoubleConfigValue(this, OpenAiApiConfig.OPENAI_TEMPERATURE)
            );
            this.getLogger().info("Successfully prepared OpenAI client.");
        } catch (InvalidConfigurationException e) {
            this.getLogger().severe("Failed to initialize OpenAI client: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        this.getLogger().info(String.format("Disabling %d features...", this.getFeatures().length));

        for (Feature feature : this.getFeatures()) {
            if (!feature.isEnabled()) {
                this.getLogger().info(String.format("Skipping disabled feature %s.", feature.getName()));
                continue;
            }

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
