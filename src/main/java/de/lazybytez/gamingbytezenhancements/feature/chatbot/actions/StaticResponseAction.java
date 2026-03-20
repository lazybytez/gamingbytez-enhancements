/*
 * Gaming Bytez Enhancements - Gameplay enhancements used on our SMP servers.
 * Copyright (C) 2026 Lazy Bytez (Pascal Zarrad, Elias Knodel) and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
