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

import java.util.stream.Stream;

/**
 * The volume a {@link BlastShape} occupies, expressed as whole-block offsets from the block a
 * charge detonates in.
 * <p>
 * A geometry knows nothing about the world. It answers which offsets belong to the volume and in
 * which order they are carved, and leaves every question about blocks, entities and protection to
 * its caller.
 * <p>
 * Two guarantees are part of the contract and callers may rely on both:
 * <ul>
 *   <li>{@link #offsets()} is ordered by non-decreasing distance from the centre. The blast
 *       carves in that order, which is what makes the removal read as a shockwave expanding out
 *       of the centre rather than a volume disappearing at once.</li>
 *   <li>Every offset {@link #offsets()} yields satisfies {@link #contains(BlastVector)}, and no
 *       offset appears twice.</li>
 * </ul>
 */
public interface BlastGeometry {

    /**
     * Returns the offsets of every block in the volume, relative to the blast centre, ordered by
     * non-decreasing distance from that centre.
     * <p>
     * Each call returns a fresh stream. Consumers that only need the innermost part of the volume
     * may short-circuit with {@code limit} or {@code takeWhile} instead of draining it.
     *
     * @return a stream of the volume's offsets, nearest to the centre first
     */
    Stream<BlastVector> offsets();

    /**
     * Returns whether the given offset lies inside the volume.
     * <p>
     * The block is judged by its centre, so a shape with a curved boundary stays round instead of
     * growing a stepped shell of half-covered blocks.
     *
     * @param offset the offset to test, relative to the blast centre
     * @return {@code true} when the offset belongs to the volume
     */
    boolean contains(BlastVector offset);
}
