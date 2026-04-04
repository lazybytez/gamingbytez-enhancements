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
package de.lazybytez.gamingbytezenhancements.feature.chatbot;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.AbstractFeature;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.actions.*;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.event.ChatBotChatListener;
import de.lazybytez.gamingbytezenhancements.lib.openai.OpenAiApiConfig;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatBotFeature extends AbstractFeature {
    public static final String CONFIG_ENABLE_AI_BOT = "chatbot.enable_ai_answers";
    public static final String CONFIG_AI_BOT_PROMPT = "chatbot.prompt";
    public static final String CONFIG_AI_BOT_SYSTEM_PROMPT = "chatbot.system_prompt";
    public static final String CONFIG_AI_BOT_DISABLE_THINKING = "chatbot.disable_thinking";

    private final List<ChatBotAction> chatBotActions = new CopyOnWriteArrayList<>();
    private ChatBotChatListener chatBotChatListener;

    public ChatBotFeature(EnhancementsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        this.registerStaticResponseActions();
        this.registerLLMAction();
        this.registerEvents();
    }

    /**
     * Register configured static-response actions
     */
    private void registerStaticResponseActions() {
        try {
            StaticResponseConfiguration configuration = new StaticResponseConfiguration(this.getPlugin());
            configuration.load();
            this.chatBotActions.addAll(configuration.getActions());
        } catch (IOException | InvalidConfigurationException e) {
            this.getPlugin().getLogger().warning("Error while registering Static-Response Chat Bot Actions: " + e.getMessage());
        }
    }

    /**
     * Register LLM Action
     */
    private void registerLLMAction() {
        // Add AI responses - only when enabled
        if (this.getPlugin().getConfig().getBoolean(ChatBotFeature.CONFIG_ENABLE_AI_BOT, false)) {
            String prompt = this.getPlugin().getConfig().getString(ChatBotFeature.CONFIG_AI_BOT_PROMPT);
            if (prompt == null) {
                this.getPlugin().getLogger().warning("AI ChatBot feature is enabled, but no prompt is configured. Prompt will be empty!");
                prompt = "";
            }

            String systemPrompt = OpenAiApiConfig.getOptionalStringConfigValue(
                    this.getPlugin(), ChatBotFeature.CONFIG_AI_BOT_SYSTEM_PROMPT
            );
            boolean disableThinking = this.getPlugin().getConfig().getBoolean(
                    ChatBotFeature.CONFIG_AI_BOT_DISABLE_THINKING, false
            );

            this.chatBotActions.add(new ChatGPTAction(this.getPlugin(), prompt, systemPrompt, disableThinking));
        }
    }

    private void registerEvents() {
        this.chatBotChatListener = new ChatBotChatListener(this);
        this.registerEvent(this.chatBotChatListener);
    }

    @Override
    public void onDisable() {
        if (this.chatBotChatListener == null) {
            return;
        }

        this.chatBotChatListener.shutdown();
    }

    public List<ChatBotAction> getChatBotActions() {
        return this.chatBotActions;
    }

    @Override
    public String getName() {
        return "ChatBot";
    }
}
