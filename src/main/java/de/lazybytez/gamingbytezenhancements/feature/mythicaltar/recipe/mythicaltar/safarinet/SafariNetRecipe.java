package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.safarinet;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.MythicAltar;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.safarinet.SafariNetManager;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.particles.HelixAltarParticleEffect;
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
 * This class represents the Safari Net recipe.
 * The Safari Net allows players to catch and transport entities using the {@link MythicAltar}.
 */
public class SafariNetRecipe extends AbstractAltarRecipe {

    private final MythicAltarFeature mythicAltarFeature;

    /**
     * Constructs a new Safari Net recipe.
     */
    public SafariNetRecipe(MythicAltarFeature mythicAltarFeature) {
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
        HelixAltarParticleEffect effect = new HelixAltarParticleEffect(
                plugin,
                Color.AQUA,
                0.025,
                2.5,
                4.0,
                3
        );

        effect.executeParticleEffect(
                altar,
                event,
                (effectPlugin, effectAltar, effectEvent) -> {
                    ItemFrame pedestal = effectAltar.getPedestal(PedestalLocation.CENTER);
                    ItemStack safariNet = this.mythicAltarFeature
                            .getCustomItemManagerRegistry()
                            .getCustomItemManager(SafariNetManager.class)
                            .createCustomItem();

                    effectAltar.getLocation().getWorld().dropItem(pedestal.getLocation(), safariNet);

                    removeLock.run();
                });
    }

    /**
     * Validates whether the given altar currently matches the recipe.
     * Recipe requires: Snowball in center, golden apple, diamond, redstone block, and phantom membrane in any outer pedestal positions.
     *
     * @param altar The altar to validate.
     * @return whether the given altar currently matches the recipe.
     */
    @Override
    public boolean validateAltarState(AltarInterface altar) {
        if (altar.getPedestal(PedestalLocation.CENTER).getItem().getType() != Material.SNOWBALL) {
            return false;
        }

        List<PedestalLocation> outerPedestals = List.of(
                PedestalLocation.NORTH_WEST,
                PedestalLocation.SOUTH_WEST,
                PedestalLocation.NORTH_EAST,
                PedestalLocation.SOUTH_EAST
        );

        boolean hasGoldenApple = false;
        boolean hasDiamond = false;
        boolean hasRedstoneBlock = false;
        boolean hasPhantomMembrane = false;

        for (PedestalLocation location : outerPedestals) {
            Material itemType = altar.getPedestal(location).getItem().getType();

            switch (itemType) {
                case GOLDEN_APPLE -> hasGoldenApple = true;
                case DIAMOND -> hasDiamond = true;
                case REDSTONE_BLOCK -> hasRedstoneBlock = true;
                case PHANTOM_MEMBRANE -> hasPhantomMembrane = true;
            }
        }

        return hasGoldenApple && hasDiamond && hasRedstoneBlock && hasPhantomMembrane;
    }

    /**
     * Returns the recipe for the Safari Net.
     * Since we use custom validation, this returns an empty map.
     *
     * @return the recipe for the Safari Net.
     */
    @Override
    public Map<PedestalLocation, ItemStack> getRecipe() {
        return Map.of();
    }
}
