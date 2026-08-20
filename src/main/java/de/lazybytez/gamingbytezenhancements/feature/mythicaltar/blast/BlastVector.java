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
 * A whole-block offset in the blast coordinate space, measured from the block a charge
 * detonates in.
 * <p>
 * Distances are exposed squared only. A blast compares and orders tens of thousands of offsets
 * per detonation, and every ordering or containment question a geometry asks is answered the
 * same way by the squared value, so the square root never has to be taken.
 *
 * @param x the offset along the x axis in blocks
 * @param y the offset along the y axis in blocks
 * @param z the offset along the z axis in blocks
 */
public record BlastVector(int x, int y, int z) {

    /**
     * The offset of the block a charge detonates in.
     */
    public static final BlastVector ORIGIN = new BlastVector(0, 0, 0);

    /**
     * Returns the squared distance between this offset and the given one.
     *
     * @param other the offset to measure to
     * @return the squared distance in blocks
     */
    public int squaredDistanceTo(BlastVector other) {
        int deltaX = this.x - other.x();
        int deltaY = this.y - other.y();
        int deltaZ = this.z - other.z();

        return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
    }

    /**
     * Returns the squared distance between this offset and {@link #ORIGIN}.
     *
     * @return the squared distance from the blast centre in blocks
     */
    public int squaredLength() {
        return this.squaredDistanceTo(BlastVector.ORIGIN);
    }
}
