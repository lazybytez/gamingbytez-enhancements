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
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.function.DoubleUnaryOperator;

import org.bukkit.event.entity.EntityDamageEvent;
import org.junit.jupiter.api.Test;

/**
 * Covers the base damage search of {@link CreeperDamageListener}.
 * <p>
 * The search has to land the intended health loss through a reduction it cannot see, so each test
 * stands a reduction in front of it and reads back what the player would actually lose.
 */
class CreeperDamageListenerTest {
    private static final double LANDING_TOLERANCE = 0.05;

    private final CreeperDamageListener listener = new CreeperDamageListener(null);

    @Test
    void applyAsFinalDamage_throughVanillaArmor_landsOnTheIntendedDamage() {
        double[] base = new double[1];
        EntityDamageEvent event = this.eventReducedBy(CreeperDamageListenerTest::netheriteWithProtection, base);

        this.listener.applyAsFinalDamage(event, 13.8);

        assertEquals(13.8, CreeperDamageListenerTest.netheriteWithProtection(base[0]),
                CreeperDamageListenerTest.LANDING_TOLERANCE);
    }

    @Test
    void applyAsFinalDamage_throughVanillaArmor_raisesTheBaseWellAboveTheIntendedDamage() {
        double[] base = new double[1];
        EntityDamageEvent event = this.eventReducedBy(CreeperDamageListenerTest::netheriteWithProtection, base);

        this.listener.applyAsFinalDamage(event, 13.8);

        assertTrue(base[0] > 13.8, "armour has to be paid for with a larger base");
    }

    @Test
    void applyAsFinalDamage_whenReductionScalesWithDamage_doesNotOvershoot() {
        double[] base = new double[1];
        EntityDamageEvent event = this.eventReducedBy(CreeperDamageListenerTest::netheriteWithProtection, base);

        this.listener.applyAsFinalDamage(event, 13.8);

        double linearGuess = 13.8 * (28.0 / CreeperDamageListenerTest.netheriteWithProtection(28.0));

        assertTrue(
                base[0] < linearGuess,
                "a ratio measured on a small hit overstates the base a large one needs"
        );
    }

    @Test
    void applyAsFinalDamage_withoutAnyReduction_setsTheIntendedDamage() {
        double[] base = new double[1];
        EntityDamageEvent event = this.eventReducedBy(damage -> damage, base);

        this.listener.applyAsFinalDamage(event, 7.5);

        assertEquals(7.5, base[0], CreeperDamageListenerTest.LANDING_TOLERANCE);
    }

    @Test
    void applyAsFinalDamage_throughAFlatReduction_landsOnTheIntendedDamage() {
        double[] base = new double[1];
        EntityDamageEvent event = this.eventReducedBy(damage -> damage * 0.25, base);

        this.listener.applyAsFinalDamage(event, 9.0);

        assertEquals(36.0, base[0], CreeperDamageListenerTest.LANDING_TOLERANCE);
    }

    @Test
    void applyAsFinalDamage_withNothingLeftToDeal_setsNoDamage() {
        double[] base = new double[1];
        EntityDamageEvent event = this.eventReducedBy(damage -> damage, base);

        this.listener.applyAsFinalDamage(event, 0.0);

        assertEquals(0.0, base[0], CreeperDamageListenerTest.LANDING_TOLERANCE);
    }

    /**
     * Vanilla armor reduction for a netherite set carrying Protection IV on every piece. Armor
     * sheds a smaller share of a large hit than of a small one, which is what the search exists to
     * cope with.
     *
     * @param baseDamage The damage before reduction.
     * @return The health the player would lose.
     */
    private static double netheriteWithProtection(double baseDamage) {
        double armorKept = Math.min(20.0, Math.max(20.0 / 5.0, 20.0 - baseDamage / 5.0));

        return baseDamage * (1.0 - armorKept / 25.0) * 0.36;
    }

    private EntityDamageEvent eventReducedBy(DoubleUnaryOperator reduction, double[] base) {
        EntityDamageEvent event = mock(EntityDamageEvent.class);

        doAnswer(invocation -> {
            base[0] = invocation.getArgument(0);

            return null;
        }).when(event).setDamage(anyDouble());
        when(event.getFinalDamage()).thenAnswer(invocation -> reduction.applyAsDouble(base[0]));

        return event;
    }
}
