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
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Renders a location as a component, in the one format every feature shows.
 *
 * The world is identified by its key, which stays stable across the plugin and
 * avoids the obsolete world name accessor.
 */
public final class LocationFormat {
    private static final Component OPEN = Component.text(" (", MessagePalette.DECORATION);
    private static final Component OPEN_WITHOUT_WORLD = Component.text("(", MessagePalette.DECORATION);
    private static final Component AXIS_SEPARATOR = Component.text(", ", MessagePalette.DECORATION);
    private static final Component CLOSE = Component.text(")", MessagePalette.DECORATION);

    private LocationFormat() {
    }

    /**
     * Render a location, or a placeholder when there is none.
     *
     * A location whose world has been unloaded keeps its coordinates, because
     * they still tell the reader where the location points.
     *
     * @param location the location to render, may be null
     * @return the rendered location
     */
    public static Component format(Location location) {
        if (location == null) {
            return Component.text("not set", MessagePalette.BODY).decorate(TextDecoration.ITALIC);
        }

        World world = resolveWorld(location);

        if (world == null) {
            return appendCoordinates(OPEN_WITHOUT_WORLD, location);
        }

        Component head = Component.text(world.getKey().asMinimalString(), MessagePalette.VALUE).append(OPEN);

        return appendCoordinates(head, location);
    }

    /**
     * Resolve the world of a location, treating an absent one as no world at all.
     *
     * {@link Location#getWorld()} returns null when no world was ever set, but throws once a world
     * that was set has been unloaded. Both mean the same thing here, so both yield null. The
     * non-throwing alternative, {@link Location#isWorldLoaded()}, resolves the world through the
     * running server, which this formatter has no reason to require.
     *
     * @param location the location to resolve
     * @return the world, or null when there is none to render
     */
    private static World resolveWorld(Location location) {
        try {
            return location.getWorld();
        } catch (IllegalArgumentException unloaded) {
            return null;
        }
    }

    private static Component appendCoordinates(Component head, Location location) {
        return head.append(Component.text(location.getBlockX(), MessagePalette.VALUE))
                .append(AXIS_SEPARATOR)
                .append(Component.text(location.getBlockY(), MessagePalette.VALUE))
                .append(AXIS_SEPARATOR)
                .append(Component.text(location.getBlockZ(), MessagePalette.VALUE))
                .append(CLOSE);
    }
}
