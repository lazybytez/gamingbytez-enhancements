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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.MythicAltar;
import de.lazybytez.gamingbytezenhancements.lib.gameplay.blast.BlastScheduler;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.excavationcharge.CollectExcavationChargeListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.excavationcharge.CycleExcavationChargeShapeListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.excavationcharge.DetonateExcavationChargeListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.excavationcharge.PlaceExcavationChargeListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.excavationcharge.ExcavationChargeManager;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.CompletableRecipeInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.excavationcharge.CraftExcavationChargeRecipe;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.excavationcharge.UpgradeExcavationChargeRecipe;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MythicAltarFeatureTest {
    private static final Component EXPECTED_PREFIX = Component.text("[", NamedTextColor.DARK_GRAY)
            .append(Component.text("MythicAltar", NamedTextColor.GOLD))
            .append(Component.text("] ", NamedTextColor.DARK_GRAY));

    @Mock
    private EnhancementsPlugin plugin;

    @Mock
    private Server server;

    @Mock
    private PluginManager pluginManager;

    @Test
    void getMessenger_rendersThePrefixOfTheFeature() {
        MythicAltarFeature feature = new MythicAltarFeature(this.plugin);

        Component body = Component.text("Body");

        assertEquals(EXPECTED_PREFIX.append(body), feature.getMessenger().prefixed(body));
    }

    @Test
    void getMessenger_returnsTheSameInstanceOnEveryCall() {
        MythicAltarFeature feature = new MythicAltarFeature(this.plugin);

        assertNotNull(feature.getMessenger());
        assertEquals(feature.getMessenger(), feature.getMessenger());
    }

    @Test
    void getDefaultRecipes_includesExcavationChargeRecipes() {
        MythicAltarFeature feature = new MythicAltarFeature(this.plugin);

        List<CompletableRecipeInterface> recipes = MythicAltar.getDefaultRecipes(feature);

        assertTrue(recipes.stream().anyMatch(CraftExcavationChargeRecipe.class::isInstance));
        assertTrue(recipes.stream().anyMatch(UpgradeExcavationChargeRecipe.class::isInstance));
    }

    @Test
    void onEnable_registersExcavationChargeManager() {
        this.stubPluginManager();

        MythicAltarFeature feature = new MythicAltarFeature(this.plugin);
        feature.onEnable();

        assertInstanceOf(
                ExcavationChargeManager.class,
                feature.getCustomItemManagerRegistry().getCustomItemManager(ExcavationChargeManager.class));
    }

    @Test
    void onEnable_registersAllFourExcavationChargeListeners() {
        this.stubPluginManager();
        ArgumentCaptor<Listener> listenerCaptor = ArgumentCaptor.forClass(Listener.class);

        MythicAltarFeature feature = new MythicAltarFeature(this.plugin);
        feature.onEnable();

        verify(this.pluginManager, atLeastOnce()).registerEvents(listenerCaptor.capture(), eq(this.plugin));
        List<Listener> listeners = listenerCaptor.getAllValues();

        assertTrue(listeners.stream().anyMatch(CycleExcavationChargeShapeListener.class::isInstance));
        assertTrue(listeners.stream().anyMatch(PlaceExcavationChargeListener.class::isInstance));
        assertTrue(listeners.stream().anyMatch(CollectExcavationChargeListener.class::isInstance));
        assertTrue(listeners.stream().anyMatch(DetonateExcavationChargeListener.class::isInstance));
    }

    @Test
    void onDisable_shutsDownTheBlastScheduler() {
        this.stubPluginManager();

        try (MockedConstruction<BlastScheduler> mockedScheduler = mockConstruction(BlastScheduler.class)) {
            MythicAltarFeature feature = new MythicAltarFeature(this.plugin);
            feature.onEnable();
            feature.onDisable();

            assertEquals(1, mockedScheduler.constructed().size());
            verify(mockedScheduler.constructed().get(0)).shutdown();
        }
    }

    @Test
    void onDisable_beforeOnEnable_doesNotThrow() {
        MythicAltarFeature feature = new MythicAltarFeature(this.plugin);

        assertDoesNotThrow(feature::onDisable);
    }

    private void stubPluginManager() {
        when(this.plugin.getServer()).thenReturn(this.server);
        when(this.server.getPluginManager()).thenReturn(this.pluginManager);
        when(this.server.getScheduler()).thenReturn(mock(org.bukkit.scheduler.BukkitScheduler.class));
        when(this.plugin.namespace()).thenReturn("gamingbytez-enhancements");
    }
}
