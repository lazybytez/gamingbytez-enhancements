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
