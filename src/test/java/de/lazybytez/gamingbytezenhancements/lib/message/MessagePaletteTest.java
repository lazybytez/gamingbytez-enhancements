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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessagePaletteTest {
    @Test
    void exposesExactlyEightPublicColorTokens() throws Exception {
        assertEquals(8, this.readTokens().size());
    }

    @Test
    void noTwoTokensResolveToTheSameColor() throws Exception {
        List<TextColor> tokens = this.readTokens();

        assertEquals(tokens.size(), new HashSet<>(tokens).size());
    }

    @Test
    void mapsEveryTokenToItsSemanticColor() {
        assertEquals(NamedTextColor.DARK_GRAY, MessagePalette.DECORATION);
        assertEquals(NamedTextColor.GRAY, MessagePalette.BODY);
        assertEquals(NamedTextColor.WHITE, MessagePalette.VALUE);
        assertEquals(NamedTextColor.YELLOW, MessagePalette.SUBJECT);
        assertEquals(NamedTextColor.AQUA, MessagePalette.HEADING);
        assertEquals(NamedTextColor.GOLD, MessagePalette.EMPHASIS);
        assertEquals(NamedTextColor.GREEN, MessagePalette.SUCCESS);
        assertEquals(NamedTextColor.RED, MessagePalette.ERROR);
    }

    @Test
    void cannotBeInstantiated() throws Exception {
        Constructor<MessagePalette> constructor = MessagePalette.class.getDeclaredConstructor();

        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    }

    private List<TextColor> readTokens() throws Exception {
        List<TextColor> tokens = new ArrayList<>();

        for (Field field : MessagePalette.class.getDeclaredFields()) {
            int modifiers = field.getModifiers();

            if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)) {
                continue;
            }

            assertTrue(Modifier.isFinal(modifiers));
            assertEquals(TextColor.class, field.getType());
            tokens.add((TextColor) field.get(null));
        }

        return tokens;
    }
}
