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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlastBudgetTest {

    private static final int BLOCKS_PER_TICK = 500;

    @Test
    void allocate_withDemandFarAboveBudget_neverExceedsThePerTickBudget() {
        BlastBudget budget = new BlastBudget();

        int[] allocation = budget.allocate(new int[]{32768, 32768, 32768, 32768, 32768});

        assertEquals(BLOCKS_PER_TICK, BlastBudgetTest.sum(allocation));
    }

    @Test
    void allocate_withThreeBlasts_distributesAcrossAllOfThem() {
        BlastBudget budget = new BlastBudget();

        int[] allocation = budget.allocate(new int[]{10000, 10000, 10000});

        assertEquals(BLOCKS_PER_TICK, BlastBudgetTest.sum(allocation));

        for (int blastAllocation : allocation) {
            assertTrue(blastAllocation >= BLOCKS_PER_TICK / 3, "each blast gets roughly an equal share");
        }
    }

    @Test
    void allocate_withBlastNeedingLessThanItsShare_returnsTheSurplusToTheOthers() {
        BlastBudget budget = new BlastBudget();

        int[] allocation = budget.allocate(new int[]{10, 10000, 10000});

        assertEquals(10, allocation[0]);
        assertEquals(BLOCKS_PER_TICK, BlastBudgetTest.sum(allocation));
        assertEquals(BLOCKS_PER_TICK - 10, allocation[1] + allocation[2]);
    }

    @Test
    void allocate_withDemandBelowBudget_allocatesEveryRemainingBlock() {
        BlastBudget budget = new BlastBudget();

        int[] allocation = budget.allocate(new int[]{5, 300, 42});

        assertArrayEquals(new int[]{5, 300, 42}, allocation);
    }

    @Test
    void allocate_withExhaustedBlast_allocatesNothingToIt() {
        BlastBudget budget = new BlastBudget();

        int[] allocation = budget.allocate(new int[]{0, 10000});

        assertEquals(0, allocation[0]);
        assertEquals(BLOCKS_PER_TICK, allocation[1]);
    }

    @Test
    void allocate_withoutActiveBlasts_returnsAnEmptyAllocation() {
        BlastBudget budget = new BlastBudget();

        int[] allocation = budget.allocate(new int[0]);

        assertEquals(0, allocation.length);
    }

    private static int sum(int[] values) {
        int total = 0;

        for (int value : values) {
            total += value;
        }

        return total;
    }
}
