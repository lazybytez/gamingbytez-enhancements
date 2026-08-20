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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class ExcavationChargeFuseTest {

    private static final int GENEROUS_SAMPLE_CAP = 100_000;
    private static final int TASK_ID = 42;

    private MockedStatic<Bukkit> bukkit;
    private BukkitScheduler scheduler;
    private EnhancementsPlugin plugin;
    private ExcavationChargeDetonator detonator;
    private ExcavationChargeAuditLog auditLog;
    private World world;
    private Runnable tick;

    @BeforeEach
    void setUp() {
        this.plugin = mock(EnhancementsPlugin.class);
        this.detonator = mock(ExcavationChargeDetonator.class);
        this.auditLog = mock(ExcavationChargeAuditLog.class);
        this.world = mock(World.class);
        this.scheduler = mock(BukkitScheduler.class);

        BukkitTask task = mock(BukkitTask.class);
        when(task.getTaskId()).thenReturn(ExcavationChargeFuseTest.TASK_ID);
        doAnswer(invocation -> {
            this.tick = invocation.getArgument(1, Runnable.class);

            return task;
        }).when(this.scheduler).runTaskTimer(any(), any(Runnable.class), anyLong(), anyLong());

        this.bukkit = mockStatic(Bukkit.class);
        this.bukkit.when(Bukkit::getScheduler).thenReturn(this.scheduler);
    }

    @AfterEach
    void tearDown() {
        this.bukkit.close();
    }

    @Test
    void aVanishedChargeStopsTheFuseAndCancelsItsTask() {
        UUID chargeId = UUID.randomUUID();
        EnderCrystal charge = this.armedCharge(chargeId);
        ExcavationChargeFuse fuse = this.fuse();

        assertTrue(fuse.armForPlayer(charge));

        this.bukkit.when(() -> Bukkit.getEntity(chargeId)).thenReturn(null);
        this.tick.run();

        assertFalse(fuse.isBurning(charge));
        verify(this.scheduler).cancelTask(ExcavationChargeFuseTest.TASK_ID);
    }

    @Test
    void aChargeStillStandingKeepsItsFuseBurning() {
        UUID chargeId = UUID.randomUUID();
        EnderCrystal charge = this.armedCharge(chargeId);
        ExcavationChargeFuse fuse = this.fuse();

        assertTrue(fuse.armForPlayer(charge));

        this.bukkit.when(() -> Bukkit.getEntity(chargeId)).thenReturn(charge);
        this.tick.run();

        assertTrue(fuse.isBurning(charge));
        verify(this.scheduler, never()).cancelTask(ExcavationChargeFuseTest.TASK_ID);
    }

    @Test
    void aChargeThatLostItsMarkerStopsTheFuse() {
        UUID chargeId = UUID.randomUUID();
        EnderCrystal charge = this.crystal(chargeId, false);
        ExcavationChargeFuse fuse = this.fuse();

        assertTrue(fuse.armForPlayer(charge));

        this.bukkit.when(() -> Bukkit.getEntity(chargeId)).thenReturn(charge);
        this.tick.run();

        assertFalse(fuse.isBurning(charge));
        verify(this.scheduler).cancelTask(ExcavationChargeFuseTest.TASK_ID);
    }

    private ExcavationChargeFuse fuse() {
        when(this.plugin.namespace()).thenReturn("gamingbytez-enhancements");

        return new ExcavationChargeFuse(this.plugin, this.detonator, this.auditLog);
    }

    private EnderCrystal armedCharge(UUID chargeId) {
        return this.crystal(chargeId, true);
    }

    /**
     * Stands up a crystal the fuse resolves through its identity.
     *
     * @param chargeId The identity the crystal answers with
     * @param marked   Whether the crystal carries the placed Excavation Charge marker
     * @return The crystal a fuse can be armed on
     */
    private EnderCrystal crystal(UUID chargeId, boolean marked) {
        EnderCrystal charge = mock(EnderCrystal.class);
        PersistentDataContainer container = mock(PersistentDataContainer.class);

        when(container.getOrDefault(any(), any(), any()))
                .thenAnswer(invocation -> marked ? Boolean.TRUE : invocation.getArgument(2));

        when(charge.getUniqueId()).thenReturn(chargeId);
        when(charge.isValid()).thenReturn(true);
        when(charge.getWorld()).thenReturn(this.world);
        when(charge.getLocation()).thenReturn(new Location(this.world, 0.5, 64.0, 0.5));
        when(charge.getPersistentDataContainer()).thenReturn(container);

        return charge;
    }

    @Test
    void playerFuseRunsLongerThanTheChainFuse() {
        assertEquals(100L, ExcavationChargeFuse.PLAYER_FUSE_TICKS);
        assertEquals(10L, ExcavationChargeFuse.CHAIN_FUSE_TICKS);
    }

    @Test
    void outlineKeepsTheCornerOfTheVolume() {
        List<BlastVector> outline = ExcavationChargeFuse.outlineOffsets(
                new CuboidBlastGeometry(BlastLevel.LEVEL_1), ExcavationChargeFuseTest.GENEROUS_SAMPLE_CAP);

        assertTrue(outline.contains(new BlastVector(-4, -4, -4)));
    }

    @Test
    void outlineDropsOffsetsSurroundedByTheVolume() {
        List<BlastVector> outline = ExcavationChargeFuse.outlineOffsets(
                new CuboidBlastGeometry(BlastLevel.LEVEL_1), ExcavationChargeFuseTest.GENEROUS_SAMPLE_CAP);

        assertFalse(outline.contains(new BlastVector(0, -1, 0)));
    }

    @Test
    void outlineOnlyContainsOffsetsInsideTheVolume() {
        CuboidBlastGeometry geometry = new CuboidBlastGeometry(BlastLevel.LEVEL_1);

        List<BlastVector> outline =
                ExcavationChargeFuse.outlineOffsets(geometry, ExcavationChargeFuseTest.GENEROUS_SAMPLE_CAP);

        assertTrue(outline.stream().allMatch(geometry::contains));
    }

    @Test
    void outlineIsThinnedDownToTheSampleCap() {
        List<BlastVector> outline =
                ExcavationChargeFuse.outlineOffsets(new CuboidBlastGeometry(BlastLevel.LEVEL_4), 50);

        assertFalse(outline.isEmpty());
        assertTrue(outline.size() <= 50);
    }

    @Test
    void outlineIsEmptyWhenNoSampleIsAllowed() {
        List<BlastVector> outline =
                ExcavationChargeFuse.outlineOffsets(new CuboidBlastGeometry(BlastLevel.LEVEL_1), 0);

        assertTrue(outline.isEmpty());
    }
}
