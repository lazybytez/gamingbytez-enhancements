package de.lazybytez.gamingbytezenhancements.lib.util;

import de.lazybytez.gamingbytezenhancements.lib.util.chance.ImplementationError;
import de.lazybytez.gamingbytezenhancements.lib.util.chance.ProbabilityMap;
import java.util.Map;
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

    /**
     * Get a random key, with the value as probability of it.
     *
     * We add all probabilities together to get the sum.
     * Example:
     * {
     *     1: 30%
     *     2: 50%
     *     3: 20%
     * }
     * So the sum is 100. The random value will be between 0.0 and 99.9
     *
     * We divide it in number-blocks.
     * Example:
     * - 0.0 - 29.9 is 1
     * - 30.0 - 79.9 is 2
     * - 80.0 - 99.9 is 3
     *
     * Now we go threw the map and check if the random value is lower than the max number of the number-block.
     * If it is the case, we can return the key.
     */
    public static Integer getRandomIntegerWithProbability(ProbabilityMap<Integer> probabilityMap) {
        double randomValue = Math.random() * probabilityMap.getProbabilitySum();

        double probabilityStack = 0.0;
        for (Map.Entry<Integer, Double> entry: probabilityMap.entrySet()) {
            probabilityStack += entry.getValue();
            if (probabilityStack > randomValue) {
                return entry.getKey();
            }
        }

        throw new ImplementationError("Random value is higher than the complete ProbabilityStack of `" + probabilityStack + "`.");
    }
}
