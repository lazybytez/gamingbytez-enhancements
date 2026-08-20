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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * A corridor of {@link BlastLevel#getSize()} blocks bored away from the detonating block along a
 * single axis.
 * <p>
 * The length follows the blast level and the square cross section grows with it, from a walkable
 * three by three corridor up to an eight by eight gallery wide enough for two rail lines and their
 * decoration. On a horizontal bore the floor sits on the detonating block's layer and the corridor
 * extends upwards from it, so a player never falls into their own tunnel.
 * <p>
 * The corridor starts at the detonating block and reaches forwards only, so nothing behind the
 * charge is carved.
 * <p>
 * The direction is an axis-aligned unit offset rather than a dedicated type, because the blast
 * coordinate space already speaks {@link BlastVector} and a caller holding a Minecraft facing maps
 * it onto one with its modifier values. {@code (1, 0, 0)} bores east, {@code (0, 0, -1)} north,
 * {@code (0, 1, 0)} straight up.
 */
public final class TunnelBlastGeometry implements BlastGeometry {

    private final BlastVector direction;
    private final int length;
    private final BlastVector lowerBound;
    private final BlastVector upperBound;

    /**
     * Creates the tunnel geometry for the given blast level and travel direction.
     *
     * @param level     the blast level supplying the corridor length and cross section
     * @param direction the axis-aligned unit offset the corridor is bored along
     * @throws IllegalArgumentException when the direction is not an axis-aligned unit offset
     */
    public TunnelBlastGeometry(BlastLevel level, BlastVector direction) {
        Objects.requireNonNull(level, "level must not be null");
        Objects.requireNonNull(direction, "direction must not be null");
        TunnelBlastGeometry.requireAxisAlignedUnit(direction);

        this.direction = direction;
        this.length = level.getSize();

        int breadth = TunnelBlastGeometry.crossSection(level);
        this.lowerBound = new BlastVector(
                this.axisLowerBound(direction.x(), false, breadth),
                this.axisLowerBound(direction.y(), true, breadth),
                this.axisLowerBound(direction.z(), false, breadth));
        this.upperBound = new BlastVector(
                this.axisUpperBound(direction.x(), false, breadth),
                this.axisUpperBound(direction.y(), true, breadth),
                this.axisUpperBound(direction.z(), false, breadth));
    }

    @Override
    public Stream<BlastVector> offsets() {
        List<BlastVector> volume = new ArrayList<>();

        for (int x = this.lowerBound.x(); x <= this.upperBound.x(); x++) {
            for (int y = this.lowerBound.y(); y <= this.upperBound.y(); y++) {
                for (int z = this.lowerBound.z(); z <= this.upperBound.z(); z++) {
                    this.collectWhenInside(volume, new BlastVector(x, y, z));
                }
            }
        }

        volume.sort(Comparator.comparingInt(BlastVector::squaredLength));

        return volume.stream();
    }

    @Override
    public boolean contains(BlastVector offset) {
        Objects.requireNonNull(offset, "offset must not be null");

        return this.withinLength(offset) && this.withinCrossSection(offset);
    }

    /**
     * Returns the side length of the square cross section for the given blast level.
     * <p>
     * The top two levels share the eight block gallery deliberately: past eight blocks a corridor
     * stops reading as a tunnel, so level five spends its whole growth on length.
     *
     * @param level the blast level to size the cross section for
     * @return the cross section side length in blocks
     */
    private static int crossSection(BlastLevel level) {
        return switch (level) {
            case LEVEL_1 -> 3;
            case LEVEL_2 -> 4;
            case LEVEL_3 -> 6;
            case LEVEL_4, LEVEL_5 -> 8;
        };
    }

    private static void requireAxisAlignedUnit(BlastVector direction) {
        int magnitude = Math.abs(direction.x()) + Math.abs(direction.y()) + Math.abs(direction.z());

        if (magnitude == 1) {
            return;
        }

        throw new IllegalArgumentException("direction must be an axis-aligned unit offset, got " + direction);
    }

    private int axisLowerBound(int directionComponent, boolean vertical, int breadth) {
        return switch (directionComponent) {
            case 1 -> 0;
            case -1 -> -(this.length - 1);
            default -> vertical ? 0 : -(breadth / 2);
        };
    }

    private int axisUpperBound(int directionComponent, boolean vertical, int breadth) {
        return switch (directionComponent) {
            case 1 -> this.length - 1;
            case -1 -> 0;
            default -> vertical ? breadth - 1 : breadth - 1 - breadth / 2;
        };
    }

    private boolean withinLength(BlastVector offset) {
        int along = offset.x() * this.direction.x()
                + offset.y() * this.direction.y()
                + offset.z() * this.direction.z();

        return along >= 0 && along < this.length;
    }

    private boolean withinCrossSection(BlastVector offset) {
        return this.withinCrossAxis(offset.x(), this.direction.x(), this.lowerBound.x(), this.upperBound.x())
                && this.withinCrossAxis(offset.y(), this.direction.y(), this.lowerBound.y(), this.upperBound.y())
                && this.withinCrossAxis(offset.z(), this.direction.z(), this.lowerBound.z(), this.upperBound.z());
    }

    private boolean withinCrossAxis(int coordinate, int directionComponent, int lower, int upper) {
        if (directionComponent != 0) {
            return true;
        }

        return coordinate >= lower && coordinate <= upper;
    }

    private void collectWhenInside(List<BlastVector> volume, BlastVector offset) {
        if (!this.contains(offset)) {
            return;
        }

        volume.add(offset);
    }
}
