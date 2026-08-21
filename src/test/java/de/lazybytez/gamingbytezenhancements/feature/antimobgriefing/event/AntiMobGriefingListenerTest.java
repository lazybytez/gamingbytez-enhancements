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
package de.lazybytez.gamingbytezenhancements.feature.antimobgriefing.event;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.junit.jupiter.api.Test;

/**
 * Covers the hanging entity protection of {@link AntiMobGriefingListener}.
 * <p>
 * The remover of a shot hanging entity is the arrow, so these cover which shooter is behind it.
 */
class AntiMobGriefingListenerTest {

    private final AntiMobGriefingListener listener = new AntiMobGriefingListener();

    @Test
    void onHangingEntityBreak_withASkeletonsArrow_isCancelled() {
        HangingBreakByEntityEvent event = this.breakEvent(
                HangingBreakByEntityEvent.RemoveCause.ENTITY,
                this.arrowShotBy(mock(Skeleton.class)),
                EntityType.PAINTING);

        this.listener.onHangingEntityBreak(event);

        verify(event).setCancelled(true);
    }

    @Test
    void onHangingEntityBreak_withASkeletonsArrowHittingAnItemFrame_isCancelled() {
        HangingBreakByEntityEvent event = this.breakEvent(
                HangingBreakByEntityEvent.RemoveCause.ENTITY,
                this.arrowShotBy(mock(Skeleton.class)),
                EntityType.GLOW_ITEM_FRAME);

        this.listener.onHangingEntityBreak(event);

        verify(event).setCancelled(true);
    }

    @Test
    void onHangingEntityBreak_withAPlayersArrow_isLeftAlone() {
        HangingBreakByEntityEvent event = this.breakEvent(
                HangingBreakByEntityEvent.RemoveCause.ENTITY,
                this.arrowShotBy(mock(Player.class)),
                EntityType.PAINTING);

        this.listener.onHangingEntityBreak(event);

        verify(event, never()).setCancelled(true);
    }

    @Test
    void onHangingEntityBreak_withACreeperExplosion_isCancelled() {
        Creeper creeper = mock(Creeper.class);
        when(creeper.getType()).thenReturn(EntityType.CREEPER);

        HangingBreakByEntityEvent event = this.breakEvent(
                HangingBreakByEntityEvent.RemoveCause.EXPLOSION,
                creeper,
                EntityType.ITEM_FRAME);

        this.listener.onHangingEntityBreak(event);

        verify(event).setCancelled(true);
    }

    @Test
    void onHangingEntityBreak_withAnUnprotectedHangingEntity_isLeftAlone() {
        HangingBreakByEntityEvent event = this.breakEvent(
                HangingBreakByEntityEvent.RemoveCause.ENTITY,
                this.arrowShotBy(mock(Skeleton.class)),
                EntityType.LEASH_KNOT);

        this.listener.onHangingEntityBreak(event);

        verify(event, never()).setCancelled(true);
    }

    private Arrow arrowShotBy(Object shooter) {
        Arrow arrow = mock(Arrow.class);
        when(arrow.getType()).thenReturn(EntityType.ARROW);
        when(arrow.getShooter()).thenReturn((org.bukkit.projectiles.ProjectileSource) shooter);

        return arrow;
    }

    private HangingBreakByEntityEvent breakEvent(
            HangingBreakByEntityEvent.RemoveCause cause,
            Entity remover,
            EntityType hangingType
    ) {
        Hanging hanging = mock(Hanging.class);
        when(hanging.getType()).thenReturn(hangingType);

        HangingBreakByEntityEvent event = mock(HangingBreakByEntityEvent.class);
        when(event.getCause()).thenReturn(cause);
        when(event.getRemover()).thenReturn(remover);
        when(event.getEntity()).thenReturn(hanging);

        return event;
    }
}
