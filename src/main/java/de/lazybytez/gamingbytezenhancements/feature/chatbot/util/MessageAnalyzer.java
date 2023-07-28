package de.lazybytez.gamingbytezenhancements.feature.chatbot.util;

/**
 * Some utility functions to analyze messages
 */
public class MessageAnalyzer {
    /**
     * Check if a message contains all supplied buzzwords.
     */
    public static boolean containsBuzzWords(String msg, String ...buzzWords) {
        for (String buzzWord : buzzWords) {
            if (!msg.matches(".*\\b" + buzzWord + "\\b.*")) {
                return false;
            }
        }

        return true;
    }

    public static boolean containsOneOfBuzzWords(String msg, String ...buzzWords) {
        for (String buzzWord : buzzWords) {
            if (containsBuzzWords(msg, buzzWord)) {
                return true;
            }
        }

        return false;
    }
}
