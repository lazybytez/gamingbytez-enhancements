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

import de.lazybytez.gamingbytezenhancements.lib.message.MessagePalette;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * Pins the display name identity colour and the lore body colour of {@link EssenceOfSpawnerManager}.
 * <p>
 * Representative of the identical pattern in {@code ExperienceGemManager}: a bold identity-coloured
 * display name that stays untouched, plus a single grey lore line that moves onto
 * {@link MessagePalette#BODY}. The guard is a regression pin against a future accidental restyle of
 * a player-visible item, not a behaviour change: {@code MessagePalette.BODY} resolves to the same
 * colour the item already used.
 */
@ExtendWith(MockitoExtension.class)
class EssenceOfSpawnerManagerTest {

    @Mock
    private Plugin plugin;

    @Mock
    private ItemMeta itemMeta;

    @Test
    void configureItemMeta_keepsDisplayNameIdentityColour_andPutsLoreOnPalette() {
        EssenceOfSpawnerManager manager = new EssenceOfSpawnerManager(this.plugin);

        manager.configureItemMeta(this.itemMeta);

        ArgumentCaptor<Component> nameCaptor = ArgumentCaptor.forClass(Component.class);
        verify(this.itemMeta).customName(nameCaptor.capture());
        assertEquals(NamedTextColor.GOLD, nameCaptor.getValue().color());
        assertTrue(nameCaptor.getValue().hasDecoration(TextDecoration.BOLD));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Component>> loreCaptor = ArgumentCaptor.forClass(List.class);
        verify(this.itemMeta).lore(loreCaptor.capture());
        TextComponent loreLine = (TextComponent) loreCaptor.getValue().get(0);
        assertEquals(MessagePalette.BODY, loreLine.color());
        assertEquals("A powder emitting a strong lively aura.", loreLine.content());
    }
}
