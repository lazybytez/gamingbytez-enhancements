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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * An upright cylinder of half the given size in radius and of the given size in height, hanging
 * below the detonating block.
 * <p>
 * The charge sits in the top layer and the shaft is sunk from there, so the shape digs ground
 * rather than the air above it.
 * <p>
 * A block joins the volume when its centre is within the radius on the horizontal plane and within
 * half the height on the vertical axis, the same by-centre judgement the sphere uses. That keeps
 * the carved wall round instead of stepped while the floor and the ceiling stay flat.
 */
public final class CylinderBlastGeometry implements BlastGeometry {

    private final int horizontalOffset;
    private final int depth;
    private final double squaredRadius;

    /**
     * Creates the cylinder geometry for the given diameter and height.
     *
     * @param size the diameter and the height in blocks
     */
    public CylinderBlastGeometry(int size) {
        double radius = size / 2.0;

        this.horizontalOffset = (int) Math.floor(radius);
        this.depth = -(size - 1);
        this.squaredRadius = radius * radius;
    }

    @Override
    public Stream<BlastVector> offsets() {
        List<BlastVector> volume = new ArrayList<>();

        for (int x = -this.horizontalOffset; x <= this.horizontalOffset; x++) {
            for (int y = this.depth; y <= 0; y++) {
                for (int z = -this.horizontalOffset; z <= this.horizontalOffset; z++) {
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

        return this.withinRadius(offset) && this.withinHeight(offset.y());
    }

    private boolean withinRadius(BlastVector offset) {
        int squaredHorizontalLength = offset.x() * offset.x() + offset.z() * offset.z();

        return squaredHorizontalLength <= this.squaredRadius;
    }

    private boolean withinHeight(int y) {
        return y >= this.depth && y <= 0;
    }

    private void collectWhenInside(List<BlastVector> volume, BlastVector offset) {
        if (!this.contains(offset)) {
            return;
        }

        volume.add(offset);
    }
}
