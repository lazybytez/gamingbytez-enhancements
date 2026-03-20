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
package de.lazybytez.gamingbytezenhancements.feature.chatbot.util;

/**
 * Some utility functions to analyze messages
 */
public class MessageAnalyzer {
    /**
     * Check if a message contains all supplied buzzwords.
     */
    public static boolean containsBuzzWords(String msg, String... buzzWords) {
        for (String buzzWord : buzzWords) {
            if (!msg.matches(".*\\b" + buzzWord + "\\b.*")) {
                return false;
            }
        }

        return true;
    }

    public static boolean containsOneOfBuzzWords(String msg, String... buzzWords) {
        for (String buzzWord : buzzWords) {
            if (containsBuzzWords(msg, buzzWord)) {
                return true;
            }
        }

        return false;
    }
}
