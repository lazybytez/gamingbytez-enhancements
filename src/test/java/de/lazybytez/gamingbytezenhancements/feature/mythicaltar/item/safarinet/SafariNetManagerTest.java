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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.safarinet;

import de.lazybytez.gamingbytezenhancements.lib.gameplay.item.ItemDataComponentStubs;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
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
 * Covers the display refresh of {@link SafariNetManager}.
 * <p>
 * Emptying a net rewrites the name and the lore of a net a player already holds, so the guard that
 * matters is that the glint and the single item stack limit are left alone: a refresh that dropped
 * them would hand back a net that stacks and no longer shines.
 */
@ExtendWith(MockitoExtension.class)
class SafariNetManagerTest {

    @Mock
    private Plugin plugin;

    @Mock
    private ItemStack safariNet;

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
    void clearEntity_restoresTheEmptyPresentationWithoutTouchingGlintOrStackSize() {
        when(this.plugin.namespace()).thenReturn("gamingbytez-enhancements");
        SafariNetManager manager = new SafariNetManager(this.plugin);
        when(this.safariNet.editPersistentDataContainer(any())).thenAnswer(invocation -> {
            Consumer<PersistentDataContainer> consumer = invocation.getArgument(0);
            consumer.accept(this.mutableContainer);

            return true;
        });
        ItemDataComponentStubs.stubLoreFactory(this.loreFactory, this.loreComponent);

        manager.clearEntity(this.safariNet);

        ArgumentCaptor<Component> nameCaptor = ArgumentCaptor.forClass(Component.class);
        verify(this.safariNet).setData(eq(DataComponentTypes.CUSTOM_NAME), nameCaptor.capture());
        assertEquals("Safari Net", ((TextComponent) nameCaptor.getValue()).content());
        verify(this.safariNet).setData(DataComponentTypes.LORE, this.loreComponent);
        verify(this.safariNet, never()).setData(eq(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE), any(Boolean.class));
        verify(this.safariNet, never()).setData(eq(DataComponentTypes.MAX_STACK_SIZE), any(Integer.class));

        List<Component> lore = ItemDataComponentStubs.captureLoreLines(this.loreFactory);
        assertTrue(lore.stream()
                .anyMatch(line -> line instanceof TextComponent text
                        && text.content().equals("A mystical net that can capture creatures.")));
    }
}
