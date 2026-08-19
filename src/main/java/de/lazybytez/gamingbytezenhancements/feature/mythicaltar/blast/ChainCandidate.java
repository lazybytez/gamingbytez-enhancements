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
import java.util.UUID;

/**
 * A placed excavation charge as the chain logic sees it: an identity and a position.
 * <p>
 * The identity is what the chain session recruits, so two charges sharing a position stay
 * distinguishable and a charge can never be woken twice by the same cascade.
 *
 * @param id the identity of the placed charge
 * @param position the block position of the placed charge
 */
public record ChainCandidate(UUID id, BlastVector position) {

    /**
     * Validates that neither the identity nor the position is missing.
     */
    public ChainCandidate {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(position, "position must not be null");
    }
}
