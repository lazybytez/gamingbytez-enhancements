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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BlastPlannerTest {
    private static final int MIN_HEIGHT = -64;
    private static final int MAX_HEIGHT = 320;

    private final Map<BlastVector, Block> blocks = new HashMap<>();
    private final Map<BlastVector, Material> materials = new HashMap<>();
    private final Set<BlastVector> unloadedChunks = new HashSet<>();

    private World world;
    private BlastPlanner planner;

    @BeforeEach
    void setUp() {
        this.world = mock(World.class);
        this.planner = new BlastPlanner(new BlastBlockFilter(material -> 0.0));

        when(this.world.getMinHeight()).thenReturn(BlastPlannerTest.MIN_HEIGHT);
        when(this.world.getMaxHeight()).thenReturn(BlastPlannerTest.MAX_HEIGHT);
        when(this.world.isChunkLoaded(anyInt(), anyInt())).thenAnswer(invocation ->
                !this.unloadedChunks.contains(new BlastVector(
                        invocation.getArgument(0), 0, invocation.getArgument(1))));
        when(this.world.getBlockAt(anyInt(), anyInt(), anyInt())).thenAnswer(invocation ->
                this.blockAt(
                        invocation.getArgument(0),
                        invocation.getArgument(1),
                        invocation.getArgument(2)));
    }

    @Test
    void plansEveryOffsetInTheGeometrysOrder() {
        BlastGeometry geometry = BlastPlannerTest.geometryOf(
                new BlastVector(0, 0, 0),
                new BlastVector(1, 0, 0),
                new BlastVector(0, 0, 2));

        List<Block> planned = this.planner.plan(geometry, this.locationAt(64, 70, 64));

        assertEquals(
                List.of(this.blockAt(64, 70, 64), this.blockAt(65, 70, 64), this.blockAt(64, 70, 66)),
                planned);
    }

    @Test
    void skipsTheWorldFloorAndEverythingBelowIt() {
        BlastGeometry geometry = BlastPlannerTest.geometryOf(
                new BlastVector(0, 0, 0),
                new BlastVector(0, -1, 0));

        List<Block> planned = this.planner.plan(
                geometry, this.locationAt(64, BlastPlannerTest.MIN_HEIGHT, 64));

        assertEquals(List.of(), planned);
    }

    @Test
    void plansTheLayerDirectlyAboveTheWorldFloor() {
        BlastGeometry geometry = BlastPlannerTest.geometryOf(new BlastVector(0, 0, 0));

        List<Block> planned = this.planner.plan(
                geometry, this.locationAt(64, BlastPlannerTest.MIN_HEIGHT + 1, 64));

        assertEquals(List.of(this.blockAt(64, BlastPlannerTest.MIN_HEIGHT + 1, 64)), planned);
    }

    @Test
    void skipsCoordinatesAtOrAboveTheWorldMaxHeight() {
        BlastGeometry geometry = BlastPlannerTest.geometryOf(
                new BlastVector(0, 0, 0),
                new BlastVector(0, 1, 0));

        List<Block> planned = this.planner.plan(
                geometry, this.locationAt(64, BlastPlannerTest.MAX_HEIGHT - 1, 64));

        assertEquals(List.of(this.blockAt(64, BlastPlannerTest.MAX_HEIGHT - 1, 64)), planned);
    }

    @Test
    void skipsCoordinatesInAnUnloadedChunk() {
        this.unloadedChunks.add(new BlastVector(-1, 0, 0));

        BlastGeometry geometry = BlastPlannerTest.geometryOf(
                new BlastVector(0, 0, 0),
                new BlastVector(-1, 0, 0));

        List<Block> planned = this.planner.plan(geometry, this.locationAt(0, 70, 0));

        assertEquals(List.of(this.blockAt(0, 70, 0)), planned);
    }

    @Test
    void neverLoadsAChunkWhilePlanning() {
        this.unloadedChunks.add(new BlastVector(-1, 0, 0));

        BlastGeometry geometry = BlastPlannerTest.geometryOf(
                new BlastVector(0, 0, 0),
                new BlastVector(-1, 0, 0));

        this.planner.plan(geometry, this.locationAt(0, 70, 0));

        verify(this.world, never()).getChunkAt(anyInt(), anyInt());
        verify(this.world, never()).getChunkAt(anyInt(), anyInt(), anyBoolean());
        verify(this.world, never()).loadChunk(anyInt(), anyInt());
        verify(this.world, never()).loadChunk(anyInt(), anyInt(), anyBoolean());
        verify(this.world, never()).getBlockAt(eq(-1), anyInt(), anyInt());
    }

    @Test
    void skipsBlocksTheFilterRejects() {
        this.materials.put(new BlastVector(65, 70, 64), Material.BEDROCK);

        BlastGeometry geometry = BlastPlannerTest.geometryOf(
                new BlastVector(0, 0, 0),
                new BlastVector(1, 0, 0));

        List<Block> planned = this.planner.plan(geometry, this.locationAt(64, 70, 64));

        assertEquals(List.of(this.blockAt(64, 70, 64)), planned);
    }

    private Location locationAt(int x, int y, int z) {
        return new Location(this.world, x, y, z);
    }

    private Block blockAt(int x, int y, int z) {
        return this.blocks.computeIfAbsent(new BlastVector(x, y, z), position -> {
            Block block = mock(Block.class);
            when(block.getType()).thenReturn(this.materials.getOrDefault(position, Material.STONE));

            return block;
        });
    }

    private static BlastGeometry geometryOf(BlastVector... offsets) {
        List<BlastVector> volume = List.of(offsets);

        return new BlastGeometry() {
            @Override
            public Stream<BlastVector> offsets() {
                return volume.stream();
            }

            @Override
            public boolean contains(BlastVector offset) {
                return volume.contains(offset);
            }
        };
    }
}
