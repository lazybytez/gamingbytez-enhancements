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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

/**
 * Carves every Excavation Charge blast that is in flight, spread over as many ticks as the per tick
 * allowance needs.
 * <p>
 * One repeating task serves all blasts and spends a single {@link BlastBudget} across them, so a
 * chain of blasts costs a tick exactly as much as a single blast does. The task runs only while
 * blasts are in flight and is cancelled again once the last one finishes.
 */
public final class BlastScheduler {
    private static final double DROP_CHANCE = 0.05;
    private static final long TICK_DELAY = 1L;
    private static final long TICK_PERIOD = 1L;
    private static final int CHUNK_SHIFT = 4;

    /**
     * The most blocks that may wait to be carved across every blast at once.
     * <p>
     * Each blast holds its whole plan in memory until it is carved, so without a ceiling a player
     * detonating charge after charge grows the queue without bound. A full five member cascade of
     * the largest shape sits just under this figure.
     */
    private static final int MAX_QUEUED_BLOCKS = 200_000;

    private final EnhancementsPlugin plugin;
    private final BlastBlockFilter blockFilter;
    private final BlastBudget budget;
    private final List<ActiveBlast> activeBlasts;

    private BukkitTask task;

    /**
     * Creates a scheduler carving blasts on behalf of the given plugin.
     *
     * @param plugin      The plugin owning the repeating task.
     * @param blockFilter Decides whether a block still qualifies for removal.
     * @param budget      Splits the per tick allowance across the blasts in flight.
     */
    public BlastScheduler(EnhancementsPlugin plugin, BlastBlockFilter blockFilter, BlastBudget budget) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null");
        this.blockFilter = Objects.requireNonNull(blockFilter, "blockFilter must not be null");
        this.budget = Objects.requireNonNull(budget, "budget must not be null");
        this.activeBlasts = new ArrayList<>();
    }

    /**
     * Takes a blast into the queue and starts the repeating task if it is not running yet.
     *
     * @param blast The blast to carve.
     */
    public void submit(ActiveBlast blast) {
        Objects.requireNonNull(blast, "blast must not be null");

        if (!blast.hasRemainingBlocks()) {
            this.finish(blast);

            return;
        }

        if (this.queuedBlocks() + blast.remainingBlocks() > BlastScheduler.MAX_QUEUED_BLOCKS) {
            this.plugin.getLogger().warning(
                    "Refused an Excavation Charge blast because too much carving is already queued.");

            return;
        }

        this.activeBlasts.add(blast);
        this.start();
    }

    /**
     * Whether a blast of the given size would still fit in the queue.
     * <p>
     * A detonation asks this before it announces itself, so a blast that cannot be carved is
     * abandoned before it logs an explosion, damages anyone or consumes the charge.
     *
     * @param blockCount The number of blocks the blast would carve.
     * @return true when the blast fits under the queued block ceiling.
     */
    public boolean canAccept(int blockCount) {
        return this.queuedBlocks() + blockCount <= BlastScheduler.MAX_QUEUED_BLOCKS;
    }

    private int queuedBlocks() {
        int queued = 0;

        for (ActiveBlast blast : this.activeBlasts) {
            queued += blast.remainingBlocks();
        }

        return queued;
    }

    /**
     * Carves every queued blast to its end right now and cancels the repeating task.
     * <p>
     * The work is done synchronously because a server that stops while blasts are in flight would
     * otherwise keep a half carved volume, and the plan that describes the rest of it lives in
     * memory only.
     */
    public void shutdown() {
        this.stop();

        for (ActiveBlast blast : this.activeBlasts) {
            this.spend(blast, blast.remainingBlocks());
            this.finish(blast);
        }

        this.activeBlasts.clear();
    }

    private void start() {
        if (this.task != null) {
            return;
        }

        this.task = Bukkit.getScheduler().runTaskTimer(
                this.plugin, this::tick, BlastScheduler.TICK_DELAY, BlastScheduler.TICK_PERIOD);
    }

    private void stop() {
        if (this.task == null) {
            return;
        }

        this.task.cancel();
        this.task = null;
    }

    /**
     * Carves one tick of every blast in flight.
     * <p>
     * Each blast asks for the blocks its wavefront has reached this tick, so the pacing comes
     * from the wave speed of the blast's level. The budget only caps the total against the per
     * tick ceiling: under normal load it never bites, it exists so a chain of large blasts
     * cannot pile their widest shells into one tick.
     */
    private void tick() {
        int[] allocation = this.budget.allocate(this.wavefrontRequests());

        for (int index = 0; index < allocation.length; index++) {
            this.spend(this.activeBlasts.get(index), allocation[index]);
        }

        this.removeFinishedBlasts();

        if (this.activeBlasts.isEmpty()) {
            this.stop();
        }
    }

    private int[] wavefrontRequests() {
        int[] requests = new int[this.activeBlasts.size()];

        for (int index = 0; index < requests.length; index++) {
            requests[index] = this.activeBlasts.get(index).advanceWavefront();
        }

        return requests;
    }

    /**
     * Removes up to the allowed number of blocks of a single blast.
     * <p>
     * A block that no longer qualifies still costs its share of the allowance, because the plan is
     * consumed over many ticks and a volume that has meanwhile been cleared by another blast would
     * otherwise be walked in a single tick.
     *
     * @param blast     The blast to carve.
     * @param allowance The number of blocks the blast may take this tick.
     */
    private void spend(ActiveBlast blast, int allowance) {
        for (int removed = 0; removed < allowance; removed++) {
            Block block = blast.nextBlock();

            if (block == null) {
                return;
            }

            this.carveSafely(block, blast.dropTally());
        }
    }

    private void carveSafely(Block block, BlastDropTally dropTally) {
        try {
            this.carve(block, dropTally);
        } catch (RuntimeException exception) {
            this.plugin.getLogger().log(
                    Level.WARNING,
                    String.format("Failed to carve block at %s.", block.getLocation()),
                    exception);
        }
    }

    /**
     * Removes a single planned block.
     * <p>
     * A blast is carved over many ticks, so a chunk that was loaded when the plan was built may
     * have unloaded since. Reading or writing a block there would load it back synchronously on
     * the main thread, which is the stall the planner refuses to cause in the first place.
     * <p>
     * The physics flag is false because a blast changes thousands of blocks, and letting each of
     * them update its neighbours would cost far more than the removal itself.
     *
     * @param block     The block to remove.
     * @param dropTally The tally collecting the drops of the blast.
     */
    private void carve(Block block, BlastDropTally dropTally) {
        if (!block.getWorld().isChunkLoaded(
                block.getX() >> BlastScheduler.CHUNK_SHIFT,
                block.getZ() >> BlastScheduler.CHUNK_SHIFT)) {
            return;
        }

        if (!this.blockFilter.isDestructible(block.getType())) {
            return;
        }

        BlastScheduler.dropContainerContents(block);

        if (ThreadLocalRandom.current().nextDouble() < BlastScheduler.DROP_CHANCE) {
            BlastScheduler.tallyDrops(block, dropTally);
        }

        block.setType(Material.AIR, false);
    }

    /**
     * Drops what a container block holds at its own location before it is removed.
     *
     * @param block The block about to be removed.
     */
    private static void dropContainerContents(Block block) {
        if (!(block.getState(false) instanceof Container container)) {
            return;
        }

        Location location = block.getLocation().toCenterLocation();
        World world = block.getWorld();

        for (ItemStack item : container.getInventory().getContents()) {
            if (item == null || item.getType().isAir()) {
                continue;
            }

            world.dropItemNaturally(location, item);
        }

        container.getInventory().clear();
    }

    private static void tallyDrops(Block block, BlastDropTally dropTally) {
        for (ItemStack drop : block.getDrops()) {
            dropTally.add(drop.getType(), drop.getAmount());
        }
    }

    private void removeFinishedBlasts() {
        Iterator<ActiveBlast> blasts = this.activeBlasts.iterator();

        while (blasts.hasNext()) {
            ActiveBlast blast = blasts.next();

            if (blast.hasRemainingBlocks()) {
                continue;
            }

            this.finish(blast);
            blasts.remove();
        }
    }

    /**
     * Drops the salvage of a blast that has finished carving.
     * <p>
     * The detonation point's chunk can have unloaded during the carve, and dropping there would
     * load it back synchronously. The salvage is discarded in that case, because nobody is present
     * to collect it.
     *
     * @param blast The blast that finished carving.
     */
    private void finish(ActiveBlast blast) {
        try {
            Location detonationPoint = blast.detonationPoint();
            World world = detonationPoint.getWorld();

            if (!world.isChunkLoaded(
                    detonationPoint.getBlockX() >> BlastScheduler.CHUNK_SHIFT,
                    detonationPoint.getBlockZ() >> BlastScheduler.CHUNK_SHIFT)) {
                blast.dropTally().drain();

                return;
            }

            for (ItemStack drop : blast.dropTally().drain()) {
                world.dropItemNaturally(detonationPoint, drop);
            }
        } catch (RuntimeException exception) {
            this.plugin.getLogger().log(
                    Level.WARNING,
                    "Failed to drop the salvage of a finished blast.",
                    exception);
        }
    }
}
