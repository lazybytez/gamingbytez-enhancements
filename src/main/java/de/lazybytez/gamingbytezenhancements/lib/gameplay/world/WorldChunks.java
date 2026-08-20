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

import java.util.Objects;

/**
 * Chunk lookups by block coordinate.
 * <p>
 * The conversion from a block coordinate to a chunk coordinate is an arithmetic right shift, which
 * floors towards negative infinity, so block -1 lands in chunk -1. Integer division truncates
 * towards zero and would place that same block in chunk 0, which reads as a harmless
 * simplification and silently breaks every lookup in negative coordinate space. Owning the
 * conversion here keeps the shift out of callers, where the difference is invisible.
 */
public final class WorldChunks {
    private WorldChunks() {
    }

    /**
     * Checks whether the chunk containing the given block coordinate is loaded.
     * <p>
     * The y coordinate is irrelevant: a chunk spans the full world height, so a column is loaded
     * or unloaded as a whole.
     *
     * @param world  The world to check in.
     * @param blockX The x block coordinate the chunk is resolved from.
     * @param blockZ The z block coordinate the chunk is resolved from.
     * @return true when the containing chunk is loaded.
     */
    public static boolean isChunkLoadedAt(World world, int blockX, int blockZ) {
        Objects.requireNonNull(world, "world must not be null");

        return world.isChunkLoaded(blockX >> 4, blockZ >> 4);
    }
}
