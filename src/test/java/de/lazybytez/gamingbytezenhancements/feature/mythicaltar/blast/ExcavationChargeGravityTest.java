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

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExcavationChargeGravityTest {

    private EnhancementsPlugin plugin;
    private Server server;
    private World world;
    private ExcavationChargeGravity gravity;
    private Runnable tick;

    @BeforeEach
    void setUp() {
        this.plugin = mock(EnhancementsPlugin.class);
        this.server = mock(Server.class);
        this.world = mock(World.class);
        BukkitScheduler scheduler = mock(BukkitScheduler.class);

        when(this.plugin.getServer()).thenReturn(this.server);
        when(this.plugin.namespace()).thenReturn("gamingbytez-enhancements");
        when(this.server.getScheduler()).thenReturn(scheduler);
        when(this.server.getWorlds()).thenReturn(List.of(this.world));
        lenient().when(this.world.getMinHeight()).thenReturn(-64);
        doAnswer(invocation -> {
            this.tick = invocation.getArgument(1, Runnable.class);

            return mock(org.bukkit.scheduler.BukkitTask.class);
        }).when(scheduler).runTaskTimer(any(), any(Runnable.class), anyLong(), anyLong());

        this.gravity = new ExcavationChargeGravity(this.plugin);
        this.gravity.start();
    }

    @Test
    void sweepsTheSpeedOfAChargeThatVanishedMidFall() {
        EnderCrystal falling = this.chargeAt(64.0, true);
        when(this.world.getEntitiesByClass(EnderCrystal.class)).thenReturn(List.of(falling));

        this.tick.run();
        assertEquals(1, this.gravity.trackedFallingCharges());

        when(this.world.getEntitiesByClass(EnderCrystal.class)).thenReturn(List.of());
        this.tick.run();

        assertEquals(0, this.gravity.trackedFallingCharges());
    }

    @Test
    void ignoresACrystalWithoutTheChargeMarker() {
        EnderCrystal foreign = this.chargeAt(64.0, false);
        when(this.world.getEntitiesByClass(EnderCrystal.class)).thenReturn(List.of(foreign));

        this.tick.run();

        assertEquals(0, this.gravity.trackedFallingCharges());
        verify(foreign, org.mockito.Mockito.never()).teleport(any(Location.class));
    }

    private EnderCrystal chargeAt(double y, boolean marked) {
        EnderCrystal crystal = mock(EnderCrystal.class);
        PersistentDataContainer container = mock(PersistentDataContainer.class);

        when(crystal.getPersistentDataContainer()).thenReturn(container);
        when(container.getOrDefault(any(), any(), any()))
                .thenAnswer(invocation -> marked ? Boolean.TRUE : invocation.getArgument(2));

        if (marked) {
            when(crystal.getUniqueId()).thenReturn(UUID.randomUUID());
            when(crystal.getLocation()).thenReturn(new Location(this.world, 0.5, y, 0.5));
            when(crystal.getWorld()).thenReturn(this.world);

            Block air = mock(Block.class);
            when(air.isPassable()).thenReturn(true);
            when(this.world.getBlockAt(anyInt(), anyInt(), anyInt())).thenReturn(air);
        }

        return crystal;
    }
}
