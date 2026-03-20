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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * This interface represents an AltarRecipe.
 * <p>
 * An AltarRecipe consists of five ItemStacks,
 * each representing a different pedestal (center, north, south, east, west).
 */
public interface AltarRecipeInterface {
    /**
     * Returns the recipe structure of the AltarRecipe.
     *
     * @return a Map with the PedestalLocation as key and the ItemStack as value.
     */
    Map<PedestalLocation, ItemStack> getRecipe();

    /**
     * Validates whether the given altar currently matches the recipe.
     * <p>
     * This function allows for more complex recipes that require specific conditions to be met.
     * By keeping the validation logic in the recipe, it is possible to create a wide variety of different
     * recipes with different conditions, that may not be possible with a simple ItemStack (type) comparison.
     *
     * @return whether the given altar currently matches the recipe.
     */
    boolean validateAltarState(AltarInterface altar);

    /**
     * Returns the AltarType of the AltarRecipe.
     *
     * @return the AltarType of the AltarRecipe.
     */
    Class<? extends AltarInterface> getAltarType();

    /**
     * Whether to destroy all items on the altar when the recipe is completed automatically.
     *
     * @return true if the items should be destroyed, false otherwise.
     */
    boolean autoCleanupAltar();
}