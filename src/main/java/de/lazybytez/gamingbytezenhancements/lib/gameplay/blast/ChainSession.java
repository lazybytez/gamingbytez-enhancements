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

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * The shared state of one chain reaction, from the charge that started it to the last charge it woke.
 * <p>
 * The cap is on the whole cascade rather than on the neighbours of a single charge, because a
 * per-charge limit is unbounded through branching: with a limit of two neighbours the initiator wakes
 * two charges, each of those wakes two more, and the cascade grows without end while every single
 * step looks compliant. One session is therefore created for the initiator and handed by reference to
 * every resolve the cascade performs, so the capacity every charge sees is what the cascade has left
 * rather than what that charge alone may take.
 */
public final class ChainSession {

    private static final int MAX_CHAIN_MEMBERS = 5;

    private final Set<UUID> recruited = new LinkedHashSet<>();

    /**
     * Opens a chain reaction for the charge that detonated on its own.
     *
     * @param initiator the identity of the charge starting the cascade, counted as its first member
     */
    public ChainSession(UUID initiator) {
        Objects.requireNonNull(initiator, "initiator must not be null");

        this.recruited.add(initiator);
    }

    /**
     * Returns how many further charges this cascade may still recruit.
     *
     * @return the number of free slots below the cap, never negative
     */
    public int remainingCapacity() {
        return ChainSession.MAX_CHAIN_MEMBERS - this.recruited.size();
    }

    /**
     * Returns how many charges belong to this cascade, counting the initiator.
     *
     * @return the number of recruited identities
     */
    public int recruitedCount() {
        return this.recruited.size();
    }

    /**
     * Tells whether the given charge already belongs to this cascade.
     *
     * @param candidateId the identity to look up
     * @return true when the identity was already recruited
     */
    public boolean hasRecruited(UUID candidateId) {
        return this.recruited.contains(candidateId);
    }

    /**
     * Adds a charge to this cascade and spends one slot of its remaining capacity.
     *
     * @param candidateId the identity to recruit
     * @return true when the identity was new to this cascade
     */
    public boolean recruit(UUID candidateId) {
        Objects.requireNonNull(candidateId, "candidateId must not be null");

        return this.recruited.add(candidateId);
    }

    /**
     * Gives a recruited charge's slot back to this cascade.
     * <p>
     * A charge can be recruited and then turn out to be counting down already, because a
     * neighbouring cascade reached it first. Holding its slot would shrink this cascade by a
     * charge that never detonates as part of it.
     *
     * @param candidateId the identity to release
     * @return true when the identity had been recruited by this cascade
     */
    public boolean release(UUID candidateId) {
        Objects.requireNonNull(candidateId, "candidateId must not be null");

        return this.recruited.remove(candidateId);
    }
}
