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
public class HalloAction implements ChatBotAction {
    private static final String RESPONSE_MESSAGE = "Schön das du mich auch mal grüßt!";
    private static final String[] BUZZWORDS = {
            "hallo",
            "hallo!",
            "hallo?",
            "hallo.",

            "hi",
            "hi!",
            "hi?",
            "hi.",

            "hey",
            "hey!",
            "hey?",
            "hey.",

            "guten\\smorgen",
            "guten\\smorgen!",
            "guten\\smorgen?",
            "guten\\smorgen.",

            "guten\\stag",
            "guten\\stag!",
            "guten\\stag?",
            "guten\\stag.",

            "grüß\\sgott",
            "grüß\\sgott!",
            "grüß\\sgott?",
            "grüß\\sgott.",

            "servus",
            "servus!",
            "servus?",
            "servus.",

            "moin",
            "moin!",
            "moin?",
            "moin.",

            "bonjour",
            "bonjour!",
            "bonjour?",
            "bonjour.",

            "buongiorno",
            "buongiorno!",
            "buongiorno?",
            "buongiorno.",

            "buenos\\sdías",
            "buenos\\sdías!",
            "buenos\\sdías?",
            "buenos\\sdías.",

            "namaste",
            "namaste!",
            "namaste?",
            "namaste.",

            "salaam",
            "salaam!",
            "salaam?",
            "salaam.",

            "grüß\\sdich",
            "grüß\\sdich!",
            "grüß\\sdich?",
            "grüß\\sdich.",

            "moin",
            "moin!",
            "moin?",
            "moin.",

            "griaß\\sdi",
            "griaß\\sdi!",
            "griaß\\sdi?",
            "griaß\\sdi.",

            "aloha",
            "aloha!",
            "aloha?",
            "aloha.",

            "shalom",
            "shalom!",
            "shalom?",
            "shalom.",

            "konnichiwa",
            "konnichiwa!",
            "konnichiwa?",
            "konnichiwa.",
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
        return ChanceUtil.isLucky(1, 50);
    }

    @Override
    public int weight() {
        return 1;
    }
}
