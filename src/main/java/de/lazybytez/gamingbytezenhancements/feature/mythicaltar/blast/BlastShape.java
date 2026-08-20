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

/**
 * The shape a charge carves into the world when it detonates.
 * <p>
 * Cycling through {@link #next()} follows the declaration order below and wraps
 * from {@link #TUNNEL} back to {@link #CUBOID}.
 */
public enum BlastShape {
    CUBOID("Cuboid"),
    SPHERE("Sphere"),
    CYLINDER("Cylinder"),
    TUNNEL("Tunnel");

    private final String displayName;

    BlastShape(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the next shape in declaration order, wrapping from {@link #TUNNEL} back to
     * {@link #CUBOID}.
     *
     * @return the following {@link BlastShape} constant
     */
    public BlastShape next() {
        BlastShape[] shapes = BlastShape.values();
        int nextOrdinal = (this.ordinal() + 1) % shapes.length;

        return shapes[nextOrdinal];
    }

    /**
     * Returns the human-readable name of this shape, suitable for item lore.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Decodes a stored shape name back into a shape.
     * <p>
     * A missing or unrecognised value reads back as {@link #CUBOID}, so a charge written by an
     * earlier version or edited by hand still carries a usable shape.
     *
     * @param rawShape the raw shape name read from a persistent data container, may be null
     * @return the decoded blast shape
     */
    public static BlastShape decode(String rawShape) {
        if (rawShape == null) {
            return BlastShape.CUBOID;
        }

        try {
            return BlastShape.valueOf(rawShape);
        } catch (IllegalArgumentException e) {
            return BlastShape.CUBOID;
        }
    }
}
