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
 * An axis-aligned box measuring {@link BlastLevel#getSize()} blocks along every edge, hanging
 * below the detonating block.
 * <p>
 * The box is centred horizontally on the charge and extends downwards from it, so the charge sits
 * in the top layer. A box centred on the charge would spend half its height on the air above a
 * player's head, which on flat ground carves a shallow square instead of the pit the shape
 * promises.
 */
public final class CuboidBlastGeometry implements BlastGeometry {

    private final int minHorizontalOffset;
    private final int maxHorizontalOffset;
    private final int minVerticalOffset;

    /**
     * Creates the cuboid geometry for the given blast level.
     *
     * @param level the blast level supplying the edge length
     */
    public CuboidBlastGeometry(BlastLevel level) {
        Objects.requireNonNull(level, "level must not be null");

        int size = level.getSize();

        this.minHorizontalOffset = -(size / 2);
        this.maxHorizontalOffset = this.minHorizontalOffset + size - 1;
        this.minVerticalOffset = -(size - 1);
    }

    @Override
    public Stream<BlastVector> offsets() {
        List<BlastVector> volume = new ArrayList<>();

        for (int x = this.minHorizontalOffset; x <= this.maxHorizontalOffset; x++) {
            for (int y = this.minVerticalOffset; y <= 0; y++) {
                for (int z = this.minHorizontalOffset; z <= this.maxHorizontalOffset; z++) {
                    volume.add(new BlastVector(x, y, z));
                }
            }
        }

        volume.sort(Comparator.comparingInt(BlastVector::squaredLength));

        return volume.stream();
    }

    @Override
    public boolean contains(BlastVector offset) {
        Objects.requireNonNull(offset, "offset must not be null");

        return this.withinHorizontalEdge(offset.x())
                && this.withinHorizontalEdge(offset.z())
                && offset.y() >= this.minVerticalOffset
                && offset.y() <= 0;
    }

    private boolean withinHorizontalEdge(int coordinate) {
        return coordinate >= this.minHorizontalOffset && coordinate <= this.maxHorizontalOffset;
    }
}
