package de.lazybytez.gamingbytezenhancements.feature.chatbot.actions;

import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotResponse;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotTarget;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.util.ChanceUtil;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.util.MessageAnalyzer;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.util.Set;

public class MeinungAction implements ChatBotAction {
    private static final String RESPONSE_MESSAGE = "Das ist aber ganz schön viel Meinung für so wenig Ahnung";
    private static final String[] BUZZWORDS = {
            "meinung",
            "meinung!",
            "meinung?",
            "meinung.",

            "finde",
            "finde!",
            "finde?",
            "finde.",

            "find",
            "find!",
            "find?",
            "find.",
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
        return ChanceUtil.isLucky(1, 5);
    }

    @Override
    public int weight() {
        return 1;
    }
}
