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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class CuboidBlastGeometryTest {

    @Test
    void shouldYieldSizeCubedOffsetsAtTheSmallestSize() {
        CuboidBlastGeometry geometry = new CuboidBlastGeometry(8);

        assertEquals(512, geometry.offsets().count());
    }

    @Test
    void shouldYieldSizeCubedOffsetsAtTheLargestSize() {
        CuboidBlastGeometry geometry = new CuboidBlastGeometry(32);

        assertEquals(32768, geometry.offsets().count());
    }

    @Test
    void shouldYieldDistinctOffsets() {
        CuboidBlastGeometry geometry = new CuboidBlastGeometry(16);

        assertEquals(4096, geometry.offsets().distinct().count());
    }

    @Test
    void shouldYieldOnlyOffsetsItContains() {
        CuboidBlastGeometry geometry = new CuboidBlastGeometry(16);

        assertTrue(geometry.offsets().allMatch(geometry::contains));
    }

    @Test
    void shouldYieldOffsetsOrderedByNonDecreasingDistance() {
        CuboidBlastGeometry geometry = new CuboidBlastGeometry(16);

        List<BlastVector> offsets = geometry.offsets().toList();

        for (int index = 1; index < offsets.size(); index++) {
            int previous = offsets.get(index - 1).squaredLength();
            int current = offsets.get(index).squaredLength();

            assertTrue(previous <= current, "offset " + index + " breaks the distance ordering");
        }
    }

    @Test
    void shouldStartAtTheCentre() {
        CuboidBlastGeometry geometry = new CuboidBlastGeometry(24);

        assertEquals(BlastVector.ORIGIN, geometry.offsets().findFirst().orElseThrow());
    }

    @Test
    void shouldContainTheBoxCorners() {
        CuboidBlastGeometry geometry = new CuboidBlastGeometry(8);

        assertTrue(geometry.contains(new BlastVector(-4, -7, -4)));
        assertTrue(geometry.contains(new BlastVector(3, 0, 3)));
    }

    @Test
    void shouldHangEntirelyBelowTheCharge() {
        CuboidBlastGeometry geometry = new CuboidBlastGeometry(8);

        assertTrue(geometry.contains(new BlastVector(0, 0, 0)));
        assertFalse(geometry.contains(new BlastVector(0, 1, 0)));
        assertTrue(geometry.offsets().allMatch(offset -> offset.y() <= 0));
    }

    @Test
    void shouldRejectOffsetsOutsideTheBox() {
        CuboidBlastGeometry geometry = new CuboidBlastGeometry(8);

        assertFalse(geometry.contains(new BlastVector(4, 0, 0)));
        assertFalse(geometry.contains(new BlastVector(0, -8, 0)));
        assertFalse(geometry.contains(new BlastVector(0, 0, 4)));
    }
}
