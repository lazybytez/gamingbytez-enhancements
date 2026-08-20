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
import java.util.List;
import java.util.Objects;

/**
 * Decides which placed charges a detonating charge wakes.
 */
public final class ChainResolver {

    private ChainResolver() {
    }

    /**
     * Returns the charges the detonating charge wakes and books them into the cascade.
     * <p>
     * The returned charges are recruited before they are handed back, because the next charge of the
     * cascade resolves against the same session: without booking them here that resolve would hand
     * out capacity this one already spent, and a branching cascade would exceed the cap while every
     * single resolve stayed within it.
     *
     * @param detonating the charge going off right now
     * @param geometry the volume the detonating charge carves
     * @param placed every charge placed in the world that may take part
     * @param session the shared state of the running cascade
     * @return the charges to wake, at most as many as the session has capacity left
     */
    public static List<ChainCandidate> resolve(
            ChainCandidate detonating,
            BlastGeometry geometry,
            List<ChainCandidate> placed,
            ChainSession session
    ) {
        Objects.requireNonNull(detonating, "detonating must not be null");
        Objects.requireNonNull(geometry, "geometry must not be null");
        Objects.requireNonNull(placed, "placed must not be null");
        Objects.requireNonNull(session, "session must not be null");

        int remaining = session.remainingCapacity();

        if (remaining <= 0) {
            return List.of();
        }

        List<ChainCandidate> woken = new ArrayList<>(remaining);

        for (ChainCandidate candidate : placed) {
            if (woken.size() >= remaining) {
                break;
            }

            if (!ChainResolver.isWakeable(detonating, geometry, candidate, session)) {
                continue;
            }

            session.recruit(candidate.id());
            woken.add(candidate);
        }

        return List.copyOf(woken);
    }

    private static boolean isWakeable(
            ChainCandidate detonating,
            BlastGeometry geometry,
            ChainCandidate candidate,
            ChainSession session
    ) {
        if (candidate.id().equals(detonating.id())) {
            return false;
        }

        if (session.hasRecruited(candidate.id())) {
            return false;
        }

        return ChainResolver.isInsideBlast(detonating.position(), geometry, candidate.position());
    }

    /**
     * Tells whether a candidate stands inside the volume the detonating charge carves.
     * <p>
     * Waking by the blast volume rather than a fixed radius means every charge the explosion
     * visibly reaches goes off with it, which is what a player laying out a chain expects.
     *
     * @param origin the position of the detonating charge
     * @param geometry the volume the detonating charge carves
     * @param target the position of the candidate
     * @return whether the candidate stands inside the carved volume
     */
    private static boolean isInsideBlast(BlastVector origin, BlastGeometry geometry, BlastVector target) {
        return geometry.contains(target.minus(origin));
    }
}
