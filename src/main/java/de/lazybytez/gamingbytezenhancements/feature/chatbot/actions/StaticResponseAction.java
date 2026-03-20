package de.lazybytez.gamingbytezenhancements.feature.chatbot.actions;

import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotResponse;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotTarget;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.util.MessageAnalyzer;
import de.lazybytez.gamingbytezenhancements.lib.util.ChanceUtil;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.util.Set;

public class StaticResponseAction implements ChatBotAction {
    private final String responseMessage;
    private String[] buzzWords = {};
    private final int numerator;
    private final int denominator;

    public StaticResponseAction(
            String responseMessage,
            String[] buzzWords,
            int numerator,
            int denominator
    ) {
        this.responseMessage = responseMessage;
        this.buzzWords = buzzWords;
        this.numerator = numerator;
        this.denominator = denominator;
    }

    @Override
    public ChatBotResponse getChatBotMessage(String message, Player sender, Set<Audience> receivers) {
        return new ChatBotResponse(false, null, this.responseMessage, ChatBotTarget.BROADCAST);
    }

    @Override
    public boolean supports(String message, Player sender, Set<Audience> receivers) {
        return MessageAnalyzer.containsOneOfBuzzWords(message.toLowerCase(), this.buzzWords);
    }

    @Override
    public boolean chance() {
        return ChanceUtil.isLucky(this.numerator, this.denominator);
    }

    @Override
    public int weight() {
        return 1;
    }
}
