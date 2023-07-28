package de.lazybytez.gamingbytezenhancements.feature.chatbot.actions;

import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotResponse;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotTarget;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.util.ChanceUtil;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.util.MessageAnalyzer;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.util.Set;

public class ChatGPTAction implements ChatBotAction {
    private static final String RESPONSE_MESSAGE = "Dei Mudda Lol";
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

    @Override
    public ChatBotResponse getChatBotMessage(String message, Player sender, Set<Audience> receivers) {
        return new ChatBotResponse(false, null, RESPONSE_MESSAGE, ChatBotTarget.BROADCAST);
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
