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
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.plugin.Plugin;

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
     * @param plugin     The plugin instance of the GamingBytezEnhancements plugin.
     * @param altar      The altar where the recipe was completed.
     * @param event      The event that triggered the recipe completion.
     * @param removeLock A runnable that can be used to remove the lock from the altar.
     */
    void onRecipeComplete(Plugin plugin, AltarInterface altar, PlayerItemFrameChangeEvent event, Runnable removeLock);

    /**
     * Returns the AltarType of the AltarRecipe.
     *
     * @return the AltarType of the AltarRecipe.
     */
    Class<? extends AltarInterface> getAltarType();
}