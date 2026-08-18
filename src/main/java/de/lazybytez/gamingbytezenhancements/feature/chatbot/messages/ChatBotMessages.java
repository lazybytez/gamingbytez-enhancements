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
package de.lazybytez.gamingbytezenhancements.feature.chatbot.messages;

import de.lazybytez.gamingbytezenhancements.lib.message.MessagePalette;
import net.kyori.adventure.text.Component;

/**
 * The wording of every operator facing Chat Bot message.
 * <p>
 * The chat bot's own chat lines are deliberately unprefixed and unworded here, because they
 * imitate a player. This class holds only what the plugin says about the chat bot, which is
 * ordinary plugin output and follows the message conventions like any other feature.
 */
public final class ChatBotMessages {
    private ChatBotMessages() {
    }

    /**
     * Report that the static responses were reloaded.
     *
     * @param count the number of static responses now active
     * @return the confirmation wording
     */
    public static Component responsesReloaded(int count) {
        return Component.text("Reloaded ")
                .append(Component.text(count, MessagePalette.VALUE))
                .append(Component.text(" static responses."));
    }

    /**
     * Reject a static response reload whose file could not be read.
     *
     * @return the rejection wording
     */
    public static Component responsesReloadFailed() {
        return Component.text("Reloading the static responses failed, the previous ones stay active.");
    }

    /**
     * Report that the chat bot settings were reloaded with AI answers enabled.
     *
     * @return the confirmation wording
     */
    public static Component settingsReloadedAiEnabled() {
        return Component.text("Reloaded the chat bot settings, AI answers are enabled.");
    }

    /**
     * Report that the chat bot settings were reloaded with AI answers disabled.
     *
     * @return the confirmation wording
     */
    public static Component settingsReloadedAiDisabled() {
        return Component.text("Reloaded the chat bot settings, AI answers are disabled.");
    }
}
