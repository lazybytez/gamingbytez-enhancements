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
package de.lazybytez.gamingbytezenhancements.lib.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessagePrefixTest {
    @Test
    void rendersBracketsInDecorationAndTheNameInTheBrandColor() {
        MessagePrefix prefix = MessagePrefix.of("MythicAltar", NamedTextColor.GOLD);

        Component expected = Component.text("[", NamedTextColor.DARK_GRAY)
                .append(Component.text("MythicAltar", NamedTextColor.GOLD))
                .append(Component.text("] ", NamedTextColor.DARK_GRAY));

        assertEquals(expected, prefix.component());
    }

    @Test
    void keepsTheBrandColorPerFeature() {
        MessagePrefix prefix = MessagePrefix.of("MinecartPortals", NamedTextColor.LIGHT_PURPLE);

        Component expected = Component.text("[", MessagePalette.DECORATION)
                .append(Component.text("MinecartPortals", NamedTextColor.LIGHT_PURPLE))
                .append(Component.text("] ", MessagePalette.DECORATION));

        assertEquals(expected, prefix.component());
    }
}
