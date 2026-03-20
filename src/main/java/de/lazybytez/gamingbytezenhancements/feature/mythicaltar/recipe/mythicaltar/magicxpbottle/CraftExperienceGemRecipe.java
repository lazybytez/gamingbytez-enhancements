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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.magicxpbottle;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.MythicAltar;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle.EssenceOfSpawnerManager;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle.ExperienceGemManager;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.particles.HelixAltarParticleEffect;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.AbstractAltarRecipe;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

/**
 * Recipe that allows players to craft an experience gem.
 */
public class CraftExperienceGemRecipe extends AbstractAltarRecipe {

    private final MythicAltarFeature mythicAltarFeature;

    /**
     * Constructs a new MagicXpBottleRecipe.
     */
    public CraftExperienceGemRecipe(MythicAltarFeature mythicAltarFeature) {
        super(MythicAltar.class, true);

        this.mythicAltarFeature = mythicAltarFeature;
    }

    @Override
    public void onRecipeComplete(Plugin plugin, AltarInterface altar, PlayerItemFrameChangeEvent event, Runnable removeLock) {
        HelixAltarParticleEffect effect = new HelixAltarParticleEffect(
                plugin,
                Color.SILVER,
                0.025,
                2.5,
                4.0,
                3
        );

        effect.executeParticleEffect(
                altar,
                event,
                (effectPlugin, effectAltar, effectEvent) -> {
                    World effectWorld = effectAltar.getLocation().getWorld();

                    ItemFrame pedestal = effectAltar.getPedestal(PedestalLocation.CENTER);
                    ItemStack experienceGem = this.mythicAltarFeature
                            .getCustomItemManagerRegistry()
                            .getCustomItemManager(ExperienceGemManager.class)
                            .createCustomItem();

                    effectWorld.dropItem(pedestal.getLocation(), experienceGem);

                    removeLock.run();
                });
    }

    @Override
    public boolean validateAltarState(AltarInterface altar) {
        if (altar.getPedestal(PedestalLocation.CENTER).getItem().getType() != Material.DIAMOND) {
            return false;
        }

        // Cannot craft gem using gem as input
        ExperienceGemManager experienceGemManager = this.mythicAltarFeature
                .getCustomItemManagerRegistry()
                .getCustomItemManager(ExperienceGemManager.class);
        if (experienceGemManager.isCustomItem(altar.getPedestal(PedestalLocation.CENTER).getItem())) {
            return false;
        }

        List<PedestalLocation> outerPedestals = List.of(
                PedestalLocation.NORTH_WEST,
                PedestalLocation.SOUTH_WEST,
                PedestalLocation.NORTH_EAST,
                PedestalLocation.SOUTH_EAST
        );

        EssenceOfSpawnerManager essenceOfSpawnerManager = this.mythicAltarFeature
                .getCustomItemManagerRegistry()
                .getCustomItemManager(EssenceOfSpawnerManager.class);
        for (PedestalLocation location : outerPedestals) {
            if (!essenceOfSpawnerManager.isCustomItem(altar.getPedestal(location).getItem())) {
                return false;
            }
        }

        return true;
    }


    @Override
    public Map<PedestalLocation, ItemStack> getRecipe() {
        // Standard mechanism is not used for this recipe due to custom items.
        return Map.of();
    }
}
