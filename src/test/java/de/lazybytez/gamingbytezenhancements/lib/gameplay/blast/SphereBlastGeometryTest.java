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

class SphereBlastGeometryTest {

    @Test
    void shouldYieldAnOffsetCountMatchingTheBallVolume() {
        SphereBlastGeometry geometry = new SphereBlastGeometry(32);

        long count = geometry.offsets().count();

        assertTrue(count >= 16000 && count <= 18000, "unexpected offset count " + count);
    }

    @Test
    void shouldYieldFewerOffsetsThanTheEnclosingCuboid() {
        SphereBlastGeometry sphere = new SphereBlastGeometry(8);
        CuboidBlastGeometry cuboid = new CuboidBlastGeometry(8);

        assertTrue(sphere.offsets().count() < cuboid.offsets().count());
    }

    @Test
    void shouldYieldDistinctOffsets() {
        SphereBlastGeometry geometry = new SphereBlastGeometry(16);

        assertEquals(geometry.offsets().count(), geometry.offsets().distinct().count());
    }

    @Test
    void shouldYieldOnlyOffsetsItContains() {
        SphereBlastGeometry geometry = new SphereBlastGeometry(16);

        assertTrue(geometry.offsets().allMatch(geometry::contains));
    }

    @Test
    void shouldYieldOffsetsOrderedByNonDecreasingDistance() {
        SphereBlastGeometry geometry = new SphereBlastGeometry(16);

        List<BlastVector> offsets = geometry.offsets().toList();

        for (int index = 1; index < offsets.size(); index++) {
            int previous = offsets.get(index - 1).squaredLength();
            int current = offsets.get(index).squaredLength();

            assertTrue(previous <= current, "offset " + index + " breaks the distance ordering");
        }
    }

    @Test
    void shouldStartAtTheCentre() {
        SphereBlastGeometry geometry = new SphereBlastGeometry(24);

        assertEquals(BlastVector.ORIGIN, geometry.offsets().findFirst().orElseThrow());
    }

    @Test
    void shouldContainBlocksWhoseCentreSitsOnTheRadius() {
        SphereBlastGeometry geometry = new SphereBlastGeometry(32);

        assertTrue(geometry.contains(new BlastVector(16, -16, 0)));
        assertTrue(geometry.contains(new BlastVector(0, -32, 0)));
    }

    @Test
    void shouldHangEntirelyBelowTheCharge() {
        SphereBlastGeometry geometry = new SphereBlastGeometry(32);

        assertTrue(geometry.contains(new BlastVector(0, 0, 0)));
        assertFalse(geometry.contains(new BlastVector(0, 1, 0)));
        assertTrue(geometry.offsets().allMatch(offset -> offset.y() <= 0));
    }

    @Test
    void shouldRejectBlocksWhoseCentreSitsOutsideTheRadius() {
        SphereBlastGeometry geometry = new SphereBlastGeometry(32);

        assertFalse(geometry.contains(new BlastVector(17, 0, 0)));
        assertFalse(geometry.contains(new BlastVector(12, 12, 0)));
    }
}
