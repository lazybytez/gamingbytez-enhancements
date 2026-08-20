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

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * A single Excavation Charge blast that is still clearing blocks.
 * <p>
 * The blast holds the blocks a {@link BlastPlanner} planned for it, in carving order, and hands
 * them out one by one as the {@link BlastScheduler} spends its share of the per tick allowance. The
 * drops it earns on the way are collected in its own tally and dropped as consolidated stacks at
 * its detonation point once it is finished.
 */
public final class ActiveBlast {

    private final Deque<Block> remaining;
    private final BlastDropTally dropTally;
    private final Location detonationPoint;

    /**
     * Creates a blast over the planned blocks.
     *
     * @param plannedBlocks   The blocks the blast removes, in carving order.
     * @param dropTally       The tally collecting the drops the blast earns.
     * @param detonationPoint The location the charge detonated in, where the drops end up.
     */
    public ActiveBlast(List<Block> plannedBlocks, BlastDropTally dropTally, Location detonationPoint) {
        Objects.requireNonNull(plannedBlocks, "plannedBlocks must not be null");

        this.remaining = new ArrayDeque<>(plannedBlocks);
        this.dropTally = Objects.requireNonNull(dropTally, "dropTally must not be null");
        this.detonationPoint = Objects.requireNonNull(detonationPoint, "detonationPoint must not be null");

        Objects.requireNonNull(detonationPoint.getWorld(), "detonationPoint must have a world");
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
