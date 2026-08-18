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
import java.lang.reflect.Modifier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class LocationFormatTest {
    @Test
    void rendersANullLocationAsItalicBodyText() {
        assertEquals(
                Component.text("not set", MessagePalette.BODY).decorate(TextDecoration.ITALIC),
                LocationFormat.format(null));
    }

    @Test
    void rendersTheWorldKeyAndBlockCoordinates() {
        World world = mock(World.class);
        when(world.getKey()).thenReturn(NamespacedKey.minecraft("overworld"));

        Component expected = Component.text("overworld", MessagePalette.VALUE)
                .append(Component.text(" (", MessagePalette.DECORATION))
                .append(Component.text(12, MessagePalette.VALUE))
                .append(Component.text(", ", MessagePalette.DECORATION))
                .append(Component.text(64, MessagePalette.VALUE))
                .append(Component.text(", ", MessagePalette.DECORATION))
                .append(Component.text(-5, MessagePalette.VALUE))
                .append(Component.text(")", MessagePalette.DECORATION));

        assertEquals(expected, LocationFormat.format(new Location(world, 12.8, 64.0, -4.2)));
    }

    @Test
    void takesTheWorldNameFromTheKeyOnly() {
        World world = mock(World.class);
        when(world.getKey()).thenReturn(NamespacedKey.minecraft("the_nether"));

        LocationFormat.format(new Location(world, 0.0, 0.0, 0.0));

        verify(world).getKey();
        verifyNoMoreInteractions(world);
    }

    @Test
    void rendersCoordinatesOnlyWhenTheWorldWasNeverSet() {
        assertEquals(coordinatesOnly(), LocationFormat.format(new Location(null, 1.0, 2.0, 3.0)));
    }

    @Test
    void rendersCoordinatesOnlyWhenTheWorldHasBeenUnloaded() {
        Location location = mock(Location.class);
        when(location.getWorld()).thenThrow(new IllegalArgumentException("World unloaded"));
        when(location.getBlockX()).thenReturn(1);
        when(location.getBlockY()).thenReturn(2);
        when(location.getBlockZ()).thenReturn(3);

        assertEquals(coordinatesOnly(), LocationFormat.format(location));
    }

    private static Component coordinatesOnly() {
        return Component.text("(", MessagePalette.DECORATION)
                .append(Component.text(1, MessagePalette.VALUE))
                .append(Component.text(", ", MessagePalette.DECORATION))
                .append(Component.text(2, MessagePalette.VALUE))
                .append(Component.text(", ", MessagePalette.DECORATION))
                .append(Component.text(3, MessagePalette.VALUE))
                .append(Component.text(")", MessagePalette.DECORATION));
    }
}
