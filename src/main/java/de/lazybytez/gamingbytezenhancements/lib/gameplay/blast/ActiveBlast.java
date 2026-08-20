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
package de.lazybytez.gamingbytezenhancements.lib.gameplay.blast;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * A single blast that is still clearing blocks.
 * <p>
 * The blast holds the blocks a {@link BlastPlanner} planned for it, in carving order, and hands
 * them out one by one as the {@link BlastScheduler} spends its share of the per tick allowance. The
 * drops it earns on the way are collected in its own tally and dropped as consolidated stacks at
 * its detonation point once it is finished.
 */
public final class ActiveBlast {

    private final List<Block> plannedBlocks;
    private final Deque<Block> remaining;
    private final BlastDropTally dropTally;
    private final Location detonationPoint;
    private final double waveSpeed;

    private double wavefront;

    /**
     * Creates a blast over the planned blocks.
     *
     * @param plannedBlocks   The blocks the blast removes, in carving order.
     * @param dropTally       The tally collecting the drops the blast earns.
     * @param detonationPoint The location the charge detonated in, where the drops end up.
     * @param waveSpeed       How far the wavefront travels per tick, in blocks of radius.
     */
    public ActiveBlast(List<Block> plannedBlocks, BlastDropTally dropTally, Location detonationPoint, double waveSpeed) {
        Objects.requireNonNull(plannedBlocks, "plannedBlocks must not be null");

        if (waveSpeed <= 0.0) {
            throw new IllegalArgumentException("waveSpeed must be positive");
        }

        this.plannedBlocks = List.copyOf(plannedBlocks);
        this.remaining = new ArrayDeque<>(this.plannedBlocks);
        this.dropTally = Objects.requireNonNull(dropTally, "dropTally must not be null");
        this.detonationPoint = Objects.requireNonNull(detonationPoint, "detonationPoint must not be null");
        this.waveSpeed = waveSpeed;
        this.wavefront = 0.0;

        Objects.requireNonNull(detonationPoint.getWorld(), "detonationPoint must have a world");
    }

    /**
     * Moves the wavefront one tick outwards and returns how many blocks it has reached.
     * <p>
     * The plan is ordered by distance from the detonation point, so the blocks the wavefront has
     * passed are exactly the leading ones. Pacing by radius instead of by a flat block count is
     * what makes the carve read as a shock wave: the small inner shells fall in quick succession
     * and the wide outer shells take their share of blocks in one sweep instead of crawling.
     *
     * @return The number of leading blocks within the advanced wavefront.
     */
    public int advanceWavefront() {
        this.wavefront += this.waveSpeed;

        double reach = this.wavefront * this.wavefront;
        int originX = this.detonationPoint.getBlockX();
        int originY = this.detonationPoint.getBlockY();
        int originZ = this.detonationPoint.getBlockZ();

        int reached = 0;
        for (Block block : this.remaining) {
            long deltaX = block.getX() - originX;
            long deltaY = block.getY() - originY;
            long deltaZ = block.getZ() - originZ;

            if (deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ > reach) {
                break;
            }

            reached++;
        }

        return reached;
    }

    /**
     * Returns the number of blocks the blast still has to remove.
     *
     * @return The number of blocks left.
     */
    public int remainingBlocks() {
        return this.remaining.size();
    }

    /**
     * Checks whether the blast still has blocks to remove.
     *
     * @return True if blocks are left.
     */
    public boolean hasRemainingBlocks() {
        return !this.remaining.isEmpty();
    }

    /**
     * Takes the next block off the plan, in carving order.
     *
     * @return The next block, or null if the blast is finished.
     */
    public Block nextBlock() {
        return this.remaining.poll();
    }

    /**
     * Returns every block the blast was planned to remove, in carving order.
     * <p>
     * The plan outlives the carve because the volume it describes is still needed once the blast
     * has finished, and the queue handing out blocks is empty by then.
     *
     * @return The planned blocks of this blast.
     */
    public List<Block> plannedBlocks() {
        return this.plannedBlocks;
    }

    /**
     * Returns the tally collecting the drops the blast earns.
     *
     * @return The drop tally of this blast.
     */
    public BlastDropTally dropTally() {
        return this.dropTally;
    }

    /**
     * Returns the location the charge detonated in.
     *
     * @return The detonation point of this blast.
     */
    public Location detonationPoint() {
        return this.detonationPoint;
    }
}
