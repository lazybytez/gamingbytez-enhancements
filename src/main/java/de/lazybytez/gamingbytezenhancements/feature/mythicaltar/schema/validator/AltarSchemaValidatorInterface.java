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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.schema.validator;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.schema.structure.AltarStructureInterface;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * This interface represents the validator for the altar schema.
 * <p>
 * The validator is responsible for validating the altar structure.
 * It checks if the blocks and item frames are valid and if the altar is complete.
 * <p>
 * Note that a validator always validates at the altar center level, which means every function
 * expects the diamond block below the center item frame as the location.
 * This limits the structures to having some center item frame. However, as the schema is defined
 * in a 3D space, the validator can validate any altar structure (so in theory, even when the center item
 * frame is not in the center of the altar).
 */
public interface AltarSchemaValidatorInterface {
    /**
     * Validates the given altar comprehensively, including both blocks and pedestals.
     * This method serves as a universal validator by internally invoking both
     * {@link #validateStructure(AltarStructureInterface, Location, World)} and
     * {@link #validatePedestal(AltarStructureInterface, Location, World)} to ensure
     * the entire altar structure meets the required criteria.
     *
     * @param altarStructure The altar structure to validate.
     * @param location       The location of the altar. This is the location of the center block.
     * @param world          The world in which the altar is located.
     * @return True if the entire structure (both blocks and pedestals) is valid, false otherwise.
     */
    boolean validate(AltarStructureInterface altarStructure, Location location, World world);

    /**
     * Validates the given altar block structure.
     *
     * @param altarStructure The altar structure to validate.
     * @param location       The location of the altar. This is the location of the center block.
     * @param world          The world in which the altar is located.
     * @return True if the structure is valid, false otherwise.
     */
    boolean validateStructure(AltarStructureInterface altarStructure, Location location, World world);

    /**
     * Validates the given pedestal structure.
     *
     * @param pedestalStructure The pedestal structure to validate
     * @param location          The location of the altar. This is the location of the center block.
     * @param world             The world in which the altar is located.
     * @return True if the structure is valid, false otherwise.
     */
    boolean validatePedestal(AltarStructureInterface pedestalStructure, Location location, World world);
}
