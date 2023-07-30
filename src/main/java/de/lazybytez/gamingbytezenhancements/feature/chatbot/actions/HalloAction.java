package de.lazybytez.gamingbytezenhancements.feature.chatbot.actions;

import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotResponse;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotTarget;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.util.ChanceUtil;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.util.MessageAnalyzer;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.util.Set;

public class HalloAction implements ChatBotAction {
    private static final String RESPONSE_MESSAGE = "Schön das du mich auch mal grüßt!";
    private static final String[] BUZZWORDS = {
            "hallo",
            "hi",
            "hey",
            "guten\\smorgen",
            "guten\\stag",
            "grüß\\sgott",
            "servus",
            "moin",
            "bonjour",
            "buongiorno",
            "buenos\\sdías",
            "namaste",
            "salaam",
            "grüß\\sdich",
            "moin",
            "griaß\\sdi",
            "aloha",
            "shalom",
            "konnichiwa",
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
        return ChanceUtil.isLucky(1, 35);
    }

    @Override
    public int weight() {
        return 1;
    }
}
