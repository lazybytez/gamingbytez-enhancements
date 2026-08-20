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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle;

import de.lazybytez.gamingbytezenhancements.lib.gameplay.item.ItemDataComponentStubs;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Covers the experience bookkeeping of {@link MagicXpBottleManager}.
 * <p>
 * A bottle a player already holds is refreshed rather than rebuilt, so the guard that matters is
 * that changing the stored experience rewrites the lore alone: the name, the glint and the single
 * item stack limit have to survive untouched, because a component the refresh silently dropped
 * would turn a bottle in an inventory into a differently presented item.
 */
@ExtendWith(MockitoExtension.class)
class MagicXpBottleManagerTest {

    @Mock
    private Plugin plugin;

    @Mock
    private ItemStack magicXpBottle;

    @Mock
    private PersistentDataContainerView view;

    @Mock
    private PersistentDataContainer mutableContainer;

    private MockedStatic<ItemLore> loreFactory;

    private ItemLore loreComponent;

    @BeforeAll
    static void bindComponentTypes() {
        ItemDataComponentStubs.bindComponentTypes();
    }

    @BeforeEach
    void openLoreFactory() {
        this.loreFactory = mockStatic(ItemLore.class);
        this.loreComponent = mock(ItemLore.class);
    }

    @AfterEach
    void closeLoreFactory() {
        this.loreFactory.close();
    }

    @Test
    void addExperience_writesTheSummedValueAndRefreshesTheLoreOnly() {
        MagicXpBottleManager manager = this.createManager();
        this.stubStoredExperience(120);
        ItemDataComponentStubs.stubLoreFactory(this.loreFactory, this.loreComponent);

        manager.addExperience(this.magicXpBottle, 30);

        ArgumentCaptor<NamespacedKey> keyCaptor = ArgumentCaptor.forClass(NamespacedKey.class);
        verify(this.mutableContainer).set(keyCaptor.capture(), eq(PersistentDataType.INTEGER), eq(150));
        assertEquals(MagicXpBottleManager.PDC_KEY_EXPERIENCE, keyCaptor.getValue().getKey());
        assertTrue(this.renderedLore().contains("Current Experience: 150"));
        this.verifyOnlyTheLoreWasWritten();
    }

    @Test
    void removeExperience_floorsAtZeroAndRefreshesTheLoreOnly() {
        MagicXpBottleManager manager = this.createManager();
        this.stubStoredExperience(10);
        ItemDataComponentStubs.stubLoreFactory(this.loreFactory, this.loreComponent);

        manager.removeExperience(this.magicXpBottle, 450);

        verify(this.mutableContainer).set(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(0));
        assertTrue(this.renderedLore().contains("Current Experience: 0"));
        this.verifyOnlyTheLoreWasWritten();
    }

    @Test
    void createItemDefinition_namesTheBottleAndCapsItToASingleStack() {
        MagicXpBottleManager manager = new MagicXpBottleManager(this.plugin);
        ItemDataComponentStubs.stubLoreFactory(this.loreFactory, this.loreComponent);
        ItemStack freshBottle = mock(ItemStack.class);

        manager.createItemDefinition().applyTo(freshBottle);

        ArgumentCaptor<Component> nameCaptor = ArgumentCaptor.forClass(Component.class);
        verify(freshBottle).setData(eq(DataComponentTypes.CUSTOM_NAME), nameCaptor.capture());
        assertEquals("Magic XP Bottle", ((TextComponent) nameCaptor.getValue()).content());
        verify(freshBottle).setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        verify(freshBottle).setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        assertTrue(this.renderedLore().contains("Current Experience: 0"));
    }

    private MagicXpBottleManager createManager() {
        when(this.plugin.namespace()).thenReturn("gamingbytez-enhancements");

        return new MagicXpBottleManager(this.plugin);
    }

    private void stubStoredExperience(int experience) {
        when(this.magicXpBottle.getPersistentDataContainer()).thenReturn(this.view);
        when(this.view.getOrDefault(any(NamespacedKey.class), eq(PersistentDataType.INTEGER), eq(0)))
                .thenReturn(experience);
        when(this.magicXpBottle.editPersistentDataContainer(any())).thenAnswer(invocation -> {
            Consumer<PersistentDataContainer> consumer = invocation.getArgument(0);
            consumer.accept(this.mutableContainer);

            return true;
        });
    }

    private void verifyOnlyTheLoreWasWritten() {
        verify(this.magicXpBottle).setData(DataComponentTypes.LORE, this.loreComponent);
        verify(this.magicXpBottle, never()).setData(eq(DataComponentTypes.CUSTOM_NAME), any(Component.class));
        verify(this.magicXpBottle, never()).setData(eq(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE), any(Boolean.class));
        verify(this.magicXpBottle, never()).setData(eq(DataComponentTypes.MAX_STACK_SIZE), any(Integer.class));
    }

    private String renderedLore() {
        List<Component> lines = ItemDataComponentStubs.captureLoreLines(this.loreFactory);
        StringBuilder rendered = new StringBuilder();

        for (Component line : lines) {
            rendered.append(MagicXpBottleManagerTest.flatten(line)).append('\n');
        }

        return rendered.toString();
    }

    private static String flatten(Component component) {
        StringBuilder builder = new StringBuilder();

        if (component instanceof TextComponent textComponent) {
            builder.append(textComponent.content());
        }

        for (Component child : component.children()) {
            builder.append(MagicXpBottleManagerTest.flatten(child));
        }

        return builder.toString();
    }
}
