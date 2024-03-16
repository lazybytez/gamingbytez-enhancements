package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.weather;

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
import org.bukkit.World;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;

/**
 * This class represents a weather rain recipe.
 * The weather rain recipe allows players to change the weather to rain using the {@link MythicAltar}.
 */
public class RainRitualAltarRecipe extends AbstractAltarRecipe {
    /**
     * Constructs a new rain ritual recipe.
     */
    public RainRitualAltarRecipe() {
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
        World world = player.getWorld();

        if (world.hasStorm() && !world.isThundering()) {
            player.sendMessage(Component.textOfChildren(
                    MythicAltarFeature.CHAT_MESSAGE_PREFIX,
                    Component.text("The weather is already stormy!", NamedTextColor.RED)
            ));

            for (ItemFrame pedestal : altar.getPedestals().values()) {
                world.dropItem(pedestal.getLocation(), pedestal.getItem());
            }

            removeLock.run();

            return;
        }

        new LinesToCenterAltarParticleEffect(plugin, Color.BLUE).executeParticleEffect(
                altar,
                event,
                (effectPlugin, effectAltar, effectEvent) -> {
                    World effectWorld = effectAltar.getLocation().getWorld();

                    effectWorld.setStorm(true);
                    effectWorld.setThundering(false);

                    for (PedestalLocation pedestalLocation : effectAltar.getPedestals().keySet()) {
                        ItemFrame pedestal = effectAltar.getPedestal(pedestalLocation);
                        effectWorld.dropItem(
                                pedestal.getLocation(),
                                new ItemStack(Material.BUCKET)
                        );
                    }

                    Bukkit.broadcast(Component.textOfChildren(
                            MythicAltarFeature.CHAT_MESSAGE_PREFIX,
                            Component.text("The weather has been set to rain by " + event.getPlayer().getName() + " using the rain ritual!", NamedTextColor.GOLD)
                    ));
                    removeLock.run();
                });
    }

    /**
     * Returns the recipe for the rain ritual.
     *
     * @return the recipe for the rain ritual.
     */
    @Override
    public Map<PedestalLocation, ItemStack> getRecipe() {
        return Map.of(
                PedestalLocation.CENTER, new ItemStack(Material.WATER_BUCKET),
                PedestalLocation.NORTH_WEST, new ItemStack(Material.WATER_BUCKET),
                PedestalLocation.SOUTH_WEST, new ItemStack(Material.WATER_BUCKET),
                PedestalLocation.NORTH_EAST, new ItemStack(Material.WATER_BUCKET),
                PedestalLocation.SOUTH_EAST, new ItemStack(Material.WATER_BUCKET)
        );
    }
}
