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
 * This class represents a weather clear recipe.
 * The weather clear recipe allows players to clear the weather using the {@link MythicAltar}.
 */
public class SunRitualAltarRecipe extends AbstractAltarRecipe {
    /**
     * Constructs a new sun ritual recipe.
     */
    public SunRitualAltarRecipe() {
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

        if (world.isClearWeather()) {
            player.sendMessage(Component.textOfChildren(
                    MythicAltarFeature.CHAT_MESSAGE_PREFIX,
                    Component.text("The weather is already clear!", NamedTextColor.RED)
            ));

            for (ItemFrame pedestal : altar.getPedestals().values()) {
                world.dropItem(pedestal.getLocation(), pedestal.getItem());
            }

            removeLock.run();

            return;
        }

        new LinesToCenterAltarParticleEffect(plugin, Color.YELLOW).executeParticleEffect(
                altar,
                event,
                (effectPlugin, effectAltar, effectEvent) -> {
                    World effectWorld = effectAltar.getLocation().getWorld();

                    effectWorld.setStorm(false);
                    effectWorld.setThundering(false);

                    for (PedestalLocation pedestalLocation : effectAltar.getPedestals().keySet()) {
                        if (pedestalLocation == PedestalLocation.CENTER) {
                            continue;
                        }

                        ItemFrame pedestal = effectAltar.getPedestal(pedestalLocation);
                        effectWorld.dropItem(
                                pedestal.getLocation(),
                                new ItemStack(Material.WET_SPONGE)
                        );
                    }

                    Bukkit.broadcast(Component.textOfChildren(
                            MythicAltarFeature.CHAT_MESSAGE_PREFIX,
                            Component.text("The weather has been cleared by " + event.getPlayer().getName() + " using the sun ritual!", NamedTextColor.GOLD)
                    ));
                    removeLock.run();
                });
    }

    /**
     * Returns the recipe for the sun ritual.
     *
     * @return the recipe for the sun ritual.
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
