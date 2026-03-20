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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.CompletableRecipeInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.magicxpbottle.CraftExperienceGemRecipe;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.magicxpbottle.CraftMagicXpBottleRecipe;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.magicxpbottle.FillMagicXpBottleRecipe;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.safarinet.SafariNetRecipe;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.time.TimeDayAltarRecipe;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.time.TimeNightAltarRecipe;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.weather.RainRitualAltarRecipe;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.weather.SunRitualAltarRecipe;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.weather.ThunderstormRitualAltarRecipe;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public final class MythicAltar extends AbstractAltar {
    /**
     * Create a new Mythic Altar.
     *
     * @param location The location of the altar. This is the diamond block
     *                 below the item frame of the center pedestal.
     */
    public MythicAltar(
            @NotNull Location location
    ) {
        super(location, EntityType.GLOW_ITEM_FRAME);
    }

    public static List<CompletableRecipeInterface> getDefaultRecipes(MythicAltarFeature mythicAltarFeature) {
        return List.of(
                // Weather recipes
                new SunRitualAltarRecipe(),
                new RainRitualAltarRecipe(),
                new ThunderstormRitualAltarRecipe(),

                // Time recipes
                new TimeDayAltarRecipe(),
                new TimeNightAltarRecipe(),

                // Magic XP Bottle
                new CraftExperienceGemRecipe(mythicAltarFeature),
                new CraftMagicXpBottleRecipe(mythicAltarFeature),
                new FillMagicXpBottleRecipe(mythicAltarFeature),

                // Safari Net
                new SafariNetRecipe(mythicAltarFeature)
        );
    }

    @Override
    public Map<PedestalLocation, Vector> getPedestalLocations() {
        // Note that the item frames  (pedestal inputs) are always above the pedestal blocks.
        // Therefore, we need to add 1 to the y-coordinate of the pedestal location.
        // By not predefining this offset, we can easily also change the position of the item frames
        // in the future.
        return Map.of(
                PedestalLocation.CENTER, new Vector(0, 1, 0),
                PedestalLocation.NORTH_WEST, new Vector(2, 1, -2),
                PedestalLocation.NORTH_EAST, new Vector(2, 1, 2),
                PedestalLocation.SOUTH_WEST, new Vector(-2, 1, -2),
                PedestalLocation.SOUTH_EAST, new Vector(-2, 1, 2)
        );
    }
}
