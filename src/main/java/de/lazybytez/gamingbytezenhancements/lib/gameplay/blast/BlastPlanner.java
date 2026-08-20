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

import de.lazybytez.gamingbytezenhancements.lib.gameplay.world.WorldChunks;

import java.util.List;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Turns a {@link BlastGeometry} and the location a charge detonates in into the blocks a blast
 * removes.
 * <p>
 * The planner reads the world and mutates nothing. It answers which blocks the blast may take and
 * in which order, and leaves the removal itself to its caller.
 */
public final class BlastPlanner {
    private final BlastBlockFilter blockFilter;

    /**
     * Creates a planner that judges materials with the given filter.
     *
     * @param blockFilter decides whether a material may be destroyed
     */
    public BlastPlanner(BlastBlockFilter blockFilter) {
        this.blockFilter = Objects.requireNonNull(blockFilter, "blockFilter must not be null");
    }

    /**
     * Returns the blocks the blast removes, nearest to the centre first.
     * <p>
     * The geometry's ordering is preserved, because the scheduler carves in list order and that
     * ordering is what makes the removal read as a shockwave expanding out of the centre.
     * <p>
     * Coordinates outside the world's height range and coordinates in a chunk that is not loaded
     * right now are dropped rather than planned. Touching an unloaded chunk would make the server
     * generate it synchronously, which stalls the tick far longer than the removal it was meant to
     * serve, so the blast stops at the edge of what is already in memory.
     *
     * @param geometry the volume the blast occupies
     * @param origin the location the charge detonates in
     * @return the blocks to remove, in carving order
     */
    public List<Block> plan(BlastGeometry geometry, Location origin) {
        Objects.requireNonNull(geometry, "geometry must not be null");
        Objects.requireNonNull(origin, "origin must not be null");

        World world = Objects.requireNonNull(origin.getWorld(), "origin must have a world");
        BlastVector centre = new BlastVector(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ());

        return geometry.offsets()
                .map(offset -> new BlastVector(
                        centre.x() + offset.x(),
                        centre.y() + offset.y(),
                        centre.z() + offset.z()))
                .filter(position -> BlastPlanner.isPlannable(world, position))
                .map(position -> world.getBlockAt(position.x(), position.y(), position.z()))
                .filter(block -> this.blockFilter.isDestructible(block.getType()))
                .toList();
    }

    /**
     * Whether a coordinate may be planned at all.
     * <p>
     * The lowest layer of a world is excluded by position rather than by material. It is bedrock in
     * an ordinary world, but a flat or custom world can floor itself with anything, and carving
     * that layer opens the world into the void.
     *
     * @param world    The world the blast is carving.
     * @param position The coordinate to judge.
     * @return true when the coordinate may be carved.
     */
    private static boolean isPlannable(World world, BlastVector position) {
        if (position.y() <= world.getMinHeight() || position.y() >= world.getMaxHeight()) {
            return false;
        }

        return WorldChunks.isChunkLoadedAt(world, position.x(), position.z());
    }
}
