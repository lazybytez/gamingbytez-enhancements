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
 * A ball of radius {@link BlastLevel#getSize()} halved, hanging below the detonating block.
 * <p>
 * The ball touches the charge from below rather than surrounding it, so every shape digs away
 * from the charge instead of into the air above it.
 * <p>
 * A block joins the volume when its centre is within the radius, so the boundary is decided by
 * one comparison per block rather than by how much of the block the ball covers. That is what
 * keeps the carved shell round instead of stepped.
 */
public final class SphereBlastGeometry implements BlastGeometry {

    private final int boundingOffset;
    private final int centreOffset;
    private final double squaredRadius;

    /**
     * Creates the sphere geometry for the given blast level.
     *
     * @param level the blast level supplying the diameter
     */
    public SphereBlastGeometry(BlastLevel level) {
        Objects.requireNonNull(level, "level must not be null");

        double radius = level.getSize() / 2.0;

        this.boundingOffset = (int) Math.floor(radius);
        this.centreOffset = -this.boundingOffset;
        this.squaredRadius = radius * radius;
    }

    @Override
    public Stream<BlastVector> offsets() {
        List<BlastVector> volume = new ArrayList<>();

        for (int x = -this.boundingOffset; x <= this.boundingOffset; x++) {
            for (int y = this.centreOffset - this.boundingOffset; y <= this.centreOffset + this.boundingOffset; y++) {
                for (int z = -this.boundingOffset; z <= this.boundingOffset; z++) {
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

        int verticalDistance = offset.y() - this.centreOffset;

        return offset.x() * offset.x()
                + verticalDistance * verticalDistance
                + offset.z() * offset.z() <= this.squaredRadius;
    }

    private void collectWhenInside(List<BlastVector> volume, BlastVector offset) {
        if (!this.contains(offset)) {
            return;
        }

        volume.add(offset);
    }
}
