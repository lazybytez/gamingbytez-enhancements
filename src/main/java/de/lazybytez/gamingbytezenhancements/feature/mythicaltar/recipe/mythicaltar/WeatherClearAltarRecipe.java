package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.MythicAltar;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.particles.LinesToCenterAltarParticleEffect;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.AbstractAltarRecipe;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;

/**
 * This class represents a WeatherClearAltarRecipe.
 * A WeatherClearAltarRecipe is responsible for handling the recipe that allows players to clear the weather
 * using the {@link MythicAltar}.
 */
public class WeatherClearAltarRecipe extends AbstractAltarRecipe {
    /**
     * Constructs a new WeatherClearAltarRecipe.
     */
    public WeatherClearAltarRecipe() {
        super(MythicAltar.class, true);
    }

    /**
     * This method is called when the recipe is completed on an altar.
     *
     * @param plugin     The plugin instance of the GamingBytezEnhancements plugin.
     * @param altar      The altar where the recipe was completed.
     * @param event      The event that triggered the recipe completion.
     * @param removeLock A runnable that can be used to remove the lock from the altar.
     */
    @Override
    public void onRecipeComplete(
            Plugin plugin,
            AltarInterface altar,
            PlayerItemFrameChangeEvent event,
            Runnable removeLock
    ) {
        Player player = event.getPlayer();

        if (player.getWorld().isClearWeather()) {
            player.sendMessage(Component.textOfChildren(
                    MythicAltarFeature.CHAT_MESSAFE_PREFIX,
                    Component.text("The weather is already clear!", NamedTextColor.RED)
            ));

            for (ItemFrame pedestal : altar.getPedestals().values()) {
                player.getWorld().dropItem(pedestal.getLocation(), pedestal.getItem());
            }

            removeLock.run();

            return;
        }

        new LinesToCenterAltarParticleEffect(plugin, Color.YELLOW).executeParticleEffect(
                altar,
                event,
                (effectPlugin, effectAltar, effectEvent) -> {
                    effectAltar.getLocation().getWorld().setStorm(false);
                    effectAltar.getLocation().getWorld().setThundering(false);

                    for (PedestalLocation pedestalLocation : effectAltar.getPedestals().keySet()) {
                        if (pedestalLocation == PedestalLocation.CENTER) {
                            continue;
                        }

                        ItemFrame pedestal = effectAltar.getPedestal(pedestalLocation);
                        pedestal.getLocation().getWorld().dropItem(
                                pedestal.getLocation(),
                                new ItemStack(Material.WET_SPONGE)
                        );
                    }

                    Bukkit.broadcast(Component.textOfChildren(
                            MythicAltarFeature.CHAT_MESSAFE_PREFIX,
                            Component.text("The weather has been cleared by " + event.getPlayer().getName() + " using the sun ritual!", NamedTextColor.GOLD)
                    ));
                    removeLock.run();
        });
    }

    /**
     * Returns the recipe for the WeatherClearAltarRecipe.
     *
     * @return the recipe for the WeatherClearAltarRecipe.
     */
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
