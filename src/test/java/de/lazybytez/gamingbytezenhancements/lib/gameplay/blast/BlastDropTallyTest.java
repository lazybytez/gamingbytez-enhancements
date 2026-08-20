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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlastDropTallyTest {

    private static final ToIntFunction<Material> FIXED_STACK_SIZE_64 = material -> 64;

    @Test
    void drainStacks_withCountExceedingMaxStackSize_splitsIntoConsolidatedStacks() {
        BlastDropTally tally = new BlastDropTally(FIXED_STACK_SIZE_64);
        tally.add(Material.COBBLESTONE, 130);

        List<BlastDropTally.MaterialStack> stacks = tally.drainStacks();

        assertEquals(
                List.of(
                        new BlastDropTally.MaterialStack(Material.COBBLESTONE, 64),
                        new BlastDropTally.MaterialStack(Material.COBBLESTONE, 64),
                        new BlastDropTally.MaterialStack(Material.COBBLESTONE, 2)
                ),
                stacks
        );
    }

    @Test
    void drainStacks_withTwoMaterials_returnsStacksForBothAndEmptiesTally() {
        Map<Material, Integer> maxStackSizes = Map.of(Material.COBBLESTONE, 64, Material.IRON_INGOT, 64);
        BlastDropTally tally = new BlastDropTally(maxStackSizes::get);
        tally.add(Material.COBBLESTONE, 70);
        tally.add(Material.IRON_INGOT, 10);

        List<BlastDropTally.MaterialStack> stacks = tally.drainStacks();

        assertEquals(3, stacks.size());
        assertTrue(stacks.contains(new BlastDropTally.MaterialStack(Material.COBBLESTONE, 64)));
        assertTrue(stacks.contains(new BlastDropTally.MaterialStack(Material.COBBLESTONE, 6)));
        assertTrue(stacks.contains(new BlastDropTally.MaterialStack(Material.IRON_INGOT, 10)));
        assertEquals(List.of(), tally.drainStacks());
    }

    @Test
    void drainStacks_withNonPositiveMaxStackSize_terminatesWithSingleItemStacks() {
        BlastDropTally tally = new BlastDropTally(material -> 0);
        tally.add(Material.COBBLESTONE, 3);

        List<BlastDropTally.MaterialStack> stacks = assertTimeoutPreemptively(
                Duration.ofSeconds(5), tally::drainStacks);

        assertEquals(
                List.of(
                        new BlastDropTally.MaterialStack(Material.COBBLESTONE, 1),
                        new BlastDropTally.MaterialStack(Material.COBBLESTONE, 1),
                        new BlastDropTally.MaterialStack(Material.COBBLESTONE, 1)
                ),
                stacks
        );
    }

    @Test
    void drain_withEmptyTally_returnsEmptyCollectionNotNull() {
        BlastDropTally tally = BlastDropTally.create();

        List<ItemStack> result = tally.drain();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
