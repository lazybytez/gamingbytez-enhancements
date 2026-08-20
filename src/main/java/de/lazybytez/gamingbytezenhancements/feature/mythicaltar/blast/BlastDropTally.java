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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ToIntFunction;

/**
 * This class represents a BlastDropTally.
 * A BlastDropTally accumulates the block drops of a single Excavation Charge blast, keyed by
 * {@link Material} with an integer count, so no {@link ItemStack} is constructed while the
 * blast is still clearing blocks. The tally is drained once the blast finishes, producing a
 * small number of consolidated stacks instead of one item entity per dropped block.
 */
public final class BlastDropTally {
    private final Map<Material, Integer> counts;
    private final ToIntFunction<Material> maxStackSizeLookup;

    /**
     * Constructor for the BlastDropTally.
     *
     * @param maxStackSizeLookup The lookup used to resolve the maximum stack size of a
     *                           material when the tally is drained.
     */
    public BlastDropTally(ToIntFunction<Material> maxStackSizeLookup) {
        this.counts = new EnumMap<>(Material.class);
        this.maxStackSizeLookup = Objects.requireNonNull(maxStackSizeLookup, "maxStackSizeLookup must not be null");
    }

    /**
     * Creates a BlastDropTally backed by the production {@link Material#getMaxStackSize()}
     * lookup, which requires a live server registry.
     *
     * @return A new BlastDropTally bound to the production stack size lookup.
     */
    public static BlastDropTally create() {
        return new BlastDropTally(Material::getMaxStackSize);
    }

    /**
     * Adds a number of dropped items of the given material to the tally.
     *
     * @param material The material that was dropped.
     * @param count    The number of items dropped. Non-positive counts are ignored.
     */
    public void add(Material material, int count) {
        Objects.requireNonNull(material, "material must not be null");

        if (count <= 0) {
            return;
        }

        this.counts.merge(material, count, Integer::sum);
    }

    /**
     * Drains the tally into consolidated {@link ItemStack}s, split at each material's maximum
     * stack size. Draining clears the tally.
     *
     * @return The consolidated item stacks, or an empty list if the tally is empty.
     */
    public List<ItemStack> drain() {
        List<MaterialStack> stacks = this.drainStacks();

        if (stacks.isEmpty()) {
            return List.of();
        }

        List<ItemStack> items = new ArrayList<>(stacks.size());

        for (MaterialStack stack : stacks) {
            items.add(ItemStack.of(stack.material(), stack.count()));
        }

        return items;
    }

    /**
     * Drains the tally into consolidated material and count pairs, split at each material's
     * maximum stack size, without constructing any {@link ItemStack}. Draining clears the
     * tally. This is the testable seam behind {@link #drain()}: {@link ItemStack} construction
     * needs a live server registry and cannot run in a unit test.
     *
     * @return The consolidated material stacks, or an empty list if the tally is empty.
     */
    List<MaterialStack> drainStacks() {
        if (this.counts.isEmpty()) {
            return List.of();
        }

        List<MaterialStack> stacks = new ArrayList<>();

        for (Map.Entry<Material, Integer> entry : this.counts.entrySet()) {
            int maxStackSize = this.maxStackSizeLookup.applyAsInt(entry.getKey());

            for (int stackSize : BlastDropTally.splitIntoStackSizes(entry.getValue(), maxStackSize)) {
                stacks.add(new MaterialStack(entry.getKey(), stackSize));
            }
        }

        this.counts.clear();

        return stacks;
    }

    /**
     * Splits a total count into consolidated stack sizes, each capped at the given maximum
     * stack size.
     * <p>
     * The step is floored at one, because a lookup reporting a non-positive maximum stack size
     * would otherwise make this loop take nothing off the total and never terminate.
     *
     * @param total        The total number of items to split.
     * @param maxStackSize The maximum size of a single stack.
     * @return The stack sizes, largest first, summing to the total.
     */
    private static List<Integer> splitIntoStackSizes(int total, int maxStackSize) {
        List<Integer> sizes = new ArrayList<>();
        int remaining = total;

        while (remaining > 0) {
            int size = Math.min(remaining, Math.max(1, maxStackSize));
            sizes.add(size);
            remaining -= size;
        }

        return sizes;
    }

    /**
     * A plain material and count pair produced while draining a {@link BlastDropTally}, before
     * it is turned into an {@link ItemStack}.
     *
     * @param material The material of the stack.
     * @param count    The number of items in the stack.
     */
    record MaterialStack(Material material, int count) {
    }
}
