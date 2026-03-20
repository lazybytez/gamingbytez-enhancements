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
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.util.Set;

/**
 * A simple action our chat bot can perform.
 * <p>
 * An action first and foremost provides a getChatBotMessage method that returns the message to
 * print out in chat, if the action is chosen.
 * <p>
 * The first step for a specific message is that the supports function of all actions is called.
 * Every action that supports the current chat message will be taken into consideration.
 * <p>
 * As we may have multiple actions and also maybe do not want to always have the chat bot answer (to keep the joke),
 * we have additional logic for the chat bot that specified when to answer.
 * <p>
 * First of all, there is the weight. The weight comes into play when multiple actions are present for the current
 * message. The weight defines how often an action should be in the decision pool. When there are two actions, with
 * weight 1 and 2, the pool will have a size if 3. The pool is shuffled and then a random number between 0 and
 * the highest index in the pool is generated. The action located at the generated index will be used.
 * <p>
 * For the chance, a boolean is returned. If an action, if chosen, should always happen it can return true.
 * Else, some custom logic (like a 1 in 25) chance can be implemented to decide whether to respond to a message or not.
 */
public interface ChatBotAction {
    /**
     * Get the message our chat bot should send into the chat.
     * <p>
     * The fucntion has access to the input message, the sender and the receivers to customize the message.
     * However, it MUST NOT directly send messages. It should return a
     */
    ChatBotResponse getChatBotMessage(String message, Player sender, Set<Audience> receivers);

    /**
     * Check whether the message is supported by this action.
     */
    boolean supports(String message, Player sender, Set<Audience> receivers);

    /**
     * This method computes whether this action should be used or not.
     * Most of the time this is done using some randomness like a one in 25th chance.
     *
     *
     */
    boolean chance();

    /**
     * The possibility that this action is chosen.
     * <p>
     * If chosen, the chance will be checked
     */
    int weight();
}
