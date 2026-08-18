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
import de.lazybytez.gamingbytezenhancements.feature.chatbot.command.ChatBotCommand;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.event.ChatBotChatListener;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.messages.ChatBotMessages;
import de.lazybytez.gamingbytezenhancements.lib.command.CommandRegistrar;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePrefix;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import de.lazybytez.gamingbytezenhancements.lib.openai.OpenAiApiConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatBotFeature extends AbstractFeature {
    public static final String CONFIG_ENABLE_AI_BOT = "chatbot.enable_ai_answers";
    public static final String CONFIG_AI_BOT_PROMPT = "chatbot.prompt";
    public static final String CONFIG_AI_BOT_SYSTEM_PROMPT = "chatbot.system_prompt";
    public static final String CONFIG_AI_BOT_DISABLE_THINKING = "chatbot.disable_thinking";

    private static final NamedTextColor BRAND_COLOR = NamedTextColor.GREEN;

    private final List<ChatBotAction> chatBotActions = new CopyOnWriteArrayList<>();
    private final StaticResponseConfiguration staticResponseConfiguration;

    /**
     * The messenger carrying the Chat Bot prefix for operator facing output.
     * <p>
     * The bot's own chat lines stay outside it on purpose, because they imitate a player and a
     * prefix would give them away. Command feedback is ordinary plugin output and is prefixed.
     */
    private final Messenger messenger;

    private ChatBotChatListener chatBotChatListener;

    public ChatBotFeature(EnhancementsPlugin plugin) {
        super(plugin);

        this.staticResponseConfiguration = new StaticResponseConfiguration(plugin);
        this.messenger = new Messenger(MessagePrefix.of(this.getName(), ChatBotFeature.BRAND_COLOR));
    }

    @Override
    public void onEnable() {
        this.registerStaticResponseActions();
        this.registerLLMAction();
        this.registerEvents();
        this.registerCommands();
    }

    /**
     * Register configured static-response actions
     */
    private void registerStaticResponseActions() {
        try {
            this.staticResponseConfiguration.load();
            this.chatBotActions.addAll(this.staticResponseConfiguration.getActions());
        } catch (IOException | InvalidConfigurationException e) {
            this.getPlugin().getLogger().warning("Error while registering Static-Response Chat Bot Actions: " + e.getMessage());
        }
    }

    /**
     * Reload the static responses from disk without interrupting the running bot.
     * <p>
     * The file is read off the server thread. Only after a successful read is the static subset
     * of the action list replaced, back on the server thread, so a broken file keeps the previous
     * responses answering and a reload never leaves the bot without its actions.
     *
     * @param sender the sender the outcome is reported to
     */
    public void reloadStaticResponses(CommandSender sender) {
        this.staticResponseConfiguration.loadAsync(loaded ->
                this.getPlugin().getServer().getScheduler().runTask(this.getPlugin(), () -> {
                    if (!loaded) {
                        this.messenger.error(sender, ChatBotMessages.responsesReloadFailed());

                        return;
                    }

                    List<StaticResponseAction> freshActions = this.staticResponseConfiguration.getActions();
                    this.chatBotActions.removeIf(action -> action instanceof StaticResponseAction);
                    this.chatBotActions.addAll(freshActions);

                    this.messenger.success(sender, ChatBotMessages.responsesReloaded(freshActions.size()));
                }));
    }

    /**
     * Reload the chat bot settings from the plugin configuration.
     * <p>
     * The AI action is replaced rather than mutated: the next request builds from the fresh
     * settings, and disabling AI answers removes the action entirely. Replacing the action also
     * resets its rate limit window and its token usage counter.
     *
     * @param sender the sender the outcome is reported to
     */
    public void reloadAiSettings(CommandSender sender) {
        this.getPlugin().reloadConfig();

        this.chatBotActions.removeIf(action -> action instanceof ChatGPTAction);
        this.registerLLMAction();

        if (this.getPlugin().getConfig().getBoolean(ChatBotFeature.CONFIG_ENABLE_AI_BOT, false)) {
            this.messenger.success(sender, ChatBotMessages.settingsReloadedAiEnabled());

            return;
        }

        this.messenger.success(sender, ChatBotMessages.settingsReloadedAiDisabled());
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

    private void registerCommands() {
        new CommandRegistrar(this.getPlugin()).register(new ChatBotCommand(this, this.messenger));
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
