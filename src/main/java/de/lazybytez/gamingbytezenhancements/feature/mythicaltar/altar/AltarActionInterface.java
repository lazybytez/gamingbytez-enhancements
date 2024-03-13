package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar;

import org.bukkit.entity.Player;

/**
 * This interface represents an AltarAction.
 * <p>
 * An AltarAction is triggered when an AltarRecipe is completed.
 * It can be used to execute some special code when a recipe is completed.
 * <p>
 * For example, an AltarAction could be used to give the player a special item when a recipe is completed.
 * Or it could do something in the world like changing the weather or spawning a mob.
 * <p>
 * Using this pattern, it is possible to create a wide variety of different recipes with different effects.
 */
public interface AltarActionInterface {

    /**
     * This method is called when an AltarRecipe is completed.
     *
     * @param altar The altar where the recipe was completed.
     * @param recipe The recipe that was completed.
     * @param completer The player who completed the recipe.
     */
    void onRecipeComplete(AltarInterface altar, AltarRecipeInterface recipe, Player completer);
}