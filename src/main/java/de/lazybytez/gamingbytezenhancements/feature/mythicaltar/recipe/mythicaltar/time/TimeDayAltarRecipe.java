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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.time;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.MythicAltar;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.particles.LinesToCenterAltarParticleEffect;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.AbstractAltarRecipe;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePalette;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import net.kyori.adventure.text.Component;
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
    private final MythicAltarFeature mythicAltarFeature;

    /**
     * Constructs a new time day ritual recipe.
     *
     * @param mythicAltarFeature the feature owning the messenger this recipe sends through
     */
    public TimeDayAltarRecipe(MythicAltarFeature mythicAltarFeature) {
        super(MythicAltar.class, true);

        this.mythicAltarFeature = mythicAltarFeature;
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
            this.mythicAltarFeature.getMessenger().error(player, "It is already day!");

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

                    Component body = Component.text("The time has been changed to day by ", MessagePalette.EMPHASIS)
                            .append(Component.text(event.getPlayer().getName(), MessagePalette.SUBJECT))
                            .append(Component.text(" using a time ritual!", MessagePalette.EMPHASIS));
                    Bukkit.broadcast(this.mythicAltarFeature.getMessenger().prefixed(body));
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

