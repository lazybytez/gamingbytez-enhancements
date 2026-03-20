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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageAnalyzerTest {
    @Test
    void containsBuzzWords_withAllBuzzwords_returnsTrue() {
        String message = "Hey bot, can you help me?";

        boolean result = MessageAnalyzer.containsBuzzWords(message, "bot", "help");

        assertTrue(result);
    }

    @Test
    void containsBuzzWords_withMissingBuzzword_returnsFalse() {
        String message = "Hey bot, please assist me";

        boolean result = MessageAnalyzer.containsBuzzWords(message, "bot", "help");

        assertFalse(result);
    }

    @Test
    void containsBuzzWords_withSingleBuzzword_returnsTrue() {
        String message = "Hello there";

        boolean result = MessageAnalyzer.containsBuzzWords(message, "Hello");

        assertTrue(result);
    }

    @Test
    void containsBuzzWords_withPartialMatch_returnsFalse() {
        String message = "helping others is good";

        boolean result = MessageAnalyzer.containsBuzzWords(message, "help");

        assertFalse(result);
    }

    @Test
    void containsBuzzWords_withWordBoundaries_respectsBoundaries() {
        String message = "I need help now";

        boolean result = MessageAnalyzer.containsBuzzWords(message, "help");

        assertTrue(result);
    }

    @Test
    void containsBuzzWords_withCaseSensitive_respectsCase() {
        String message = "Help me please";

        boolean result = MessageAnalyzer.containsBuzzWords(message, "help");

        assertFalse(result);
    }

    @Test
    void containsBuzzWords_withEmptyMessage_returnsFalse() {
        String message = "";

        boolean result = MessageAnalyzer.containsBuzzWords(message, "test");

        assertFalse(result);
    }

    @Test
    void containsOneOfBuzzWords_withOneMatchingBuzzword_returnsTrue() {
        String message = "Can you help me?";

        boolean result = MessageAnalyzer.containsOneOfBuzzWords(message, "assist", "help", "support");

        assertTrue(result);
    }

    @Test
    void containsOneOfBuzzWords_withMultipleMatchingBuzzwords_returnsTrue() {
        String message = "Please help and support me";

        boolean result = MessageAnalyzer.containsOneOfBuzzWords(message, "help", "support");

        assertTrue(result);
    }

    @Test
    void containsOneOfBuzzWords_withNoMatchingBuzzwords_returnsFalse() {
        String message = "Just chatting here";

        boolean result = MessageAnalyzer.containsOneOfBuzzWords(message, "help", "support", "assist");

        assertFalse(result);
    }

    @Test
    void containsOneOfBuzzWords_withEmptyMessage_returnsFalse() {
        String message = "";

        boolean result = MessageAnalyzer.containsOneOfBuzzWords(message, "help");

        assertFalse(result);
    }
}
