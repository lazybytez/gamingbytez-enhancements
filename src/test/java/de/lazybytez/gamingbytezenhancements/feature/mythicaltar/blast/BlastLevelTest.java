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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlastLevelTest {
    @Test
    void of_returnsSizeEightForLevelOne() {
        assertEquals(8, BlastLevel.of(1).getSize());
    }

    @Test
    void of_returnsSizeSixteenForLevelTwo() {
        assertEquals(16, BlastLevel.of(2).getSize());
    }

    @Test
    void of_returnsSizeTwentyFourForLevelThree() {
        assertEquals(24, BlastLevel.of(3).getSize());
    }

    @Test
    void of_returnsSizeThirtyTwoForLevelFour() {
        assertEquals(32, BlastLevel.of(4).getSize());
    }

    @Test
    void of_returnsCentreDamageForEachLevel() {
        assertEquals(10.0, BlastLevel.of(1).getCentreDamage());
        assertEquals(18.0, BlastLevel.of(2).getCentreDamage());
        assertEquals(26.0, BlastLevel.of(3).getCentreDamage());
        assertEquals(34.0, BlastLevel.of(4).getCentreDamage());
    }

    @Test
    void of_returnsChainReachForEachLevel() {
        assertEquals(6, BlastLevel.of(1).getChainReach());
        assertEquals(8, BlastLevel.of(2).getChainReach());
        assertEquals(10, BlastLevel.of(3).getChainReach());
        assertEquals(12, BlastLevel.of(4).getChainReach());
    }

    @Test
    void of_clampsBelowMinimumToLevelOne() {
        assertEquals(BlastLevel.of(BlastLevel.MIN_LEVEL), BlastLevel.of(0));
    }

    @Test
    void of_clampsAboveMaximumToLevelFour() {
        assertEquals(BlastLevel.of(BlastLevel.MAX_LEVEL), BlastLevel.of(9));
    }

    @Test
    void maxLevel_isExposedAsConstant() {
        assertEquals(4, BlastLevel.MAX_LEVEL);
    }

    @Test
    void minLevel_isExposedAsConstant() {
        assertEquals(1, BlastLevel.MIN_LEVEL);
    }
}
