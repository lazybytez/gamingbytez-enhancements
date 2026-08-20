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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bukkit.Location;
import org.junit.jupiter.api.Test;

class BlastVectorTest {

    @Test
    void minus_returnsTheOffsetFromTheOtherVectorToThisOne() {
        BlastVector target = new BlastVector(4, 2, -3);
        BlastVector origin = new BlastVector(1, 5, -1);

        assertEquals(new BlastVector(3, -3, -2), target.minus(origin));
    }

    @Test
    void minus_producesNegativeComponentsWhenTheOtherVectorIsFurtherOut() {
        BlastVector target = new BlastVector(0, 0, 0);
        BlastVector origin = new BlastVector(5, -5, 10);

        assertEquals(new BlastVector(-5, 5, -10), target.minus(origin));
    }

    @Test
    void fromBlock_readsTheBlockCoordinatesOfTheLocation() {
        Location location = this.locationAt(3, 70, -8);

        assertEquals(new BlastVector(3, 70, -8), BlastVector.fromBlock(location));
    }

    @Test
    void fromBlock_readsNegativeBlockCoordinates() {
        Location location = this.locationAt(-12, -4, -30);

        assertEquals(new BlastVector(-12, -4, -30), BlastVector.fromBlock(location));
    }

    private Location locationAt(int x, int y, int z) {
        Location location = mock(Location.class);
        when(location.getBlockX()).thenReturn(x);
        when(location.getBlockY()).thenReturn(y);
        when(location.getBlockZ()).thenReturn(z);

        return location;
    }
}
