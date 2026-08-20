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
package de.lazybytez.gamingbytezenhancements.lib.gameplay.blast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BlastDamageTest {

    private static final double CENTRE_DAMAGE = 34.0;
    private static final double MAX_EXTENT = 16.0;

    @Test
    void shouldReturnCentreDamageAtZeroDistance() {
        double damage = BlastDamage.falloff(BlastDamageTest.CENTRE_DAMAGE, 0.0, BlastDamageTest.MAX_EXTENT);

        assertEquals(BlastDamageTest.CENTRE_DAMAGE, damage);
    }

    @Test
    void shouldReturnLessThanOneAtTheMaximumExtent() {
        double damage = BlastDamage.falloff(
                BlastDamageTest.CENTRE_DAMAGE, BlastDamageTest.MAX_EXTENT, BlastDamageTest.MAX_EXTENT);

        assertTrue(damage < 1.0, "damage at the edge should be near zero but was " + damage);
    }

    @Test
    void shouldReturnZeroBeyondTheMaximumExtent() {
        double damage = BlastDamage.falloff(
                BlastDamageTest.CENTRE_DAMAGE, BlastDamageTest.MAX_EXTENT + 1.0, BlastDamageTest.MAX_EXTENT);

        assertEquals(0.0, damage);
    }

    @Test
    void shouldFallOffLinearlyAtHalfTheMaximumExtent() {
        double damage = BlastDamage.falloff(
                BlastDamageTest.CENTRE_DAMAGE, BlastDamageTest.MAX_EXTENT / 2.0, BlastDamageTest.MAX_EXTENT);

        assertEquals(BlastDamageTest.CENTRE_DAMAGE / 2.0, damage);
    }

    @Test
    void shouldNeverReturnNegativeDamage() {
        double damage = BlastDamage.falloff(
                BlastDamageTest.CENTRE_DAMAGE, BlastDamageTest.MAX_EXTENT * 100.0, BlastDamageTest.MAX_EXTENT);

        assertTrue(damage >= 0.0, "damage should never be negative but was " + damage);
    }

    @Test
    void shouldReturnZeroWhenTheMaximumExtentIsZero() {
        double damage = BlastDamage.falloff(BlastDamageTest.CENTRE_DAMAGE, 0.0, 0.0);

        assertEquals(0.0, damage);
    }

    @Test
    void shouldReturnZeroWhenTheMaximumExtentIsZeroAndDistanceIsPositive() {
        double damage = BlastDamage.falloff(BlastDamageTest.CENTRE_DAMAGE, 1.0, 0.0);

        assertEquals(0.0, damage);
    }
}
