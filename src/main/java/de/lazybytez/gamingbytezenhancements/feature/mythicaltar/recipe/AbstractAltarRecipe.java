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

/**
 * This abstract class represents a CompletableRecipe.
 * <p>
 * A CompletableRecipe is a combination of an AltarRecipe and an AltarAction.
 * It is used to create a recipe with a specific effect when completed.
 * The CompletableRecipe is associated with a specific type of Altar and has an option to automatically clean up the Altar after the recipe is completed.
 */
public abstract class AbstractAltarRecipe implements CompletableRecipeInterface {
    private final Class<? extends AltarInterface> altarType;
    private final boolean autoCleanupAltar;

    /**
     * Constructor for the AbstractAltarRecipe.
     *
     * @param altarType        The type of the altar associated with this recipe.
     * @param autoCleanupAltar If true, the altar will be automatically cleaned up after the recipe is completed.
     */
    protected AbstractAltarRecipe(Class<? extends AltarInterface> altarType, boolean autoCleanupAltar) {
        this.altarType = altarType;
        this.autoCleanupAltar = autoCleanupAltar;
    }

    /**
     * Default implementation of validation if an altar's content currently matches the recipe.
     * <p>
     * This implementation is used for recipes that only require a simple ItemStack comparison using
     * the isSimilar function of ItemStacks.
     * If a more complex validation is required, the method should be overridden.
     * <p>
     * Note that it may make sense to create your own abstract class that extends this class
     * if you have some common validation logic that you want to reuse in multiple recipes.
     *
     * @param altar The altar to validate.
     * @return whether the given altar currently matches the recipe.
     */
    @Override
    public boolean validateAltarState(AltarInterface altar) {
        for (PedestalLocation location : this.getRecipe().keySet()) {
            if (!altar.getPedestal(location).getItem().isSimilar(this.getRecipe().get(location))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the type of the altar associated with this recipe.
     *
     * @return The type of the altar.
     */
    @Override
    public Class<? extends AltarInterface> getAltarType() {
        return this.altarType;
    }

    /**
     * Returns whether the altar should be automatically cleaned up after the recipe is completed.
     *
     * @return True if the altar should be automatically cleaned up, false otherwise.
     */
    @Override
    public boolean autoCleanupAltar() {
        return this.autoCleanupAltar;
    }
}