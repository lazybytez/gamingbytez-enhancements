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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.excavationcharge;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.MythicAltar;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastLevel;
import de.lazybytez.gamingbytezenhancements.lib.gameplay.blast.BlastShape;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.excavationcharge.ExcavationChargeManager;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.particles.RisingRingsAltarParticleEffect;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.AbstractAltarRecipe;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

/**
 * This class represents the Excavation Charge upgrade recipe.
 * It raises an existing Excavation Charge one blast level, framed by TNT, obsidian, an echo shard and a
 * diamond block, while preserving the charge's blast shape, on the {@link MythicAltar}.
 */
public class UpgradeExcavationChargeRecipe extends AbstractAltarRecipe {

    private final MythicAltarFeature mythicAltarFeature;

    /**
     * Constructs a new Excavation Charge upgrade recipe.
     *
     * @param mythicAltarFeature the feature owning the custom item manager registry used to read and create charges
     */
    public UpgradeExcavationChargeRecipe(MythicAltarFeature mythicAltarFeature) {
        super(MythicAltar.class, true);

        this.mythicAltarFeature = mythicAltarFeature;
    }

    /**
     * This method is called when the recipe is completed on an altar.
     * <p>
     * The input charge's shape and level are read before the particle animation starts, since the
     * altar's pedestals are cleared shortly after this method returns.
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
        ExcavationChargeManager excavationChargeManager = this.mythicAltarFeature
                .getCustomItemManagerRegistry()
                .getCustomItemManager(ExcavationChargeManager.class);

        ItemStack centerItem = altar.getPedestal(PedestalLocation.CENTER).getItem();
        BlastShape shape = excavationChargeManager.getShape(centerItem);
        int nextLevel = excavationChargeManager.getLevel(centerItem) + 1;

        RisingRingsAltarParticleEffect effect =
                new RisingRingsAltarParticleEffect(plugin, Color.AQUA);

        effect.executeParticleEffect(
                altar,
                event,
                (effectPlugin, effectAltar, effectEvent) -> {
                    ItemFrame pedestal = effectAltar.getPedestal(PedestalLocation.CENTER);
                    ItemStack upgradedCharge = excavationChargeManager.createCustomItem();
                    excavationChargeManager.setShape(upgradedCharge, shape);
                    excavationChargeManager.setLevel(upgradedCharge, nextLevel);

                    effectAltar.getLocation().getWorld().dropItem(pedestal.getLocation(), upgradedCharge);

                    removeLock.run();
                });
    }

    /**
     * Validates whether the given altar currently matches the recipe.
     * Recipe requires: an existing Excavation Charge below the maximum blast level in the center, and
     * TNT, obsidian, an echo shard and a diamond block in any outer pedestal positions.
     *
     * @param altar The altar to validate.
     * @return whether the given altar currently matches the recipe.
     */
    @Override
    public boolean validateAltarState(AltarInterface altar) {
        ItemStack centerItem = altar.getPedestal(PedestalLocation.CENTER).getItem();

        ExcavationChargeManager excavationChargeManager = this.mythicAltarFeature
                .getCustomItemManagerRegistry()
                .getCustomItemManager(ExcavationChargeManager.class);
        if (!excavationChargeManager.isCustomItem(centerItem)) {
            return false;
        }

        if (excavationChargeManager.getLevel(centerItem) >= BlastLevel.MAX_LEVEL) {
            return false;
        }

        List<PedestalLocation> outerPedestals = List.of(
                PedestalLocation.NORTH_WEST,
                PedestalLocation.SOUTH_WEST,
                PedestalLocation.NORTH_EAST,
                PedestalLocation.SOUTH_EAST
        );

        boolean hasTnt = false;
        boolean hasObsidian = false;
        boolean hasEchoShard = false;
        boolean hasDiamondBlock = false;

        for (PedestalLocation location : outerPedestals) {
            Material itemType = altar.getPedestal(location).getItem().getType();

            switch (itemType) {
                case TNT -> hasTnt = true;
                case OBSIDIAN -> hasObsidian = true;
                case ECHO_SHARD -> hasEchoShard = true;
                case DIAMOND_BLOCK -> hasDiamondBlock = true;
            }
        }

        return hasTnt && hasObsidian && hasEchoShard && hasDiamondBlock;
    }

    /**
     * Returns the recipe for the Excavation Charge upgrade recipe.
     * Since we use custom validation, this returns an empty map.
     *
     * @return the recipe for the Excavation Charge upgrade recipe.
     */
    @Override
    public Map<PedestalLocation, ItemStack> getRecipe() {
        return Map.of();
    }
}
