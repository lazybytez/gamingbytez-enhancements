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
package de.lazybytez.gamingbytezenhancements.feature.customcreeperdamage.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Covers the damage arithmetic of {@link CreeperDamageListener}.
 * <p>
 * The reference figures come from a live server: a blast of 17.85 reached a netherite clad player
 * as 6.39.
 */
class CreeperDamageListenerTest {

    private final CreeperDamageListener listener = new CreeperDamageListener(null);

    @Test
    void baseFor_raisesTheBaseSoTheIntendedDamageSurvivesArmour() {
        // Numbers observed on a server: a blast of 17.85 reached a netherite clad player as 6.39.
        double base = this.listener.baseFor(19.77, 17.85, 6.39);

        assertEquals(19.77 * (17.85 / 6.39), base, 0.0001);
        assertTrue(base > 19.77, "an armoured player needs a raised base, not the intended number");
    }

    @Test
    void baseFor_appliedReductionLandsOnTheIntendedDamage() {
        double reductionKept = 6.39 / 17.85;

        double base = this.listener.baseFor(19.77, 17.85, 6.39);

        assertEquals(19.77, base * reductionKept, 0.0001);
    }

    @Test
    void baseFor_withoutAnyReduction_leavesTheIntendedDamageAlone() {
        assertEquals(7.5, this.listener.baseFor(7.5, 12.0, 12.0), 0.0001);
    }

    @Test
    void baseFor_whenTheHitWasFullyAbsorbed_keepsTheIntendedDamage() {
        assertEquals(7.5, this.listener.baseFor(7.5, 12.0, 0.0), 0.0001);
    }

    @Test
    void baseFor_withoutAnyIncomingDamage_keepsTheIntendedDamage() {
        assertEquals(7.5, this.listener.baseFor(7.5, 0.0, 0.0), 0.0001);
    }

    @Test
    void baseFor_whenTheServerRaisesTheDamage_lowersTheBase() {
        // Freezing and similar effects add damage rather than removing it.
        double base = this.listener.baseFor(10.0, 8.0, 16.0);

        assertEquals(5.0, base, 0.0001);
        assertTrue(base < 10.0, "damage the server raises needs a lowered base");
    }
}
