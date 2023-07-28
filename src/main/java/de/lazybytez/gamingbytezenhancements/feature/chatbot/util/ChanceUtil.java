package de.lazybytez.gamingbytezenhancements.feature.chatbot.util;

import java.util.Random;

/**
 * Utility class providing some static functions to make decisions based on chances.
 */
public class ChanceUtil {
    /**
     * Determine if a player is lucky in an X in N chance.
     */
    public static boolean isLucky(int numerator, int denominator) {
        Random random = new Random();
        int result = random.nextInt(denominator) + 1;

        if (numerator == 1) {
            return result == 1;
        } else {
            int range = denominator / numerator;

            return result <= range;
        }
    }
}
