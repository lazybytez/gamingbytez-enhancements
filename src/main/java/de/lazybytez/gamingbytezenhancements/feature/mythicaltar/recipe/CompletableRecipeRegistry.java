package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a CompletableRecipeRegistry.
 * A CompletableRecipeRegistry is used to manage CompletableRecipes.
 * It stores the recipes in a map where the key is the type of the altar and the value is a list of CompletableRecipes.
 */
public final class CompletableRecipeRegistry implements CompletableRecipeRegistryInterface {
    private final Map<Class<? extends AltarInterface>, List<CompletableRecipeInterface>> recipes;

    /**
     * Constructor for the CompletableRecipeRegistry.
     * Initializes the recipes map.
     */
    public CompletableRecipeRegistry() {
        this.recipes = new HashMap<>();
    }

    /**
     * Returns the CompletableRecipes that match the given AltarType.
     *
     * @param altarType The type of the altar.
     * @return The CompletableRecipes that match the given AltarType. If no recipes are found, an empty list is returned.
     */
    @Override
    public List<CompletableRecipeInterface> getRecipesByAltarType(Class<? extends AltarInterface> altarType) {
        List<CompletableRecipeInterface> recipes = this.recipes.get(altarType);

        if (recipes == null) {
            return List.of();
        }

        return recipes;
    }

    /**
     * Finds a CompletableRecipe that matches the given AltarType and Altar.
     *
     * @param altar The altar.
     * @return The CompletableRecipe that matches the given AltarType and Altar. If no matching recipe is found, null is returned.
     */
    @Override
    public CompletableRecipeInterface findMatchingRecipe(AltarInterface altar) {
        List<CompletableRecipeInterface> recipes = this.recipes.get(altar.getClass());

        if (recipes == null) {
            return null;
        }

        for (CompletableRecipeInterface recipe : this.recipes.get(altar.getClass())) {
            if (recipe.validateAltarState(altar)) {
                return recipe;
            }
        }

        return null;
    }

    /**
     * Registers a CompletableRecipe.
     *
     * @param recipe The CompletableRecipe to register.
     * @return true if the recipe was successfully registered, false otherwise.
     */
    @Override
    public boolean registerRecipe(CompletableRecipeInterface recipe) {
        if (!this.recipes.containsKey(recipe.getAltarType())) {
            List<CompletableRecipeInterface> recipes = new ArrayList<>();
            recipes.add(recipe);

            this.recipes.put(recipe.getAltarType(), recipes);
            return true;
        }

        List<CompletableRecipeInterface> recipes = this.recipes.get(recipe.getAltarType());
        if (recipes.contains(recipe)) {
            return false;
        }

        return recipes.add(recipe);
    }

    /**
     * Registers a list of CompletableRecipes.
     *
     * @param recipes The list of CompletableRecipes to register.
     * @return true if the recipes were successfully registered, false otherwise.
     */
    @Override
    public boolean registerRecipes(List<CompletableRecipeInterface> recipes) {
        boolean success = true;
        for (CompletableRecipeInterface recipe : recipes) {
            success = this.registerRecipe(recipe);
        }

        return success;
    }

    /**
     * Unregisters a CompletableRecipe.
     *
     * @param recipe The CompletableRecipe to unregister.
     * @return true if the recipe was successfully unregistered, false otherwise.
     */
    @Override
    public boolean unregisterRecipe(CompletableRecipeInterface recipe) {
        if (!this.recipes.containsKey(recipe.getAltarType())) {
            return false;
        }

        return this.recipes.get(recipe.getAltarType()).remove(recipe);
    }
}