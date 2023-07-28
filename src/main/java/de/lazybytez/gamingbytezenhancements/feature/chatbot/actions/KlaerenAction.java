package de.lazybytez.gamingbytezenhancements.feature.chatbot.actions;

import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotResponse;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotTarget;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.util.ChanceUtil;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.util.MessageAnalyzer;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * Action that has a 1 in 25 chance of responding with " Dei Mudda Lol".
 */
public class KlaerenAction implements ChatBotAction {
    private static final String RESPONSE_MESSAGE = "Abwasser ist ein Thema das unbedingt geklärt werden muss";
    private static final String[] BUZZWORDS = {
            "klären",
            "klären!",
            "klären?",
            "klären.",

            "klärung",
            "klärung!",
            "klärung?",
            "klärung.",

            "geklärt",
            "geklärt!",
            "geklärt?",
            "geklärt.",

            "abklären",
            "abklären!",
            "abklären?",
            "abklären.",

            "erklären",
            "erklären!",
            "erklären?",
            "erklären.",
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
