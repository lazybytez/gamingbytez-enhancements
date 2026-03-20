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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.schema.structure;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.Map;

/**
 * This interface represents the structure of an altar.
 */
public interface AltarStructureInterface {
    /**
     * Returns the structure of the altar.
     * <p>
     * The structure is a map of vectors and materials, representing the blocks of the altar.
     * The vector (key of the map) is relative to the location of the altar.
     *
     * @return Structure of the altar.
     */
    Map<Vector, Material> getAltarStructure();

    /**
     * Returns the structure of the pedestals.
     * <p>
     * The structure is a map of vectors and entity types, representing the top of the pedestals.
     * The Vector (key of the map) is relative to the location of the altars center pedestal.
     * <p>
     * The entity type should be either an item frame or a glow item frame.
     * <p>
     * Note: Implementations should ensure that the EntityType provided is either ITEM_FRAME or GLOW_ITEM_FRAME.
     *
     * @return Structure of the pedestals.
     */
    Map<Vector, EntityType> getPedestalStructure();

    /**
     * Returns the class of the altar.
     *
     * @return Class of the altar.
     */
    Class<? extends AltarInterface> getAltarClass();
}
