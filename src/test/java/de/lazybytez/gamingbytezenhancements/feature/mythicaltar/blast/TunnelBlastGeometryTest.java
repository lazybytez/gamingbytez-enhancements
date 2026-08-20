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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class TunnelBlastGeometryTest {

    private static final BlastVector EAST = new BlastVector(1, 0, 0);
    private static final BlastVector NORTH = new BlastVector(0, 0, -1);
    private static final BlastVector UP = new BlastVector(0, 1, 0);

    private static TunnelBlastGeometry tunnelAt(BlastLevel level, BlastVector direction) {
        BlastDimensions dimensions = level.getDimensions();

        return new TunnelBlastGeometry(dimensions.size(), dimensions.crossSection(), direction);
    }

    @Test
    void shouldYieldOneBlockPerCrossSectionCellAlongTheLength() {
        TunnelBlastGeometry geometry = TunnelBlastGeometryTest.tunnelAt(BlastLevel.LEVEL_4, TunnelBlastGeometryTest.EAST);

        assertEquals(2048L, geometry.offsets().count());
    }

    @Test
    void shouldUseTheWalkableCrossSectionAtLevelOne() {
        TunnelBlastGeometry geometry = TunnelBlastGeometryTest.tunnelAt(BlastLevel.LEVEL_1, TunnelBlastGeometryTest.EAST);

        assertEquals(72L, geometry.offsets().count());
    }

    @Test
    void shouldSpendLevelFiveGrowthOnLengthAlone() {
        TunnelBlastGeometry geometry = TunnelBlastGeometryTest.tunnelAt(BlastLevel.LEVEL_5, TunnelBlastGeometryTest.EAST);

        assertEquals(4096L, geometry.offsets().count());
    }

    @Test
    void shouldYieldDifferentOffsetsForDifferentDirections() {
        Set<BlastVector> east = TunnelBlastGeometryTest.tunnelAt(BlastLevel.LEVEL_2, TunnelBlastGeometryTest.EAST)
                .offsets()
                .collect(Collectors.toSet());
        Set<BlastVector> north = TunnelBlastGeometryTest.tunnelAt(BlastLevel.LEVEL_2, TunnelBlastGeometryTest.NORTH)
                .offsets()
                .collect(Collectors.toSet());

        assertNotEquals(east, north);
    }

    @Test
    void shouldYieldDistinctOffsets() {
        TunnelBlastGeometry geometry = TunnelBlastGeometryTest.tunnelAt(BlastLevel.LEVEL_2, TunnelBlastGeometryTest.NORTH);

        assertEquals(geometry.offsets().count(), geometry.offsets().distinct().count());
    }

    @Test
    void shouldYieldOnlyOffsetsItContains() {
        TunnelBlastGeometry geometry = TunnelBlastGeometryTest.tunnelAt(BlastLevel.LEVEL_2, TunnelBlastGeometryTest.NORTH);

        assertTrue(geometry.offsets().allMatch(geometry::contains));
    }

    @Test
    void shouldYieldOffsetsOrderedByNonDecreasingDistance() {
        TunnelBlastGeometry geometry = TunnelBlastGeometryTest.tunnelAt(BlastLevel.LEVEL_3, TunnelBlastGeometryTest.EAST);

        List<BlastVector> offsets = geometry.offsets().toList();

        for (int index = 1; index < offsets.size(); index++) {
            int previous = offsets.get(index - 1).squaredLength();
            int current = offsets.get(index).squaredLength();

            assertTrue(previous <= current, "offset " + index + " breaks the distance ordering");
        }
    }

    @Test
    void shouldStartAtTheCentre() {
        TunnelBlastGeometry geometry = TunnelBlastGeometryTest.tunnelAt(BlastLevel.LEVEL_3, TunnelBlastGeometryTest.EAST);

        assertEquals(BlastVector.ORIGIN, geometry.offsets().findFirst().orElseThrow());
    }

    @Test
    void shouldNotReachBehindTheCentre() {
        TunnelBlastGeometry geometry = TunnelBlastGeometryTest.tunnelAt(BlastLevel.LEVEL_2, TunnelBlastGeometryTest.EAST);

        assertFalse(geometry.contains(new BlastVector(-1, 0, 0)));
        assertTrue(geometry.contains(new BlastVector(15, 0, 0)));
        assertFalse(geometry.contains(new BlastVector(16, 0, 0)));
    }

    @Test
    void shouldGrowTheCrossSectionWithTheLevel() {
        TunnelBlastGeometry geometry = TunnelBlastGeometryTest.tunnelAt(BlastLevel.LEVEL_4, TunnelBlastGeometryTest.EAST);

        assertTrue(geometry.contains(new BlastVector(0, 7, 3)));
        assertTrue(geometry.contains(new BlastVector(0, 0, -4)));
        assertFalse(geometry.contains(new BlastVector(0, 8, 0)));
        assertFalse(geometry.contains(new BlastVector(0, 0, 4)));
    }

    @Test
    void shouldKeepTheFloorOnTheChargeLayerForAHorizontalBore() {
        TunnelBlastGeometry geometry = TunnelBlastGeometryTest.tunnelAt(BlastLevel.LEVEL_1, TunnelBlastGeometryTest.EAST);

        assertTrue(geometry.contains(new BlastVector(0, 0, 0)));
        assertTrue(geometry.contains(new BlastVector(0, 2, 0)));
        assertFalse(geometry.contains(new BlastVector(0, -1, 0)));
        assertFalse(geometry.contains(new BlastVector(0, 3, 0)));
    }

    @Test
    void shouldBoreVerticallyWhenTheDirectionPointsUp() {
        TunnelBlastGeometry geometry = TunnelBlastGeometryTest.tunnelAt(BlastLevel.LEVEL_1, TunnelBlastGeometryTest.UP);

        assertTrue(geometry.contains(new BlastVector(0, 7, 0)));
        assertFalse(geometry.contains(new BlastVector(0, 8, 0)));
        assertFalse(geometry.contains(new BlastVector(2, 0, 0)));
    }

    @Test
    void shouldRejectADirectionThatIsNotAnAxisAlignedUnitVector() {
        assertThrows(IllegalArgumentException.class,
                () -> TunnelBlastGeometryTest.tunnelAt(BlastLevel.LEVEL_1, new BlastVector(1, 1, 0)));
        assertThrows(IllegalArgumentException.class,
                () -> TunnelBlastGeometryTest.tunnelAt(BlastLevel.LEVEL_1, BlastVector.ORIGIN));
        assertThrows(IllegalArgumentException.class,
                () -> TunnelBlastGeometryTest.tunnelAt(BlastLevel.LEVEL_1, new BlastVector(2, 0, 0)));
    }
}
