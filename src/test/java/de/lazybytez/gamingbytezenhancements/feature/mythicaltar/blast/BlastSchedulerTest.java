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

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Covers the carving contract of {@link BlastScheduler}: what it touches, what it refuses to touch
 * and what it leaves behind when the server stops.
 */
class BlastSchedulerTest {
    private static final int MAX_QUEUED_BLOCKS = 200_000;

    private MockedStatic<Bukkit> bukkit;
    private EnhancementsPlugin plugin;
    private Logger logger;
    private World world;
    private BukkitTask task;
    private Runnable tickTask;
    private boolean chunkLoaded;

    @BeforeEach
    void setUp() {
        this.plugin = mock(EnhancementsPlugin.class);
        this.logger = mock(Logger.class);
        this.world = mock(World.class);
        this.task = mock(BukkitTask.class);
        this.chunkLoaded = true;

        when(this.plugin.getLogger()).thenReturn(this.logger);
        when(this.world.isChunkLoaded(anyInt(), anyInt())).thenAnswer(invocation -> this.chunkLoaded);

        BukkitScheduler bukkitScheduler = mock(BukkitScheduler.class);
        when(bukkitScheduler.runTaskTimer(eq(this.plugin), any(Runnable.class), anyLong(), anyLong()))
                .thenAnswer(invocation -> {
                    this.tickTask = invocation.getArgument(1);

                    return this.task;
                });

        this.bukkit = mockStatic(Bukkit.class);
        this.bukkit.when(Bukkit::getScheduler).thenReturn(bukkitScheduler);
    }

    @AfterEach
    void tearDown() {
        this.bukkit.close();
    }

    @Test
    void tick_withDestructibleBlock_removesItWithoutPhysics() {
        Block block = this.blockOf(Material.STONE);

        this.schedulerWith(new BlastBudget()).submit(this.blastOf(block));
        this.tick();

        verify(block).setType(Material.AIR, false);
    }

    @Test
    void tick_withBlockInAnUnloadedChunk_leavesTheBlockUntouched() {
        this.chunkLoaded = false;
        Block block = mock(Block.class);
        when(block.getWorld()).thenReturn(this.world);

        this.schedulerWith(new BlastBudget()).submit(this.blastOf(block));
        this.tick();

        verify(block, never()).getType();
        verify(block, never()).setType(any(Material.class), anyBoolean());
    }

    @Test
    void tick_withBlockTheFilterRejects_stillSpendsItsShareOfTheAllowance() {
        Block rejected = this.blockOf(Material.BEDROCK);
        Block destructible = this.blockOf(Material.STONE);

        this.schedulerWith(new BlastBudget(1)).submit(this.blastOf(rejected, destructible));

        this.tick();

        verify(rejected, never()).setType(any(Material.class), anyBoolean());
        verify(destructible, never()).setType(any(Material.class), anyBoolean());

        this.tick();

        verify(destructible).setType(Material.AIR, false);
    }

    @Test
    void shutdown_withBlastInFlight_carvesItToItsEnd() {
        ActiveBlast blast = this.blastOf(this.blockOf(Material.STONE), this.blockOf(Material.STONE));
        BlastScheduler blastScheduler = this.schedulerWith(new BlastBudget(1));

        blastScheduler.submit(blast);
        blastScheduler.shutdown();

        assertEquals(0, blast.remainingBlocks());
        verify(this.task).cancel();
    }

    @Test
    void submit_pastTheQueuedBlockCeiling_refusesTheBlast() {
        Block queued = this.blockOf(Material.STONE);
        Block refused = this.blockOf(Material.STONE);
        BlastScheduler blastScheduler = this.schedulerWith(new BlastBudget(2));

        blastScheduler.submit(new ActiveBlast(
                Collections.nCopies(BlastSchedulerTest.MAX_QUEUED_BLOCKS, queued),
                this.tally(),
                this.detonationPoint()));
        blastScheduler.submit(this.blastOf(refused));

        this.tick();

        verify(this.logger).warning(anyString());
        verify(refused, never()).setType(any(Material.class), anyBoolean());
    }

    private BlastScheduler schedulerWith(BlastBudget budget) {
        return new BlastScheduler(this.plugin, new BlastBlockFilter(material -> 0.0), budget);
    }

    private ActiveBlast blastOf(Block... blocks) {
        return new ActiveBlast(List.of(blocks), this.tally(), this.detonationPoint());
    }

    private BlastDropTally tally() {
        return new BlastDropTally(material -> 64);
    }

    private Location detonationPoint() {
        return new Location(this.world, 0, 64, 0);
    }

    private Block blockOf(Material material) {
        Block block = mock(Block.class);
        when(block.getWorld()).thenReturn(this.world);
        when(block.getType()).thenReturn(material);

        return block;
    }

    private void tick() {
        this.tickTask.run();
    }
}
