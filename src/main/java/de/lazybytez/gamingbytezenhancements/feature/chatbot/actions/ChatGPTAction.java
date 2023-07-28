package de.lazybytez.gamingbytezenhancements.feature.chatbot.actions;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotResponse;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotTarget;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.util.ChanceUtil;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.util.MessageAnalyzer;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Set;

public class ChatGPTAction implements ChatBotAction {
    private static final String[] BUZZWORDS = {
            "wer",
            "wer!",
            "wer?",
            "wer.",

            "was",
            "was!",
            "was?",
            "was.",

            "wann",
            "wann!",
            "wann?",
            "wann.",

            "wo",
            "wo!",
            "wo?",
            "wo.",

            "warum",
            "warum!",
            "warum?",
            "warum.",

            "wie",
            "wie!",
            "wie?",
            "wie.",

            "welche",
            "welche!",
            "welche?",
            "welche.",

            "welcher",
            "welcher!",
            "welcher?",
            "welcher.",

            "welches",
            "welches!",
            "welches?",
            "welches.",

            "wann",
            "wann!",
            "wann?",
            "wann.",

            "wie\\sviele",
            "wie\\sviele!",
            "wie\\sviele?",
            "wie\\sviele.",

            "kannst\\sdu",
            "kannst\\sdu!",
            "kannst\\sdu?",
            "kannst\\sdu.",

            "weißt\\sdu",
            "weißt\\sdu!",
            "weißt\\sdu?",
            "weißt\\sdu.",

            "denkst\\sdu",
            "denkst\\sdu!",
            "denkst\\sdu?",
            "denkst\\sdu.",

            "glaubst\\sdu",
            "glaubst\\sdu!",
            "glaubst\\sdu?",
            "glaubst\\sdu.",

            "ist\\ses\\smöglich",
            "ist\\ses\\smöglich!",
            "ist\\ses\\smöglich?",
            "ist\\ses\\smöglich.",

            "was\\sdenkst\\sdu\\süber",
            "was\\sdenkst\\sdu\\süber!",
            "was\\sdenkst\\sdu\\süber?",
            "Was\\sdenkst\\sdu\\süber.",
    };

    public static final long OPEN_AI_RATE_LIMIT = 15;
    private static final String PROMPT_TEMPLATE = "Der Kontext ist ein Minecraft Chat und ein Spieler schreibt die am Ende des Textes stehende Nachricht. " +
            "Was ist deine konkrete Antwort? " +
            "Schreibe deine Antwort wie wenn du es als eine einzelne Nachricht in den Chat schreiben würdest. " +
            "Beschränkte dich auf 1024 Zeichen. " +
            "Gibt die Anzahl der Zeichen am Ende nicht aus! " +
            "Die Nachricht ist: \"%s\"";

    private final EnhancementsPlugin enhancementsPlugin;

    private long lastAction = 0L;

    public ChatGPTAction(EnhancementsPlugin enhancementsPlugin) {
        this.enhancementsPlugin = enhancementsPlugin;
    }

    @Override
    public ChatBotResponse getChatBotMessage(String message, Player sender, Set<Audience> receivers) {
        if (this.enhancementsPlugin.getOpenAiClient() == null) {
            this.enhancementsPlugin.getLogger().severe(
                    "Cannot send OpenAI chat bot response as client was not initialized!"
            );
        }

        long lastActionSeconds = (System.currentTimeMillis() - lastAction) / 1000;
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
            this.lastAction = System.currentTimeMillis();
            String response = this.enhancementsPlugin.getOpenAiClient().completion(String.format(PROMPT_TEMPLATE, message));
            this.enhancementsPlugin.getLogger().info(String.format("Received answer for \"%s\" from OpenAI...", message));

            return new ChatBotResponse(false, null, response, ChatBotTarget.BROADCAST);
        } catch (IOException e) {
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
        return ChanceUtil.isLucky(1, 10);
    }

    @Override
    public int weight() {
        return 3;
    }
}
