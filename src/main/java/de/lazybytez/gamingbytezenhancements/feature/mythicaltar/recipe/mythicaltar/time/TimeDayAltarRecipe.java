package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.time;

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
 * This class represents a time day recipe.
 * The time day recipe allows players to change the time to day using the {@link MythicAltar}.
 */
public class TimeDayAltarRecipe extends AbstractAltarRecipe {
    /**
     * Constructs a new time day ritual recipe.
     */
    public TimeDayAltarRecipe() {
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

        if (world.isDayTime()) {
            player.sendMessage(Component.textOfChildren(
                    MythicAltarFeature.CHAT_MESSAGE_PREFIX,
                    Component.text("It is already day!", NamedTextColor.RED)
            ));

            for (ItemFrame pedestal : altar.getPedestals().values()) {
                world.dropItem(pedestal.getLocation(), pedestal.getItem());
            }

            removeLock.run();

            return;
        }

        new LinesToCenterAltarParticleEffect(plugin, Color.WHITE).executeParticleEffect(
                altar,
                event,
                (effectPlugin, effectAltar, effectEvent) -> {
                    effectAltar.getLocation().getWorld().setTime(0L);

                    Bukkit.broadcast(Component.textOfChildren(
                            MythicAltarFeature.CHAT_MESSAGE_PREFIX,
                            Component.text("The time has been changed to day by " + event.getPlayer().getName() + " using a time ritual!", NamedTextColor.GOLD)
                    ));
                    removeLock.run();
                });
    }

    /**
     * Returns the recipe for the time day ritual.
     *
     * @return the recipe for the time day ritual.
     */
    @Override
    public Map<PedestalLocation, ItemStack> getRecipe() {
        return Map.of(
                PedestalLocation.CENTER, new ItemStack(Material.TORCH),
                PedestalLocation.NORTH_WEST, new ItemStack(Material.TORCH),
                PedestalLocation.SOUTH_WEST, new ItemStack(Material.TORCH),
                PedestalLocation.NORTH_EAST, new ItemStack(Material.TORCH),
                PedestalLocation.SOUTH_EAST, new ItemStack(Material.TORCH)
        );
    }
}

