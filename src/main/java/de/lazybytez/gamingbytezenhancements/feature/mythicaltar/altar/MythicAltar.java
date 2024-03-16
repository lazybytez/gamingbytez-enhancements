package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.CompletableRecipeInterface;
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

    public static List<CompletableRecipeInterface> getDefaultRecipes() {
        return List.of(
                // Weather recipes
                new SunRitualAltarRecipe(),
                new RainRitualAltarRecipe(),
                new ThunderstormRitualAltarRecipe(),

                // Time recipes
                new TimeDayAltarRecipe(),
                new TimeNightAltarRecipe()
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
