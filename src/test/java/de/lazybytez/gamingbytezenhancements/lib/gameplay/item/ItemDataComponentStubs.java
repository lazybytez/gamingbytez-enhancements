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
import org.bukkit.Keyed;
import org.bukkit.Registry;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.withSettings;

/**
 * The two server side entry points a {@link CustomItemDefinition} writes through, answered without
 * a running server.
 * <p>
 * {@code DataComponentTypes} resolves every constant through the live registry and
 * {@link ItemLore#lore(List)} through the server side component bridge, so any code path that
 * applies a definition reaches both. Every class that exercises such a path shares this stubbing,
 * because the registry binding has to happen before {@code DataComponentTypes} initialises and that
 * moment is not owned by a single test class.
 */
public final class ItemDataComponentStubs {

    private static Registry<Keyed> registry;

    private ItemDataComponentStubs() {
    }

    /**
     * Resolves every {@code DataComponentTypes} constant against a stub registry that hands out one
     * distinct type per key, which is what lets a verification tell the lore component apart from
     * the name component. The deprecated class keyed lookup is answered as well because
     * {@code org.bukkit.Registry} still fills its legacy constants through it while initialising,
     * and a constant left null there breaks the classes that read it.
     */
    @SuppressWarnings({"unchecked", "deprecation", "removal"})
    public static void bindComponentTypes() {
        RegistryAccess registryAccess = mock(RegistryAccess.class);

        try (MockedStatic<RegistryAccess> access = mockStatic(RegistryAccess.class)) {
            access.when(RegistryAccess::registryAccess).thenReturn(registryAccess);
            lenient().when(registryAccess.getRegistry(any(RegistryKey.class)))
                    .thenAnswer(invocation -> ItemDataComponentStubs.stubRegistry());
            lenient().when(registryAccess.getRegistry(any(Class.class)))
                    .thenAnswer(invocation -> ItemDataComponentStubs.stubRegistry());

            assertEquals(DataComponentTypes.LORE, DataComponentTypes.LORE);
        }
    }

    /**
     * Answers the lore factory with the given double, so the written lore component is one a
     * verification can recognise.
     *
     * @param loreFactory   the open static mock of {@link ItemLore}
     * @param loreComponent the component the factory hands back
     */
    public static void stubLoreFactory(MockedStatic<ItemLore> loreFactory, ItemLore loreComponent) {
        loreFactory.when(() -> ItemLore.lore(anyList())).thenReturn(loreComponent);
    }

    /**
     * Reads back the lines the factory was called with, which is the only place the lore text is
     * still visible once it has been folded into a component.
     *
     * @param loreFactory the open static mock of {@link ItemLore}
     * @return the captured lore lines
     */
    @SuppressWarnings("unchecked")
    public static List<Component> captureLoreLines(MockedStatic<ItemLore> loreFactory) {
        ArgumentCaptor<List<Component>> linesCaptor = ArgumentCaptor.forClass(List.class);
        loreFactory.verify(() -> ItemLore.lore(linesCaptor.capture()));

        return linesCaptor.getValue();
    }

    /**
     * Builds the registry lazily, because mocking it eagerly loads {@code org.bukkit.Registry}
     * before the stub that its own static initialiser reaches for is in place. Every handed out type
     * answers to both component type shapes, since the pool being resolved holds valued and
     * unvalued types alike.
     */
    @SuppressWarnings("unchecked")
    private static Registry<Keyed> stubRegistry() {
        if (ItemDataComponentStubs.registry != null) {
            return ItemDataComponentStubs.registry;
        }

        Map<Key, DataComponentType> types = new HashMap<>();
        ItemDataComponentStubs.registry = mock(Registry.class);
        lenient().when(ItemDataComponentStubs.registry.getOrThrow(any(Key.class)))
                .thenAnswer(invocation -> types.computeIfAbsent(
                        invocation.getArgument(0),
                        key -> mock(DataComponentType.Valued.class, withSettings()
                                .extraInterfaces(DataComponentType.NonValued.class))
                ));

        return ItemDataComponentStubs.registry;
    }
}
