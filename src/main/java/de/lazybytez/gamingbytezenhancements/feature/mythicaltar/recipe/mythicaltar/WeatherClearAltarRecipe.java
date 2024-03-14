package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.MythicAltar;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.AbstractAltarRecipe;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class WeatherClearAltarRecipe extends AbstractAltarRecipe {
    public WeatherClearAltarRecipe() {
        super(MythicAltar.class, true);
    }

    @Override
    public void onRecipeComplete(AltarInterface altar, PlayerItemFrameChangeEvent event) {
        altar.getLocation().getWorld().setClearWeatherDuration(0);
        Bukkit.broadcastMessage("Weather cleared!");
    }

    @Override
    public Map<PedestalLocation, ItemStack> getRecipe() {
        return Map.of(
                PedestalLocation.CENTER, new ItemStack(Material.LIGHTNING_ROD),
                PedestalLocation.NORTH_WEST, new ItemStack(Material.SPONGE),
                PedestalLocation.SOUTH_WEST, new ItemStack(Material.SPONGE),
                PedestalLocation.NORTH_EAST, new ItemStack(Material.SPONGE),
                PedestalLocation.SOUTH_EAST, new ItemStack(Material.SPONGE)
        );
    }
}
