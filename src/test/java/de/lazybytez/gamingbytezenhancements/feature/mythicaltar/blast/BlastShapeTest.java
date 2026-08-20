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

class BlastShapeTest {
    @Test
    void next_cyclesThroughAllShapesInDeclarationOrder() {
        assertEquals(BlastShape.SPHERE, BlastShape.CUBOID.next());
        assertEquals(BlastShape.CYLINDER, BlastShape.SPHERE.next());
        assertEquals(BlastShape.TUNNEL, BlastShape.CYLINDER.next());
    }

    @Test
    void next_wrapsFromTunnelBackToCuboid() {
        assertEquals(BlastShape.CUBOID, BlastShape.TUNNEL.next());
    }

    @Test
    void decode_readsBackEveryShapeWrittenByPlacement() {
        for (BlastShape shape : BlastShape.values()) {
            assertEquals(shape, BlastShape.decode(shape.name()));
        }
    }

    @Test
    void decode_fallsBackToCuboidWhenNoShapeIsStored() {
        assertEquals(BlastShape.CUBOID, BlastShape.decode(null));
    }

    @Test
    void decode_fallsBackToCuboidForAnUnknownShapeName() {
        assertEquals(BlastShape.CUBOID, BlastShape.decode("PYRAMID"));
        assertEquals(BlastShape.CUBOID, BlastShape.decode("sphere"));
    }

    @Test
    void getDisplayName_returnsLoreSuitableNames() {
        assertEquals("Cuboid", BlastShape.CUBOID.getDisplayName());
        assertEquals("Sphere", BlastShape.SPHERE.getDisplayName());
        assertEquals("Cylinder", BlastShape.CYLINDER.getDisplayName());
        assertEquals("Tunnel", BlastShape.TUNNEL.getDisplayName());
    }
}
