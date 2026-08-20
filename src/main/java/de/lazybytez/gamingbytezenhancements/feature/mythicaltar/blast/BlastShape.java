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

import java.util.Objects;

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
     * Builds the volume this shape carves at the given measurements.
     * <p>
     * Only {@link #TUNNEL} reads the direction, because it is the one shape bored along an axis
     * rather than sunk around the charge. The other three ignore it, which lets a caller hand over
     * the facing it holds without first asking which shape it is talking to.
     *
     * @param dimensions the measurements to carve to
     * @param direction  the axis-aligned unit offset a tunnel is bored along
     * @return the geometry of this shape
     */
    public BlastGeometry geometry(BlastDimensions dimensions, BlastVector direction) {
        Objects.requireNonNull(dimensions, "dimensions must not be null");

        return switch (this) {
            case CUBOID -> new CuboidBlastGeometry(dimensions.size());
            case SPHERE -> new SphereBlastGeometry(dimensions.size());
            case CYLINDER -> new CylinderBlastGeometry(dimensions.size());
            case TUNNEL -> new TunnelBlastGeometry(dimensions.size(), dimensions.crossSection(), direction);
        };
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
