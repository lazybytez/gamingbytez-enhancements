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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar;

import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;

import java.util.Map;

/**
 * This interface represents an Altar.
 * <p>
 * An Altar consists of five pedestals, each representing a different direction (center, north, south, east, west).
 */
public interface AltarInterface {
    /**
     * Returns the location of the altar.
     * <p>
     * The location is always the diamond block below the item frame of the center pedestal.
     *
     * @return Location of the altar.
     */
    Location getLocation();

    /**
     * Returns the ItemFrame located at the given pedestal.
     *
     * @param location The location of the pedestal.
     * @return The ItemFrame at the given pedestal.
     */
    ItemFrame getPedestal(PedestalLocation location);

    /**
     * Returns a map of all pedestals of the altar.
     *
     * @return Map of all pedestals.
     */
    Map<PedestalLocation, ItemFrame> getPedestals();
}
