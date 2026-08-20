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

import java.util.Objects;

/**
 * Splits the per tick block allowance across every blast that is currently in flight.
 * <p>
 * The allowance is global rather than per blast, because a per blast allowance would multiply the
 * work a tick has to do by the number of blasts in flight, and five chained level four blasts would
 * then cost five times as much as one. A global allowance keeps the cost of a tick the same no
 * matter how many blasts are running.
 * <p>
 * Blasts are served round robin, and a blast that needs less than its share leaves the rest to the
 * others, so the allowance is spent in full while no blast is starved.
 * <p>
 * The class holds no Bukkit types: the arithmetic that carries the safety property depends on
 * nothing beyond the counts it is handed.
 */
public final class BlastBudget {
    /**
     * The per tick ceiling across every blast in flight.
     * <p>
     * The pace of a blast comes from its wavefront, not from this figure: it sits above the
     * widest single shell most blasts can request, so a blast rarely feels it. Only the widest
     * shells of a level five blast lean on it briefly, which flattens exactly the spike it
     * exists to bound, and a throttled shell is recounted next tick rather than lost.
     */
    private static final int DEFAULT_BLOCKS_PER_TICK = 8000;

    private final int blocksPerTick;

    /**
     * Creates a budget carrying the standard per tick allowance.
     */
    public BlastBudget() {
        this(BlastBudget.DEFAULT_BLOCKS_PER_TICK);
    }

    /**
     * Creates a budget carrying the given per tick allowance.
     *
     * @param blocksPerTick The number of blocks that may be removed across all blasts in a tick.
     */
    public BlastBudget(int blocksPerTick) {
        if (blocksPerTick < 0) {
            throw new IllegalArgumentException("blocksPerTick must not be negative");
        }

        this.blocksPerTick = blocksPerTick;
    }

    /**
     * Splits the per tick allowance across the active blasts, round robin.
     *
     * @param remaining The number of blocks each active blast still has to remove, by blast.
     * @return The number of blocks each active blast may remove this tick, by the same index.
     */
    public int[] allocate(int[] remaining) {
        Objects.requireNonNull(remaining, "remaining must not be null");

        int[] allocation = new int[remaining.length];

        if (remaining.length == 0) {
            return allocation;
        }

        int budget = this.blocksPerTick;

        while (budget > 0) {
            int claimants = BlastBudget.countClaimants(remaining, allocation);

            if (claimants == 0) {
                return allocation;
            }

            budget -= BlastBudget.distribute(remaining, allocation, budget, Math.max(1, budget / claimants));
        }

        return allocation;
    }

    /**
     * Hands each blast that still wants blocks up to one share, capped by what it needs and by what
     * is left of the allowance.
     *
     * @param remaining  The number of blocks each blast still has to remove.
     * @param allocation The allocation built so far, mutated in place.
     * @param budget     The blocks left to hand out this tick.
     * @param share      The blocks a single blast may take in this pass.
     * @return The number of blocks handed out.
     */
    private static int distribute(int[] remaining, int[] allocation, int budget, int share) {
        int spent = 0;

        for (int index = 0; index < remaining.length && spent < budget; index++) {
            int wanted = remaining[index] - allocation[index];

            if (wanted <= 0) {
                continue;
            }

            int granted = Math.min(Math.min(share, wanted), budget - spent);
            allocation[index] += granted;
            spent += granted;
        }

        return spent;
    }

    /**
     * Counts the blasts that still want more blocks than they have been allocated.
     *
     * @param remaining  The number of blocks each blast still has to remove.
     * @param allocation The allocation built so far.
     * @return The number of blasts still wanting blocks.
     */
    private static int countClaimants(int[] remaining, int[] allocation) {
        int claimants = 0;

        for (int index = 0; index < remaining.length; index++) {
            if (remaining[index] > allocation[index]) {
                claimants++;
            }
        }

        return claimants;
    }
}
