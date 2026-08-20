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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ChainResolverTest {

    private static final int CHAIN_REACH = 6;

    @Test
    void sessionForInitiatorReportsFourRemainingCapacity() {
        ChainSession session = new ChainSession(UUID.randomUUID());

        assertEquals(4, session.remainingCapacity());
        assertEquals(1, session.recruitedCount());
    }

    @Test
    void candidateOutsideReachIsNotReturned() {
        ChainCandidate detonating = ChainResolverTest.candidateAt(0, 0, 0);
        ChainCandidate outOfReach = ChainResolverTest.candidateAt(7, 0, 0);
        ChainSession session = new ChainSession(detonating.id());

        List<ChainCandidate> woken = ChainResolver.resolve(
                detonating,
                ChainResolverTest.CHAIN_REACH,
                List.of(outOfReach),
                session);

        assertTrue(woken.isEmpty());
        assertFalse(session.hasRecruited(outOfReach.id()));
    }

    @Test
    void candidateOnTheReachBoundaryIsReturned() {
        ChainCandidate detonating = ChainResolverTest.candidateAt(0, 0, 0);
        ChainCandidate onBoundary = ChainResolverTest.candidateAt(6, 0, 0);
        ChainSession session = new ChainSession(detonating.id());

        List<ChainCandidate> woken = ChainResolver.resolve(
                detonating,
                ChainResolverTest.CHAIN_REACH,
                List.of(onBoundary),
                session);

        assertEquals(List.of(onBoundary), woken);
    }

    @Test
    void alreadyRecruitedCandidateIsNotReturnedASecondTime() {
        ChainCandidate detonating = ChainResolverTest.candidateAt(0, 0, 0);
        ChainCandidate neighbour = ChainResolverTest.candidateAt(1, 0, 0);
        ChainSession session = new ChainSession(detonating.id());
        List<ChainCandidate> placed = List.of(detonating, neighbour);

        List<ChainCandidate> first = ChainResolver.resolve(
                detonating,
                ChainResolverTest.CHAIN_REACH,
                placed,
                session);
        List<ChainCandidate> second = ChainResolver.resolve(
                detonating,
                ChainResolverTest.CHAIN_REACH,
                placed,
                session);

        assertEquals(List.of(neighbour), first);
        assertTrue(second.isEmpty());
    }

    @Test
    void resolveReturnsAtMostTheRemainingCapacity() {
        ChainCandidate detonating = ChainResolverTest.candidateAt(0, 0, 0);
        List<ChainCandidate> placed = new ArrayList<>();
        placed.add(detonating);

        for (int index = 0; index < 20; index++) {
            placed.add(ChainResolverTest.candidateAt(1, 0, 0));
        }

        ChainSession session = new ChainSession(detonating.id());

        List<ChainCandidate> woken = ChainResolver.resolve(
                detonating,
                ChainResolverTest.CHAIN_REACH,
                placed,
                session);

        assertEquals(4, woken.size());
        assertEquals(0, session.remainingCapacity());
    }

    @Test
    void branchingCascadeRecruitsExactlyFiveIdentities() {
        List<ChainCandidate> placed = new ArrayList<>();

        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                placed.add(ChainResolverTest.candidateAt(x, 0, z));
            }
        }

        ChainCandidate initiator = placed.get(0);
        ChainSession session = new ChainSession(initiator.id());
        Set<UUID> detonated = new HashSet<>();
        Deque<ChainCandidate> pending = new ArrayDeque<>();
        pending.add(initiator);

        while (!pending.isEmpty()) {
            ChainCandidate detonating = pending.poll();
            detonated.add(detonating.id());

            pending.addAll(ChainResolver.resolve(
                    detonating,
                    ChainResolverTest.CHAIN_REACH,
                    placed,
                    session));
        }

        assertEquals(5, session.recruitedCount());
        assertEquals(5, detonated.size());
        assertEquals(0, session.remainingCapacity());
    }

    @Test
    void releaseGivesTheSlotBackToTheCascade() {
        ChainCandidate initiator = ChainResolverTest.candidateAt(0, 0, 0);
        ChainCandidate neighbour = ChainResolverTest.candidateAt(1, 0, 0);

        ChainSession session = new ChainSession(initiator.id());
        ChainResolver.resolve(initiator, ChainResolverTest.CHAIN_REACH, List.of(neighbour), session);

        assertEquals(2, session.recruitedCount());

        assertTrue(session.release(neighbour.id()));
        assertEquals(1, session.recruitedCount());
        assertFalse(session.hasRecruited(neighbour.id()));
    }

    @Test
    void releaseIgnoresAnIdentityTheCascadeNeverRecruited() {
        ChainCandidate initiator = ChainResolverTest.candidateAt(0, 0, 0);
        ChainSession session = new ChainSession(initiator.id());

        assertFalse(session.release(UUID.randomUUID()));
        assertEquals(1, session.recruitedCount());
    }

    private static ChainCandidate candidateAt(int x, int y, int z) {
        return new ChainCandidate(UUID.randomUUID(), new BlastVector(x, y, z));
    }
}
