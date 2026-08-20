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
package de.lazybytez.gamingbytezenhancements.lib.gameplay.item;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.withSettings;

/**
 * Covers {@link CustomItemDefinition}.
 * <p>
 * Neither a real {@link ItemStack} nor a real component type can be built in a unit test:
 * {@code DataComponentTypes} resolves every constant through the live server registry and
 * {@link ItemLore#lore(List)} through the server side component bridge. Both entry points are
 * therefore stubbed once, so the written components can be observed on a Mockito double.
 * <p>
 * The distinction that matters in game is absence versus a written default, which is why every
 * unset property is asserted as a component that was never written at all.
 */
@ExtendWith(MockitoExtension.class)
class CustomItemDefinitionTest {

    private static Registry<Keyed> registry;

    @Mock
    private ItemStack itemStack;

    private MockedStatic<ItemLore> loreFactory;

    private ItemLore loreComponent;

    /**
     * Resolves every {@code DataComponentTypes} constant against a stub registry that hands out one
     * distinct type per key, which is what lets a verification tell the lore component apart from
     * the name component. The deprecated class keyed lookup is answered as well because
     * {@code org.bukkit.Registry} still fills its legacy constants through it while initialising,
     * and a constant left null there breaks the classes that read it.
     */
    @BeforeAll
    @SuppressWarnings({"unchecked", "deprecation", "removal"})
    static void bindComponentTypesToAStubRegistry() {
        RegistryAccess registryAccess = mock(RegistryAccess.class);

        try (MockedStatic<RegistryAccess> access = mockStatic(RegistryAccess.class)) {
            access.when(RegistryAccess::registryAccess).thenReturn(registryAccess);
            lenient().when(registryAccess.getRegistry(any(RegistryKey.class)))
                    .thenAnswer(invocation -> stubRegistry());
            lenient().when(registryAccess.getRegistry(any(Class.class)))
                    .thenAnswer(invocation -> stubRegistry());

            assertEquals(DataComponentTypes.LORE, DataComponentTypes.LORE);
        }
    }

    /**
     * Builds the registry lazily, because mocking it eagerly loads {@code org.bukkit.Registry}
     * before the stub that its own static initialiser reaches for is in place. Every handed out type
     * answers to both component type shapes, since the pool being resolved holds valued and
     * unvalued types alike.
     */
    @SuppressWarnings("unchecked")
    private static Registry<Keyed> stubRegistry() {
        if (CustomItemDefinitionTest.registry != null) {
            return CustomItemDefinitionTest.registry;
        }

        Map<Key, DataComponentType> types = new HashMap<>();
        CustomItemDefinitionTest.registry = mock(Registry.class);
        lenient().when(CustomItemDefinitionTest.registry.getOrThrow(any(Key.class)))
                .thenAnswer(invocation -> types.computeIfAbsent(
                        invocation.getArgument(0),
                        key -> mock(DataComponentType.Valued.class, withSettings()
                                .extraInterfaces(DataComponentType.NonValued.class))
                ));

        return CustomItemDefinitionTest.registry;
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
    void applyTo_fullyPopulatedDefinition_writesEveryComponent() {
        Component name = Component.text("Safari Net", NamedTextColor.GOLD);
        List<Component> lore = List.of(
                Component.text("Holds a captured mob.", NamedTextColor.GRAY),
                Component.text("Empty", NamedTextColor.WHITE)
        );
        this.stubLoreFactory();

        CustomItemDefinition.builder()
                .name(name)
                .lore(lore)
                .enchantmentGlintOverride(true)
                .maxStackSize(1)
                .build()
                .applyTo(this.itemStack);

        verify(this.itemStack).setData(DataComponentTypes.CUSTOM_NAME, name);
        verify(this.itemStack).setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        verify(this.itemStack).setData(DataComponentTypes.MAX_STACK_SIZE, 1);
        verify(this.itemStack).setData(DataComponentTypes.LORE, this.loreComponent);
        assertEquals(lore, this.captureLoreLines());
    }

    @Test
    void applyTo_minimalDefinition_writesOnlyTheLoreComponent() {
        this.stubLoreFactory();

        CustomItemDefinition.builder()
                .lore(List.of(Component.text("A gem holding the life energy of centuries.")))
                .build()
                .applyTo(this.itemStack);

        verify(this.itemStack).setData(DataComponentTypes.LORE, this.loreComponent);
        verify(this.itemStack, never()).setData(eq(DataComponentTypes.CUSTOM_NAME), any(Component.class));
        verify(this.itemStack, never()).setData(eq(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE), any(Boolean.class));
        verify(this.itemStack, never()).setData(eq(DataComponentTypes.MAX_STACK_SIZE), any(Integer.class));
    }

    @Test
    void applyTo_emptyDefinition_writesNoComponentAtAll() {
        CustomItemDefinition.builder()
                .build()
                .applyTo(this.itemStack);

        verifyNoInteractions(this.itemStack);
    }

    @Test
    void applyTo_glintOverrideSetToFalse_writesTheFalseValue() {
        CustomItemDefinition.builder()
                .enchantmentGlintOverride(false)
                .build()
                .applyTo(this.itemStack);

        verify(this.itemStack).setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
    }

    @Test
    void applyTo_loreMutatedAfterBuild_writesTheLinesTheBuilderSaw() {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Holds a captured mob."));
        this.stubLoreFactory();

        CustomItemDefinition definition = CustomItemDefinition.builder()
                .lore(lore)
                .build();
        lore.add(Component.text("Added after the definition was built."));

        definition.applyTo(this.itemStack);

        assertEquals(1, this.captureLoreLines().size());
    }

    @Test
    void applyTo_nullItemStack_isRejected() {
        CustomItemDefinition definition = CustomItemDefinition.builder().build();

        assertThrows(NullPointerException.class, () -> definition.applyTo(null));
    }

    @Test
    void builder_nullName_isRejected() {
        assertThrows(NullPointerException.class, () -> CustomItemDefinition.builder().name(null));
    }

    @Test
    void builder_nullLore_isRejected() {
        assertThrows(NullPointerException.class, () -> CustomItemDefinition.builder().lore(null));
    }

    private void stubLoreFactory() {
        this.loreFactory.when(() -> ItemLore.lore(anyList())).thenReturn(this.loreComponent);
    }

    @SuppressWarnings("unchecked")
    private List<Component> captureLoreLines() {
        ArgumentCaptor<List<Component>> linesCaptor = ArgumentCaptor.forClass(List.class);
        this.loreFactory.verify(() -> ItemLore.lore(linesCaptor.capture()));

        return linesCaptor.getValue();
    }
}
