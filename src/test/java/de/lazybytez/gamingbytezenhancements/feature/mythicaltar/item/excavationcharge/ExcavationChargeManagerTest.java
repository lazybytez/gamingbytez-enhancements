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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.excavationcharge;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastLevel;
import de.lazybytez.gamingbytezenhancements.lib.gameplay.blast.BlastShape;
import de.lazybytez.gamingbytezenhancements.lib.gameplay.item.ItemDataComponentStubs;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePalette;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Covers {@link ExcavationChargeManager}.
 * <p>
 * A real {@link ItemStack} cannot be constructed in a unit test: {@code ItemStack.of(Material)}
 * resolves through the live server registry, and so do the data components a definition writes.
 * The persistent data container round trip is
 * therefore exercised against Mockito doubles, and the shape/level encode-decode rules that
 * govern the stored primitives are covered exhaustively through the package-private static
 * helpers, including the missing-key defaults an older item on disk would trigger.
 */
@ExtendWith(MockitoExtension.class)
class ExcavationChargeManagerTest {

    @Mock
    private Plugin plugin;

    @Mock
    private ItemStack excavationCharge;

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
    void setLevel_refreshesTheLoreAndLeavesEveryOtherComponentAlone() {
        this.stubNamespace();
        ExcavationChargeManager manager = new ExcavationChargeManager(this.plugin);

        this.stubEditingPdc();
        when(this.excavationCharge.getPersistentDataContainer()).thenReturn(this.view);
        when(this.view.get(any(NamespacedKey.class), eq(PersistentDataType.STRING))).thenReturn(null);
        ItemDataComponentStubs.stubLoreFactory(this.loreFactory, this.loreComponent);

        manager.setLevel(this.excavationCharge, 2);

        verify(this.excavationCharge).setData(DataComponentTypes.LORE, this.loreComponent);
        verify(this.excavationCharge, never()).setData(eq(DataComponentTypes.CUSTOM_NAME), any(Component.class));
        verify(this.excavationCharge, never()).setData(eq(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE), any(Boolean.class));
        verify(this.excavationCharge, never()).setData(eq(DataComponentTypes.MAX_STACK_SIZE), any(Integer.class));

        String rendered = ItemDataComponentStubs.captureLoreLines(this.loreFactory).stream()
                .map(ExcavationChargeManagerTest::flatten)
                .reduce("", (left, right) -> left + right + "\n");
        assertTrue(rendered.contains("Level: 2"));
    }

    private void stubNamespace() {
        when(this.plugin.namespace()).thenReturn("gamingbytez-enhancements");
    }

    @Test
    void encodeShape_returnsEnumName() {
        for (BlastShape shape : BlastShape.values()) {
            assertEquals(shape.name(), ExcavationChargeManager.encodeShape(shape));
        }
    }

    @Test
    void decodeLevel_missingValue_defaultsToLevelOne() {
        assertEquals(1, ExcavationChargeManager.decodeLevel(null));
    }

    @Test
    void decodeLevel_belowMinimum_clampsToLevelOne() {
        assertEquals(BlastLevel.MIN_LEVEL, ExcavationChargeManager.decodeLevel(0));
        assertEquals(BlastLevel.MIN_LEVEL, ExcavationChargeManager.decodeLevel(-5));
    }

    @Test
    void decodeLevel_aboveMaximum_clampsToLevelFour() {
        assertEquals(BlastLevel.MAX_LEVEL, ExcavationChargeManager.decodeLevel(9));
    }

    @Test
    void decodeLevel_validValue_returnsSameValue() {
        for (int level = BlastLevel.MIN_LEVEL; level <= BlastLevel.MAX_LEVEL; level++) {
            assertEquals(level, ExcavationChargeManager.decodeLevel(level));
        }
    }

    @Test
    void encodeLevel_belowMinimum_clampsToLevelOne() {
        assertEquals(BlastLevel.MIN_LEVEL, ExcavationChargeManager.encodeLevel(0));
    }

    @Test
    void encodeLevel_aboveMaximum_clampsToLevelFour() {
        assertEquals(BlastLevel.MAX_LEVEL, ExcavationChargeManager.encodeLevel(99));
    }

    @Test
    void encodeLevel_validValue_returnsSameValue() {
        for (int level = BlastLevel.MIN_LEVEL; level <= BlastLevel.MAX_LEVEL; level++) {
            assertEquals(level, ExcavationChargeManager.encodeLevel(level));
        }
    }

    @Test
    void getShape_missingKey_readsBackAsCuboid() {
        this.stubNamespace();
        ExcavationChargeManager manager = new ExcavationChargeManager(this.plugin);
        when(this.excavationCharge.getPersistentDataContainer()).thenReturn(this.view);
        when(this.view.get(any(NamespacedKey.class), eq(PersistentDataType.STRING))).thenReturn(null);

        assertEquals(BlastShape.CUBOID, manager.getShape(this.excavationCharge));
    }

    @Test
    void getShape_storedValue_decodesToMatchingShape() {
        this.stubNamespace();
        ExcavationChargeManager manager = new ExcavationChargeManager(this.plugin);
        when(this.excavationCharge.getPersistentDataContainer()).thenReturn(this.view);
        when(this.view.get(any(NamespacedKey.class), eq(PersistentDataType.STRING))).thenReturn("SPHERE");

        assertEquals(BlastShape.SPHERE, manager.getShape(this.excavationCharge));
    }

    @Test
    void getLevel_missingKey_readsBackAsLevelOne() {
        this.stubNamespace();
        ExcavationChargeManager manager = new ExcavationChargeManager(this.plugin);
        when(this.excavationCharge.getPersistentDataContainer()).thenReturn(this.view);
        when(this.view.get(any(NamespacedKey.class), eq(PersistentDataType.INTEGER))).thenReturn(null);

        assertEquals(1, manager.getLevel(this.excavationCharge));
    }

    @Test
    void getLevel_storedValue_decodesToMatchingLevel() {
        this.stubNamespace();
        ExcavationChargeManager manager = new ExcavationChargeManager(this.plugin);
        when(this.excavationCharge.getPersistentDataContainer()).thenReturn(this.view);
        when(this.view.get(any(NamespacedKey.class), eq(PersistentDataType.INTEGER))).thenReturn(3);

        assertEquals(3, manager.getLevel(this.excavationCharge));
    }

    @Test
    void setShape_writesEncodedShapeUnderTheShapeKey() {
        this.stubNamespace();
        ExcavationChargeManager manager = new ExcavationChargeManager(this.plugin);

        this.stubEditingPdc();
        when(this.excavationCharge.getPersistentDataContainer()).thenReturn(this.view);
        when(this.view.get(any(NamespacedKey.class), eq(PersistentDataType.INTEGER))).thenReturn(null);
        ItemDataComponentStubs.stubLoreFactory(this.loreFactory, this.loreComponent);

        manager.setShape(this.excavationCharge, BlastShape.SPHERE);

        ArgumentCaptor<NamespacedKey> keyCaptor = ArgumentCaptor.forClass(NamespacedKey.class);
        verify(this.mutableContainer).set(keyCaptor.capture(), eq(PersistentDataType.STRING), eq("SPHERE"));
        assertEquals("gamingbytez-excavation-charge-shape", keyCaptor.getValue().getKey());
        verify(this.excavationCharge).setData(DataComponentTypes.LORE, this.loreComponent);
    }

    @Test
    void setLevel_clampsAndWritesLevelUnderTheLevelKey() {
        this.stubNamespace();
        ExcavationChargeManager manager = new ExcavationChargeManager(this.plugin);

        this.stubEditingPdc();
        when(this.excavationCharge.getPersistentDataContainer()).thenReturn(this.view);
        when(this.view.get(any(NamespacedKey.class), eq(PersistentDataType.STRING))).thenReturn(null);
        ItemDataComponentStubs.stubLoreFactory(this.loreFactory, this.loreComponent);

        manager.setLevel(this.excavationCharge, 99);

        ArgumentCaptor<NamespacedKey> keyCaptor = ArgumentCaptor.forClass(NamespacedKey.class);
        verify(this.mutableContainer).set(keyCaptor.capture(), eq(PersistentDataType.INTEGER), eq(BlastLevel.MAX_LEVEL));
        assertEquals("gamingbytez-excavation-charge-level", keyCaptor.getValue().getKey());
    }

    private void stubEditingPdc() {
        when(this.excavationCharge.editPersistentDataContainer(any())).thenAnswer(invocation -> {
            Consumer<PersistentDataContainer> consumer = invocation.getArgument(0);
            consumer.accept(this.mutableContainer);
            return true;
        });
    }

    @Test
    void isCustomItem_trueForLevelThreeSphereCharge() {
        this.stubNamespace();
        ExcavationChargeManager manager = new ExcavationChargeManager(this.plugin);
        when(this.excavationCharge.getType()).thenReturn(Material.END_CRYSTAL);
        when(this.excavationCharge.getPersistentDataContainer()).thenReturn(this.view);
        when(this.view.getOrDefault(any(NamespacedKey.class), eq(PersistentDataType.BOOLEAN), eq(false)))
                .thenReturn(true);

        assertTrue(manager.isCustomItem(this.excavationCharge));
    }

    @Test
    void isCustomItem_falseForPlainEndCrystal() {
        this.stubNamespace();
        ExcavationChargeManager manager = new ExcavationChargeManager(this.plugin);
        when(this.excavationCharge.getType()).thenReturn(Material.END_CRYSTAL);
        when(this.excavationCharge.getPersistentDataContainer()).thenReturn(this.view);
        when(this.view.getOrDefault(any(NamespacedKey.class), eq(PersistentDataType.BOOLEAN), eq(false)))
                .thenReturn(false);

        assertFalse(manager.isCustomItem(this.excavationCharge));
    }

    @Test
    void isCustomItem_falseForDifferentMaterial() {
        ExcavationChargeManager manager = new ExcavationChargeManager(this.plugin);
        when(this.excavationCharge.getType()).thenReturn(Material.STONE);

        assertFalse(manager.isCustomItem(this.excavationCharge));
    }

    @Test
    void createItemDefinition_keepsDisplayNameIdentityColour_andListsBlastLevelOneStatsInLore() {
        ExcavationChargeManager manager = new ExcavationChargeManager(this.plugin);
        ItemDataComponentStubs.stubLoreFactory(this.loreFactory, this.loreComponent);

        manager.createItemDefinition().applyTo(this.excavationCharge);

        ArgumentCaptor<Component> nameCaptor = ArgumentCaptor.forClass(Component.class);
        verify(this.excavationCharge).setData(eq(DataComponentTypes.CUSTOM_NAME), nameCaptor.capture());
        assertTrue(nameCaptor.getValue().hasDecoration(TextDecoration.BOLD));

        List<Component> lore = ItemDataComponentStubs.captureLoreLines(this.loreFactory);

        String rendered = lore.stream()
                .map(ExcavationChargeManagerTest::flatten)
                .reduce("", (left, right) -> left + right + "\n");

        assertTrue(rendered.contains("Cuboid"));
        assertTrue(rendered.contains(String.valueOf(BlastLevel.of(1).getSize())));
        assertTrue(rendered.contains(String.valueOf(BlastLevel.of(1).getCentreDamage())));
        assertTrue(rendered.contains("Chains to every charge inside the blast."));

        boolean hasBodyToken = lore.stream().anyMatch(line -> line.color() == MessagePalette.BODY);
        boolean hasValueToken = lore.stream()
                .flatMap(line -> line.children().stream())
                .anyMatch(child -> child.color() == MessagePalette.VALUE);
        assertTrue(hasBodyToken);
        assertTrue(hasValueToken);
    }

    private static String flatten(Component component) {
        StringBuilder builder = new StringBuilder();
        if (component instanceof TextComponent textComponent) {
            builder.append(textComponent.content());
        }
        for (Component child : component.children()) {
            builder.append(flatten(child));
        }
        return builder.toString();
    }
}
