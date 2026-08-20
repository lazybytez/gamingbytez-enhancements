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

import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ActiveBlastTest {

    @Test
    void wavefrontReleasesTheShellsInDistanceOrder() {
        ActiveBlast blast = new ActiveBlast(
                List.of(this.blockAt(0), this.blockAt(1), this.blockAt(2), this.blockAt(5)),
                BlastDropTally.create(),
                this.detonationPoint(),
                1.0
        );

        assertEquals(2, blast.advanceWavefront());
        assertEquals(3, blast.advanceWavefront());
        assertEquals(3, blast.advanceWavefront());
        assertEquals(3, blast.advanceWavefront());
        assertEquals(4, blast.advanceWavefront());
    }

    @Test
    void blocksNotYetCarvedStayWithinTheNextWavefront() {
        ActiveBlast blast = new ActiveBlast(
                List.of(this.blockAt(0), this.blockAt(1)),
                BlastDropTally.create(),
                this.detonationPoint(),
                2.0
        );

        assertEquals(2, blast.advanceWavefront());
        blast.nextBlock();

        assertEquals(1, blast.advanceWavefront());
    }

    @Test
    void rejectsAWaveSpeedThatCannotAdvance() {
        assertThrows(IllegalArgumentException.class, () -> new ActiveBlast(
                List.of(),
                BlastDropTally.create(),
                this.detonationPoint(),
                0.0
        ));
    }

    private Block blockAt(int x) {
        Block block = mock(Block.class);
        when(block.getX()).thenReturn(x);

        return block;
    }

    private Location detonationPoint() {
        return new Location(mock(World.class), 0, 0, 0);
    }
}
