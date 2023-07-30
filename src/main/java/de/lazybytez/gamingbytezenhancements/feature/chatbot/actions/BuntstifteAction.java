package de.lazybytez.gamingbytezenhancements.feature.chatbot.actions;

import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotResponse;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotTarget;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.util.ChanceUtil;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.util.MessageAnalyzer;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.util.Set;

public class BuntstifteAction implements ChatBotAction {
    private static final String RESPONSE_MESSAGE = "Ich habe weder die Zeit noch die Buntstifte, um dir das jetzt zu erklären.";
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
        return ChanceUtil.isLucky(1, 7);
    }

    @Override
    public int weight() {
        return 1;
    }
}
