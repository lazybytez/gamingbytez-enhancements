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
package de.lazybytez.gamingbytezenhancements.feature.chatbot.actions;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotResponse;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotTarget;
import de.lazybytez.gamingbytezenhancements.lib.util.ChanceUtil;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.util.MessageAnalyzer;
import de.lazybytez.gamingbytezenhancements.lib.openai.OpenAiException;
import de.lazybytez.gamingbytezenhancements.lib.openai.OpenAiResponse;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class ChatGPTAction implements ChatBotAction {
    public static final long OPEN_AI_RATE_LIMIT = 60;
    private static final String[] BUZZWORDS = {
            "wer",
            "was",
            "wann",
            "wo",
            "warum",
            "wie",
            "welche",
            "welcher",
            "welches",
            "wollen",
            "wolltest",
            "wann",
            "wie\\sviele",
            "kannst\\sdu",
            "weißt\\sdu",
            "denkst\\sdu",
            "glaubst\\sdu",
            "ist\\ses\\smöglich",
            "was\\sdenkst\\sdu\\süber",
            "willst\\sdu",
            "hast\\sdu"
    };
    private final EnhancementsPlugin enhancementsPlugin;

    private final String promptTemplate;
    private final String systemPrompt;
    private final boolean disableThinking;

    private final AtomicLong lastAction = new AtomicLong(0L);

    private final AtomicLong totalTokensUsed = new AtomicLong(0L);

    /**
     * @param enhancementsPlugin the plugin instance
     * @param promptTemplate     user prompt template with {@code %s} placeholder for the message
     * @param systemPrompt       optional system prompt; {@code null} to omit from requests
     * @param disableThinking    when {@code true}, sends {@code chat_template_kwargs} to disable model thinking
     */
    public ChatGPTAction(
            EnhancementsPlugin enhancementsPlugin,
            String promptTemplate,
            String systemPrompt,
            boolean disableThinking
    ) {
        this.enhancementsPlugin = enhancementsPlugin;
        this.promptTemplate = promptTemplate;
        this.systemPrompt = systemPrompt;
        this.disableThinking = disableThinking;
    }

    @Override
    public ChatBotResponse getChatBotMessage(String message, Player sender, Set<Audience> receivers) {
        if (message.length() > 256) {
            this.enhancementsPlugin.getLogger().warning("Received a chat message with more than 256 characters. ChatGPT action will be skipped!");

            return null;
        }

        if (this.enhancementsPlugin.getOpenAiClient() == null) {
            this.enhancementsPlugin.getLogger().severe(
                    "Cannot send OpenAI chat bot response as client was not initialized!"
            );
            return null;
        }

        long lastActionSeconds = (System.currentTimeMillis() - this.lastAction.get()) / 1000;
        if (lastActionSeconds < OPEN_AI_RATE_LIMIT) {
            this.enhancementsPlugin.getLogger().info(String.format(
                    "ChatGPT action was triggered for message \"%s\". However, the action is currently on cool down for another %d seconds (%d in total).",
                    message,
                    OPEN_AI_RATE_LIMIT - lastActionSeconds,
                    OPEN_AI_RATE_LIMIT
            ));

            return null;
        }

        try {
            this.enhancementsPlugin.getLogger().info(String.format("Requesting answer for \"%s\" from OpenAI...", message));
            this.lastAction.set(System.currentTimeMillis());
            OpenAiResponse response = this.enhancementsPlugin
                    .getOpenAiClient()
                    .completion(
                            String.format(this.promptTemplate, message),
                            this.systemPrompt,
                            this.disableThinking
                    );

            if (response.content() == null || response.content().isEmpty()) {
                this.enhancementsPlugin.getLogger().info(String.format(
                        "Received answer for \"%s\" from OpenAI is empty!",
                        message
                ));

                return null;
            }

            this.enhancementsPlugin.getLogger().info(String.format(
                    "Received answer \"%s\" for input message \"%s\" from OpenAI and used %d tokens",
                    response.content(),
                    message,
                    response.totalTokens()
            ));
            this.totalTokensUsed.addAndGet(response.totalTokens());
            this.enhancementsPlugin.getLogger().info(String.format(
                    "The server has used a total of %d OpenAI tokens since the last restart!",
                    this.totalTokensUsed.get()
            ));

            return new ChatBotResponse(
                    false,
                    null,
                    response.content().trim(),
                    ChatBotTarget.BROADCAST
            );
        } catch (IOException | OpenAiException e) {
            this.enhancementsPlugin.getLogger().severe(
                    "An error occurred while trying to answer to a message using the OpenAI client: " +
                            e.getLocalizedMessage()
            );
        }

        return null;
    }

    @Override
    public boolean supports(String message, Player sender, Set<Audience> receivers) {
        return MessageAnalyzer.containsOneOfBuzzWords(message.toLowerCase(), BUZZWORDS);
    }

    @Override
    public boolean chance() {
        return ChanceUtil.isLucky(1, 4);
    }

    @Override
    public int weight() {
        return 3;
    }
}
