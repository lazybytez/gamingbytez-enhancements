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

/**
 * The measurements a blast geometry is carved to, in blocks.
 * <p>
 * A geometry asks for these rather than for a level, so the shapes stay usable by any caller that
 * can state a size and leave a level table free to map its levels onto measurements however it
 * likes.
 *
 * @param size         the full extent of the affected volume in blocks
 * @param crossSection the side length of the square cross section a bore carves
 */
public record BlastDimensions(int size, int crossSection) {

    /**
     * Rejects measurements that describe no volume at all.
     *
     * @throws IllegalArgumentException when either measurement is not positive
     */
    public BlastDimensions {
        if (size <= 0) {
            throw new IllegalArgumentException("size must be positive, got " + size);
        }

        if (crossSection <= 0) {
            throw new IllegalArgumentException("crossSection must be positive, got " + crossSection);
        }
    }
}
