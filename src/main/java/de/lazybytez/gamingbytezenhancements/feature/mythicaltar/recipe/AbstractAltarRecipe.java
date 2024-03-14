package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation;
import org.bukkit.Bukkit;

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
     * @param altarType The type of the altar associated with this recipe.
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
                Bukkit.broadcastMessage("Item not similar: " + altar.getPedestal(location).getItem().getType() + " " + this.getRecipe().get(location).getType());
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