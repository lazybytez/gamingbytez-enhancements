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
package de.lazybytez.gamingbytezenhancements.lib.gameplay.world;

import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WorldChunksTest {
    private World world;

    @BeforeEach
    void setUp() {
        this.world = mock(World.class);
        when(this.world.isChunkLoaded(anyInt(), anyInt())).thenReturn(true);
    }

    @Test
    void shouldMapPositiveBlockCoordinatesToTheirChunk() {
        WorldChunks.isChunkLoadedAt(this.world, 32, 47);

        verify(this.world).isChunkLoaded(2, 2);
    }

    @Test
    void shouldMapNegativeBlockCoordinatesToTheirChunk() {
        WorldChunks.isChunkLoadedAt(this.world, -17, -33);

        verify(this.world).isChunkLoaded(-2, -3);
    }

    @Test
    void shouldMapBlockZeroToChunkZero() {
        WorldChunks.isChunkLoadedAt(this.world, 0, 0);

        verify(this.world).isChunkLoaded(0, 0);
    }

    @Test
    void shouldMapBlockMinusOneToChunkMinusOne() {
        WorldChunks.isChunkLoadedAt(this.world, -1, -1);

        verify(this.world).isChunkLoaded(-1, -1);
    }

    @Test
    void shouldMapTheLastBlockOfChunkMinusOneToChunkMinusOne() {
        WorldChunks.isChunkLoadedAt(this.world, -16, -16);

        verify(this.world).isChunkLoaded(-1, -1);
    }

    @Test
    void shouldReportLoadedWhenTheWorldReportsLoaded() {
        assertTrue(WorldChunks.isChunkLoadedAt(this.world, 100, -100));
    }

    @Test
    void shouldReportUnloadedWhenTheWorldReportsUnloaded() {
        when(this.world.isChunkLoaded(anyInt(), anyInt())).thenReturn(false);

        assertFalse(WorldChunks.isChunkLoadedAt(this.world, 100, -100));
    }

    @Test
    void shouldRejectANullWorld() {
        assertThrows(NullPointerException.class, () -> WorldChunks.isChunkLoadedAt(null, 0, 0));
    }
}
