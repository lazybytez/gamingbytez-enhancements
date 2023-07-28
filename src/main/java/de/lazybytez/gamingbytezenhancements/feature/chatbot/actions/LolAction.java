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
public class LolAction implements ChatBotAction {
    private static final String RESPONSE_MESSAGE = "Dei Mudda Lol";
    @Override
    public ChatBotResponse getChatBotMessage(String message, Player sender, Set<Audience> receivers) {
        return new ChatBotResponse(false, null, RESPONSE_MESSAGE, ChatBotTarget.BROADCAST);
    }

    @Override
    public boolean supports(String message, Player sender, Set<Audience> receivers) {
        return MessageAnalyzer.containsBuzzWord(message.toLowerCase(), "lol");
    }

    @Override
    public boolean chance() {
        return ChanceUtil.isLucky(1, 15);
    }

    @Override
    public int weight() {
        return 1;
    }
}
