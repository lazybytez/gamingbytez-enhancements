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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.excavationcharge;

import org.bukkit.block.BlockFace;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlaceExcavationChargeListenerTest {
    @Test
    void toCardinalFace_mapsLevelYawToTheFourCompassFaces() {
        assertEquals(BlockFace.SOUTH, PlaceExcavationChargeListener.toCardinalFace(0.0f, 0.0f));
        assertEquals(BlockFace.WEST, PlaceExcavationChargeListener.toCardinalFace(90.0f, 0.0f));
        assertEquals(BlockFace.NORTH, PlaceExcavationChargeListener.toCardinalFace(180.0f, 0.0f));
        assertEquals(BlockFace.EAST, PlaceExcavationChargeListener.toCardinalFace(270.0f, 0.0f));
    }

    @Test
    void toCardinalFace_wrapsYawOutsideOneFullTurn() {
        assertEquals(BlockFace.EAST, PlaceExcavationChargeListener.toCardinalFace(-90.0f, 0.0f));
        assertEquals(BlockFace.SOUTH, PlaceExcavationChargeListener.toCardinalFace(720.0f, 0.0f));
        assertEquals(BlockFace.NORTH, PlaceExcavationChargeListener.toCardinalFace(-180.0f, 0.0f));
    }

    @Test
    void toCardinalFace_snapsToTheNearestCompassFace() {
        assertEquals(BlockFace.SOUTH, PlaceExcavationChargeListener.toCardinalFace(44.0f, 0.0f));
        assertEquals(BlockFace.WEST, PlaceExcavationChargeListener.toCardinalFace(46.0f, 0.0f));
    }

    @Test
    void toCardinalFace_returnsUpWhenLookingSteeplyUpwards() {
        assertEquals(BlockFace.UP, PlaceExcavationChargeListener.toCardinalFace(0.0f, -90.0f));
        assertEquals(BlockFace.UP, PlaceExcavationChargeListener.toCardinalFace(137.0f, -60.0f));
    }

    @Test
    void toCardinalFace_returnsDownWhenLookingSteeplyDownwards() {
        assertEquals(BlockFace.DOWN, PlaceExcavationChargeListener.toCardinalFace(0.0f, 90.0f));
        assertEquals(BlockFace.DOWN, PlaceExcavationChargeListener.toCardinalFace(137.0f, 60.0f));
    }

    @Test
    void toCardinalFace_keepsTheCompassFaceForShallowPitch() {
        assertEquals(BlockFace.SOUTH, PlaceExcavationChargeListener.toCardinalFace(0.0f, -44.0f));
        assertEquals(BlockFace.EAST, PlaceExcavationChargeListener.toCardinalFace(270.0f, 44.0f));
    }

    @Test
    void toCardinalFace_alwaysYieldsAnAxisAlignedUnitVector() {
        for (float yaw = -360.0f; yaw <= 360.0f; yaw += 7.0f) {
            for (float pitch = -90.0f; pitch <= 90.0f; pitch += 5.0f) {
                BlockFace face = PlaceExcavationChargeListener.toCardinalFace(yaw, pitch);
                int magnitude = Math.abs(face.getModX()) + Math.abs(face.getModY()) + Math.abs(face.getModZ());

                assertEquals(1, magnitude, "yaw " + yaw + " pitch " + pitch + " produced " + face);
            }
        }
    }
}
