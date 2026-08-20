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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.excavationcharge.PlacedExcavationCharge;
import de.lazybytez.gamingbytezenhancements.lib.gameplay.blast.ActiveBlast;
import de.lazybytez.gamingbytezenhancements.lib.gameplay.blast.BlastScheduler;
import de.lazybytez.gamingbytezenhancements.lib.gameplay.blast.BlastVector;
import de.lazybytez.gamingbytezenhancements.lib.gameplay.blast.ChainCandidate;
import de.lazybytez.gamingbytezenhancements.lib.gameplay.blast.ChainSession;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

class ExcavationChargeDetonatorTest {

    private static final NamespacedKey PLACED_KEY = new NamespacedKey("gamingbytez", "excavation_charge_placed");
    private static final NamespacedKey SHAPE_KEY = new NamespacedKey("gamingbytez", "excavation_charge_shape");
    private static final NamespacedKey LEVEL_KEY = new NamespacedKey("gamingbytez", "excavation_charge_level");
    private static final NamespacedKey FACING_KEY = new NamespacedKey("gamingbytez", "excavation_charge_facing");

    private static final PlacedExcavationCharge.Keys KEYS = new PlacedExcavationCharge.Keys(
            ExcavationChargeDetonatorTest.PLACED_KEY,
            ExcavationChargeDetonatorTest.SHAPE_KEY,
            ExcavationChargeDetonatorTest.LEVEL_KEY,
            ExcavationChargeDetonatorTest.FACING_KEY);

    private static final int MIN_HEIGHT = -64;
    private static final int MAX_HEIGHT = 320;

    private MockedStatic<Bukkit> bukkit;
    private MockedStatic<RegistryAccess> registryAccess;
    private MockedStatic<DamageSource> damageSource;

    private World world;
    private PluginManager pluginManager;
    private BlastScheduler blastScheduler;
    private ExcavationChargeAuditLog auditLog;

    /**
     * Stands the server statics the detonator reaches through up.
     * <p>
     * The deprecated registry lookup is stubbed because {@link Registry}'s own initializer calls it
     * for its legacy constants and rejects a null result, which happens as soon as a registry is
     * mocked at all.
     */
    @SuppressWarnings({"deprecation", "removal"})
    @BeforeEach
    void setUp() {
        this.world = mock(World.class);
        this.pluginManager = mock(PluginManager.class);
        this.blastScheduler = mock(BlastScheduler.class);
        this.auditLog = mock(ExcavationChargeAuditLog.class);
        lenient().when(this.blastScheduler.canAccept(anyInt())).thenReturn(true);

        when(this.world.getMinHeight()).thenReturn(ExcavationChargeDetonatorTest.MIN_HEIGHT);
        when(this.world.getMaxHeight()).thenReturn(ExcavationChargeDetonatorTest.MAX_HEIGHT);
        when(this.world.isChunkLoaded(anyInt(), anyInt())).thenReturn(false);

        RegistryAccess access = mock(RegistryAccess.class);
        when(access.getRegistry(any(RegistryKey.class))).thenAnswer(invocation -> mock(Registry.class));
        when(access.getRegistry(any(Class.class))).thenAnswer(invocation -> mock(Registry.class));

        this.registryAccess = mockStatic(RegistryAccess.class);
        this.registryAccess.when(RegistryAccess::registryAccess).thenReturn(access);

        this.damageSource = mockStatic(DamageSource.class);
        DamageSource.Builder builder = mock(DamageSource.Builder.class);
        when(builder.withDamageLocation(any(Location.class))).thenReturn(builder);
        when(builder.build()).thenReturn(mock(DamageSource.class));
        this.damageSource.when(() -> DamageSource.builder(any())).thenReturn(builder);

        this.bukkit = mockStatic(Bukkit.class);
        this.bukkit.when(Bukkit::getPluginManager).thenReturn(this.pluginManager);
    }

    @AfterEach
    void tearDown() {
        this.bukkit.close();
        this.damageSource.close();
        this.registryAccess.close();
    }

    @Test
    void toCandidateTakesTheBlockCoordinatesOfTheCharge() {
        UUID id = UUID.randomUUID();

        ChainCandidate candidate =
                ExcavationChargeDetonator.toCandidate(id, new Location(null, 12.5, 64.9, -3.5));

        assertEquals(id, candidate.id());
        assertEquals(new BlastVector(12, 64, -4), candidate.position());
    }

    @Test
    void detonate_withCancelledExplosion_carvesNothingAndLeavesTheChargeStanding() {
        Block spared = mock(Block.class);
        EnderCrystal charge = this.placedCharge();
        this.onExplosion(event -> {
            event.blockList().add(spared);
            event.setCancelled(true);
        });

        List<EnderCrystal> woken = this.detonator().detonate(charge, new ChainSession(UUID.randomUUID()));

        assertTrue(woken.isEmpty());
        verify(this.blastScheduler, never()).submit(any(ActiveBlast.class));
        verify(charge, never()).remove();
        verify(this.world, never()).getNearbyLivingEntities(any(Location.class), anyDouble());
    }

    @Test
    void detonate_withAFullScheduler_abandonsTheBlastBeforeAnnouncingIt() {
        when(this.blastScheduler.canAccept(anyInt())).thenReturn(false);
        EnderCrystal charge = this.placedCharge();

        List<EnderCrystal> woken = this.detonator().detonate(charge, new ChainSession(UUID.randomUUID()));

        assertTrue(woken.isEmpty());
        verify(this.pluginManager, never()).callEvent(any(EntityExplodeEvent.class));
        verify(this.blastScheduler, never()).submit(any(ActiveBlast.class));
        verify(charge, never()).remove();
    }

    @Test
    void detonate_withListenerSparingABlock_carvesOnlyWhatTheListenerLeft() {
        Block spared = mock(Block.class);
        Block doomed = mock(Block.class);
        EnderCrystal charge = this.placedCharge();
        this.onExplosion(event -> {
            event.blockList().add(spared);
            event.blockList().add(doomed);
            event.blockList().remove(spared);
        });

        this.detonator().detonate(charge, new ChainSession(UUID.randomUUID()));

        ArgumentCaptor<ActiveBlast> submitted = ArgumentCaptor.forClass(ActiveBlast.class);
        verify(this.blastScheduler).submit(submitted.capture());

        ActiveBlast blast = submitted.getValue();
        assertEquals(1, blast.remainingBlocks());
        assertSame(doomed, blast.nextBlock());
    }

    @Test
    void detonate_withEntityCaughtInTheBlast_damagesItOncePerDetonation() {
        LivingEntity victim = mock(LivingEntity.class);
        when(victim.getLocation()).thenReturn(new Location(this.world, 0, 64, 0));
        when(victim.getVelocity()).thenReturn(new Vector(0.0, 0.0, 0.0));
        when(this.world.getNearbyLivingEntities(any(Location.class), anyDouble()))
                .thenReturn(List.of(victim));

        EnderCrystal charge = this.placedCharge();
        this.onExplosion(event -> {
            event.blockList().add(mock(Block.class));
            event.blockList().add(mock(Block.class));
            event.blockList().add(mock(Block.class));
        });

        this.detonator().detonate(charge, new ChainSession(UUID.randomUUID()));

        verify(victim, times(1)).damage(anyDouble(), any(DamageSource.class));
    }

    private ExcavationChargeDetonator detonator() {
        return new ExcavationChargeDetonator(
                this.blastScheduler, this.auditLog, ExcavationChargeDetonatorTest.KEYS);
    }

    private EnderCrystal placedCharge() {
        PersistentDataContainer container = mock(PersistentDataContainer.class);
        when(container.getOrDefault(
                ExcavationChargeDetonatorTest.PLACED_KEY, PersistentDataType.BOOLEAN, false))
                .thenReturn(true);
        when(container.get(ExcavationChargeDetonatorTest.LEVEL_KEY, PersistentDataType.INTEGER))
                .thenReturn(BlastLevel.MIN_LEVEL);

        EnderCrystal charge = mock(EnderCrystal.class);
        when(charge.getPersistentDataContainer()).thenReturn(container);
        when(charge.getLocation()).thenReturn(new Location(this.world, 0, 64, 0));
        when(charge.getUniqueId()).thenReturn(UUID.randomUUID());

        return charge;
    }

    private void onExplosion(Consumer<EntityExplodeEvent> listener) {
        doAnswer(invocation -> {
            listener.accept(invocation.getArgument(0));

            return null;
        }).when(this.pluginManager).callEvent(any(EntityExplodeEvent.class));
    }
}
