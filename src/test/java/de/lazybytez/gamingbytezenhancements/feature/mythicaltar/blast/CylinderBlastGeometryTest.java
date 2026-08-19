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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class CylinderBlastGeometryTest {

    @Test
    void shouldYieldAnOffsetCountMatchingTheCylinderVolume() {
        CylinderBlastGeometry geometry = new CylinderBlastGeometry(BlastLevel.LEVEL_4);

        long count = geometry.offsets().count();

        assertTrue(count >= 24000 && count <= 27000, "unexpected offset count " + count);
    }

    @Test
    void shouldYieldMoreOffsetsThanTheInscribedSphere() {
        CylinderBlastGeometry cylinder = new CylinderBlastGeometry(BlastLevel.LEVEL_2);
        SphereBlastGeometry sphere = new SphereBlastGeometry(BlastLevel.LEVEL_2);

        assertTrue(cylinder.offsets().count() > sphere.offsets().count());
    }

    @Test
    void shouldYieldDistinctOffsets() {
        CylinderBlastGeometry geometry = new CylinderBlastGeometry(BlastLevel.LEVEL_2);

        assertEquals(geometry.offsets().count(), geometry.offsets().distinct().count());
    }

    @Test
    void shouldYieldOnlyOffsetsItContains() {
        CylinderBlastGeometry geometry = new CylinderBlastGeometry(BlastLevel.LEVEL_2);

        assertTrue(geometry.offsets().allMatch(geometry::contains));
    }

    @Test
    void shouldYieldOffsetsOrderedByNonDecreasingDistance() {
        CylinderBlastGeometry geometry = new CylinderBlastGeometry(BlastLevel.LEVEL_2);

        List<BlastVector> offsets = geometry.offsets().toList();

        for (int index = 1; index < offsets.size(); index++) {
            int previous = offsets.get(index - 1).squaredLength();
            int current = offsets.get(index).squaredLength();

            assertTrue(previous <= current, "offset " + index + " breaks the distance ordering");
        }
    }

    @Test
    void shouldStartAtTheCentre() {
        CylinderBlastGeometry geometry = new CylinderBlastGeometry(BlastLevel.LEVEL_3);

        assertEquals(BlastVector.ORIGIN, geometry.offsets().findFirst().orElseThrow());
    }

    @Test
    void shouldContainBlocksWhoseCentreSitsOnTheHorizontalRadius() {
        CylinderBlastGeometry geometry = new CylinderBlastGeometry(BlastLevel.LEVEL_4);

        assertTrue(geometry.contains(new BlastVector(16, 0, 0)));
        assertTrue(geometry.contains(new BlastVector(0, 0, -16)));
    }

    @Test
    void shouldRejectBlocksOutsideTheHorizontalRadius() {
        CylinderBlastGeometry geometry = new CylinderBlastGeometry(BlastLevel.LEVEL_4);

        assertFalse(geometry.contains(new BlastVector(17, 0, 0)));
        assertFalse(geometry.contains(new BlastVector(12, 0, 12)));
    }

    @Test
    void shouldKeepTheFullHorizontalRadiusAtEveryHeight() {
        CylinderBlastGeometry geometry = new CylinderBlastGeometry(BlastLevel.LEVEL_4);

        assertTrue(geometry.contains(new BlastVector(16, 0, 0)));
        assertTrue(geometry.contains(new BlastVector(16, -31, 0)));
    }

    @Test
    void shouldSinkTheShaftBelowTheCharge() {
        CylinderBlastGeometry geometry = new CylinderBlastGeometry(BlastLevel.LEVEL_4);

        assertTrue(geometry.contains(new BlastVector(0, 0, 0)));
        assertTrue(geometry.offsets().allMatch(offset -> offset.y() <= 0));
    }

    @Test
    void shouldRejectBlocksBeyondTheHeight() {
        CylinderBlastGeometry geometry = new CylinderBlastGeometry(BlastLevel.LEVEL_4);

        assertFalse(geometry.contains(new BlastVector(0, 1, 0)));
        assertFalse(geometry.contains(new BlastVector(0, -32, 0)));
    }
}
