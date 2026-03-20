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

import java.util.List;

/**
 * This interface represents a CompletableRecipeRegistry.
 * A CompletableRecipeRegistry is used to manage CompletableRecipes.
 */
public interface CompletableRecipeRegistryInterface {

    /**
     * Returns the CompletableRecipes that match the given AltarType.
     *
     * @param altarType The type of the altar.
     * @return The CompletableRecipes that match the given AltarType.
     */
    List<CompletableRecipeInterface> getRecipesByAltarType(Class<? extends AltarInterface> altarType);

    /**
     * Finds a CompletableRecipe that matches the given AltarType and Altar.
     *
     * @param altar The altar.
     * @return The CompletableRecipe that matches the given AltarType and Altar.
     */
    CompletableRecipeInterface findMatchingRecipe(AltarInterface altar);

    /**
     * Registers a CompletableRecipe.
     *
     * @param recipe The CompletableRecipe to register.
     * @return true if the recipe was successfully registered, false otherwise.
     */
    boolean registerRecipe(CompletableRecipeInterface recipe);

    /**
     * Registers a list of CompletableRecipes.
     *
     * @param recipes The list of CompletableRecipes to register.
     * @return true if the recipes were successfully registered, false otherwise.
     */
    boolean registerRecipes(List<CompletableRecipeInterface> recipes);

    /**
     * Unregisters a CompletableRecipe.
     *
     * @param recipe The CompletableRecipe to unregister.
     * @return true if the recipe was successfully unregistered, false otherwise.
     */
    boolean unregisterRecipe(CompletableRecipeInterface recipe);
}