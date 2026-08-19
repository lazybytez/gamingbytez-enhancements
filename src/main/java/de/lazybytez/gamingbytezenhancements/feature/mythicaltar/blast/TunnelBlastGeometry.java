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
 * Only the length follows the blast level. The cross-section stays three blocks wide and three
 * blocks high at every level, which is what makes this a walkable mining corridor rather than a
 * cavern and what makes it the cheapest shape per level by a wide margin.
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

    private static final int CROSS_SECTION_WIDTH = 3;
    private static final int CROSS_SECTION_HEIGHT = 3;
    private static final int HALF_CROSS_SECTION_WIDTH = TunnelBlastGeometry.CROSS_SECTION_WIDTH / 2;
    private static final int HALF_CROSS_SECTION_HEIGHT = TunnelBlastGeometry.CROSS_SECTION_HEIGHT / 2;

    private final BlastVector direction;
    private final int length;
    private final BlastVector lowerBound;
    private final BlastVector upperBound;

    /**
     * Creates the tunnel geometry for the given blast level and travel direction.
     *
     * @param level     the blast level supplying the corridor length
     * @param direction the axis-aligned unit offset the corridor is bored along
     * @throws IllegalArgumentException when the direction is not an axis-aligned unit offset
     */
    public TunnelBlastGeometry(BlastLevel level, BlastVector direction) {
        Objects.requireNonNull(level, "level must not be null");
        Objects.requireNonNull(direction, "direction must not be null");
        TunnelBlastGeometry.requireAxisAlignedUnit(direction);

        this.direction = direction;
        this.length = level.getSize();
        this.lowerBound = new BlastVector(
                this.axisLowerBound(direction.x(), TunnelBlastGeometry.HALF_CROSS_SECTION_WIDTH),
                this.axisLowerBound(direction.y(), TunnelBlastGeometry.HALF_CROSS_SECTION_HEIGHT),
                this.axisLowerBound(direction.z(), TunnelBlastGeometry.HALF_CROSS_SECTION_WIDTH));
        this.upperBound = new BlastVector(
                this.axisUpperBound(direction.x(), TunnelBlastGeometry.HALF_CROSS_SECTION_WIDTH),
                this.axisUpperBound(direction.y(), TunnelBlastGeometry.HALF_CROSS_SECTION_HEIGHT),
                this.axisUpperBound(direction.z(), TunnelBlastGeometry.HALF_CROSS_SECTION_WIDTH));
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

    private static void requireAxisAlignedUnit(BlastVector direction) {
        int magnitude = Math.abs(direction.x()) + Math.abs(direction.y()) + Math.abs(direction.z());

        if (magnitude == 1) {
            return;
        }

        throw new IllegalArgumentException("direction must be an axis-aligned unit offset, got " + direction);
    }

    private static boolean withinCrossAxis(int coordinate, int directionComponent, int halfExtent) {
        if (directionComponent != 0) {
            return true;
        }

        return Math.abs(coordinate) <= halfExtent;
    }

    private int axisLowerBound(int directionComponent, int halfExtent) {
        return switch (directionComponent) {
            case 1 -> 0;
            case -1 -> -(this.length - 1);
            default -> -halfExtent;
        };
    }

    private int axisUpperBound(int directionComponent, int halfExtent) {
        return switch (directionComponent) {
            case 1 -> this.length - 1;
            case -1 -> 0;
            default -> halfExtent;
        };
    }

    private boolean withinLength(BlastVector offset) {
        int along = offset.x() * this.direction.x()
                + offset.y() * this.direction.y()
                + offset.z() * this.direction.z();

        return along >= 0 && along < this.length;
    }

    private boolean withinCrossSection(BlastVector offset) {
        return TunnelBlastGeometry.withinCrossAxis(
                        offset.x(), this.direction.x(), TunnelBlastGeometry.HALF_CROSS_SECTION_WIDTH)
                && TunnelBlastGeometry.withinCrossAxis(
                        offset.y(), this.direction.y(), TunnelBlastGeometry.HALF_CROSS_SECTION_HEIGHT)
                && TunnelBlastGeometry.withinCrossAxis(
                        offset.z(), this.direction.z(), TunnelBlastGeometry.HALF_CROSS_SECTION_WIDTH);
    }

    private void collectWhenInside(List<BlastVector> volume, BlastVector offset) {
        if (!this.contains(offset)) {
            return;
        }

        volume.add(offset);
    }
}
