package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.CompletableRecipeInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.WeatherClearAltarRecipe;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.ItemFrame;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class MythicAltar extends AbstractAltar {
    public static List<CompletableRecipeInterface> getDefaultRecipes() {
        return List.of(
            new WeatherClearAltarRecipe()
        );
    }

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
