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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BlastDimensionsTest {
    @Test
    void carriesTheMeasurementsItWasBuiltWith() {
        BlastDimensions dimensions = new BlastDimensions(32, 8);

        assertEquals(32, dimensions.size());
        assertEquals(8, dimensions.crossSection());
    }

    @Test
    void rejectsANonPositiveSize() {
        assertThrows(IllegalArgumentException.class, () -> new BlastDimensions(0, 8));
        assertThrows(IllegalArgumentException.class, () -> new BlastDimensions(-1, 8));
    }

    @Test
    void rejectsANonPositiveCrossSection() {
        assertThrows(IllegalArgumentException.class, () -> new BlastDimensions(32, 0));
        assertThrows(IllegalArgumentException.class, () -> new BlastDimensions(32, -1));
    }
}
