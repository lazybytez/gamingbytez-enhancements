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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.excavationcharge;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastLevel;
import de.lazybytez.gamingbytezenhancements.lib.gameplay.blast.BlastShape;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.CustomItemManagerRegistry;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.excavationcharge.ExcavationChargeManager;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Covers {@link UpgradeExcavationChargeRecipe}.
 */
@ExtendWith(MockitoExtension.class)
class UpgradeExcavationChargeRecipeTest {

    @Mock
    private MythicAltarFeature mythicAltarFeature;

    @Mock
    private CustomItemManagerRegistry customItemManagerRegistry;

    @Mock
    private ExcavationChargeManager excavationChargeManager;

    @Mock
    private AltarInterface altar;

    @Mock
    private Plugin plugin;

    @Mock
    private PlayerItemFrameChangeEvent event;

    @Mock
    private Runnable removeLock;

    private void stubRegistry() {
        when(this.mythicAltarFeature.getCustomItemManagerRegistry()).thenReturn(this.customItemManagerRegistry);
        when(this.customItemManagerRegistry.getCustomItemManager(ExcavationChargeManager.class))
                .thenReturn(this.excavationChargeManager);
    }

    private ItemFrame pedestalWith(Material material) {
        ItemStack item = mock(ItemStack.class);
        when(item.getType()).thenReturn(material);

        ItemFrame frame = mock(ItemFrame.class);
        when(frame.getItem()).thenReturn(item);

        return frame;
    }

    private ItemFrame pedestalWithItem(ItemStack item) {
        ItemFrame frame = mock(ItemFrame.class);
        when(frame.getItem()).thenReturn(item);

        return frame;
    }

    @Test
    void validateAltarState_returnsFalse_whenCenterIsPlainEndCrystal() {
        this.stubRegistry();

        ItemStack centerItem = mock(ItemStack.class);
        when(this.excavationChargeManager.isCustomItem(centerItem)).thenReturn(false);
        ItemFrame centerPedestal = this.pedestalWithItem(centerItem);
        when(this.altar.getPedestal(PedestalLocation.CENTER)).thenReturn(centerPedestal);

        UpgradeExcavationChargeRecipe recipe = new UpgradeExcavationChargeRecipe(this.mythicAltarFeature);

        assertFalse(recipe.validateAltarState(this.altar));
    }

    @Test
    void validateAltarState_returnsFalse_whenCenterChargeAtMaxLevel() {
        this.stubRegistry();

        ItemStack centerItem = mock(ItemStack.class);
        when(this.excavationChargeManager.isCustomItem(centerItem)).thenReturn(true);
        when(this.excavationChargeManager.getLevel(centerItem)).thenReturn(BlastLevel.MAX_LEVEL);
        ItemFrame centerPedestal = this.pedestalWithItem(centerItem);
        when(this.altar.getPedestal(PedestalLocation.CENTER)).thenReturn(centerPedestal);

        UpgradeExcavationChargeRecipe recipe = new UpgradeExcavationChargeRecipe(this.mythicAltarFeature);

        assertFalse(recipe.validateAltarState(this.altar));
    }

    @Test
    void validateAltarState_returnsTrue_whenOuterIngredientsPresentInAnyOrder() {
        this.stubRegistry();

        ItemStack centerItem = mock(ItemStack.class);
        when(this.excavationChargeManager.isCustomItem(centerItem)).thenReturn(true);
        when(this.excavationChargeManager.getLevel(centerItem)).thenReturn(2);
        ItemFrame centerPedestal = this.pedestalWithItem(centerItem);
        when(this.altar.getPedestal(PedestalLocation.CENTER)).thenReturn(centerPedestal);

        // Deliberately shuffled relative to declaration order to prove position independence.
        ItemFrame northWest = this.pedestalWith(Material.DIAMOND_BLOCK);
        ItemFrame southWest = this.pedestalWith(Material.TNT);
        ItemFrame northEast = this.pedestalWith(Material.ECHO_SHARD);
        ItemFrame southEast = this.pedestalWith(Material.OBSIDIAN);
        when(this.altar.getPedestal(PedestalLocation.NORTH_WEST)).thenReturn(northWest);
        when(this.altar.getPedestal(PedestalLocation.SOUTH_WEST)).thenReturn(southWest);
        when(this.altar.getPedestal(PedestalLocation.NORTH_EAST)).thenReturn(northEast);
        when(this.altar.getPedestal(PedestalLocation.SOUTH_EAST)).thenReturn(southEast);

        UpgradeExcavationChargeRecipe recipe = new UpgradeExcavationChargeRecipe(this.mythicAltarFeature);

        assertTrue(recipe.validateAltarState(this.altar));
    }

    @Test
    void onRecipeComplete_preservesInputChargeShape_inOutput() {
        this.stubRegistry();

        ItemStack centerItem = mock(ItemStack.class);
        ItemStack upgradedCharge = mock(ItemStack.class);
        when(this.excavationChargeManager.getShape(centerItem)).thenReturn(BlastShape.TUNNEL);
        when(this.excavationChargeManager.getLevel(centerItem)).thenReturn(2);
        when(this.excavationChargeManager.createCustomItem()).thenReturn(upgradedCharge);

        ItemFrame centerPedestal = this.pedestalWithItem(centerItem);
        when(this.altar.getPedestal(PedestalLocation.CENTER)).thenReturn(centerPedestal);

        World world = mock(World.class);
        Location centerLocation = new Location(world, 0, 64, 0);
        when(this.altar.getLocation()).thenReturn(centerLocation);
        when(centerPedestal.getLocation()).thenReturn(centerLocation);

        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            BukkitScheduler scheduler = mock(BukkitScheduler.class);
            bukkit.when(Bukkit::getScheduler).thenReturn(scheduler);
            when(scheduler.scheduleSyncDelayedTask(eq(this.plugin), any(Runnable.class), anyLong()))
                    .thenAnswer(invocation -> {
                        // Run every scheduled step, so the test does not depend on which delay the
                        // chosen particle effect happens to hand the recipe callback over on.
                        Runnable task = invocation.getArgument(1);
                        task.run();

                        return 0;
                    });

            UpgradeExcavationChargeRecipe recipe = new UpgradeExcavationChargeRecipe(this.mythicAltarFeature);

            recipe.onRecipeComplete(this.plugin, this.altar, this.event, this.removeLock);
        }

        verify(this.excavationChargeManager).setShape(upgradedCharge, BlastShape.TUNNEL);
        verify(this.excavationChargeManager).setLevel(upgradedCharge, 3);
        verify(world).dropItem(any(Location.class), eq(upgradedCharge));
        verify(this.removeLock).run();
    }

    @Test
    void getRecipe_returnsEmptyMap() {
        UpgradeExcavationChargeRecipe recipe = new UpgradeExcavationChargeRecipe(this.mythicAltarFeature);

        assertTrue(recipe.getRecipe().equals(Map.of()));
    }
}
