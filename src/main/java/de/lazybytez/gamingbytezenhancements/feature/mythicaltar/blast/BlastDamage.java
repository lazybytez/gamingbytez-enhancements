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
 * The damage falloff curve a blast applies to an entity caught in its volume.
 * <p>
 * Damage is linear in distance from the centre: it equals the centre damage at distance zero,
 * zero at the volume's maximum extent, and stays zero beyond it. The arithmetic takes plain
 * doubles rather than blast or entity types, so it holds no server state and computes purely from
 * its arguments. Finding the entities to damage and applying that damage to them is a caller
 * concern.
 */
public final class BlastDamage {

    private BlastDamage() {
    }

    /**
     * Computes the damage an entity takes at the given distance from the blast centre.
     * <p>
     * A maximum extent of zero collapses the volume to a single point, so any positive distance
     * is already beyond the edge; the method returns zero rather than dividing by zero.
     *
     * @param centreDamage the damage dealt at distance zero
     * @param distance     the distance from the blast centre to the entity
     * @param maxExtent    the volume's maximum extent, at which damage reaches zero
     * @return the damage to apply, never negative
     */
    public static double falloff(double centreDamage, double distance, double maxExtent) {
        if (maxExtent <= 0.0) {
            return 0.0;
        }

        double damage = centreDamage * (1.0 - distance / maxExtent);

        return Math.max(damage, 0.0);
    }
}
