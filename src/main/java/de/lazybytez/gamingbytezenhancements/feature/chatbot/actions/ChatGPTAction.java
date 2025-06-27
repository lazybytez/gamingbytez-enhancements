package de.lazybytez.gamingbytezenhancements.feature.chatbot.actions;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotResponse;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotTarget;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.util.ChanceUtil;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.util.MessageAnalyzer;
import de.lazybytez.gamingbytezenhancements.lib.openai.OpenAiException;
import de.lazybytez.gamingbytezenhancements.lib.openai.OpenAiResponse;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class ChatGPTAction implements ChatBotAction {
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

    public static final long OPEN_AI_RATE_LIMIT = 60;

    private final EnhancementsPlugin enhancementsPlugin;

    private final String promptTemplate;

    private final AtomicLong lastAction = new AtomicLong(0L);

    private final AtomicLong totalTokensUsed = new AtomicLong(0L);

    public ChatGPTAction(EnhancementsPlugin enhancementsPlugin, String promptTemplate) {
        this.enhancementsPlugin = enhancementsPlugin;
        this.promptTemplate = promptTemplate;
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
                    lastActionSeconds,
                    OPEN_AI_RATE_LIMIT
            ));

            return null;
        }

        try {
            this.enhancementsPlugin.getLogger().info(String.format("Requesting answer for \"%s\" from OpenAI...", message));
            this.lastAction.set(System.currentTimeMillis());
            OpenAiResponse response = this.enhancementsPlugin
                    .getOpenAiClient()
                    .completion(String.format(promptTemplate, message));

            if (response.getContent() == null || response.getContent().isEmpty()) {
                this.enhancementsPlugin.getLogger().info(String.format(
                        "Received answer for \"%s\" from OpenAI is empty!",
                        message
                ));

                return null;
            }

            this.enhancementsPlugin.getLogger().info(String.format(
                    "Received answer \"%s\" for input message \"%s\" from OpenAI and used %d tokens",
                    response.getContent(),
                    message,
                    response.getTotalTokens()
            ));
            this.totalTokensUsed.addAndGet(response.getTotalTokens());
            this.enhancementsPlugin.getLogger().info(String.format(
                    "The server has used a total of %d OpenAI tokens since the last restart!",
                    this.totalTokensUsed.get()
            ));

            return new ChatBotResponse(false, null, response.getContent(), ChatBotTarget.BROADCAST);
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
