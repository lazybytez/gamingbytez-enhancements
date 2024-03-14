package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.AltarRecipeInterface;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
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
     * @param event The event that triggered the recipe completion.
     */
    void onRecipeComplete(AltarInterface altar, PlayerItemFrameChangeEvent event);

    /**
     * Returns the AltarType of the AltarRecipe.
     *
     * @return the AltarType of the AltarRecipe.
     */
    Class<? extends AltarInterface> getAltarType();
}