package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.magicxpbottle;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.MythicAltar;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle.ExperienceGemManager;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle.MagicXpBottleManager;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.particles.DnaAltarParticleEffect;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.particles.DoubleHelixAltarParticleEffect;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.AbstractAltarRecipe;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FillMagicXpBottleRecipe extends AbstractAltarRecipe {

    private final MythicAltarFeature mythicAltarFeature;

    public FillMagicXpBottleRecipe(MythicAltarFeature mythicAltarFeature) {
        super(MythicAltar.class, true);

        this.mythicAltarFeature = mythicAltarFeature;
    }

    @Override
    public void onRecipeComplete(Plugin plugin, AltarInterface altar, PlayerItemFrameChangeEvent event, Runnable removeLock) {
        MagicXpBottleManager magicXpBottleManager = this.mythicAltarFeature
                .getCustomItemManagerRegistry()
                .getCustomItemManager(MagicXpBottleManager.class);

        ItemFrame pedestal = altar.getPedestal(PedestalLocation.CENTER);
        ItemStack originalMagicXPBottle = pedestal.getItem();

        if (!magicXpBottleManager.isCustomItem(originalMagicXPBottle)) {
            originalMagicXPBottle = magicXpBottleManager.createCustomItem();
        }

        int originalExperience = magicXpBottleManager.getExperience(originalMagicXPBottle);

        DnaAltarParticleEffect effect = new DnaAltarParticleEffect(
                plugin,
                Color.LIME,
                Color.GREEN,
                0.025,
                2,
                10,
                3,
                160);
        effect.executeParticleEffect(
                altar,
                event,
                (effectPlugin, effectAltar, effectEvent) -> {
                    World effectWorld = effectAltar.getLocation().getWorld();

                    // First generate new XP bottle
                    ItemStack newMagicXpBottle = magicXpBottleManager.createCustomItem();
                    magicXpBottleManager.addExperience(
                            newMagicXpBottle,
                            originalExperience + event.getPlayer().calculateTotalExperiencePoints()
                    );

                    // Then do actual changes
                    event.getPlayer().setExp(0.0f);
                    event.getPlayer().setLevel(0);
                    event.getPlayer().playSound(
                            event.getPlayer().getLocation(),
                            Sound.ENTITY_PLAYER_LEVELUP,
                            1.0f,
                            1.0f
                    );

                    effectWorld.dropItem(pedestal.getLocation(), newMagicXpBottle);

                    removeLock.run();
                });
    }

    @Override
    public boolean validateAltarState(AltarInterface altar) {
        MagicXpBottleManager magicXpBottleManager = this.mythicAltarFeature
                .getCustomItemManagerRegistry()
                .getCustomItemManager(MagicXpBottleManager.class);

        if (!magicXpBottleManager.isCustomItem(altar.getPedestal(PedestalLocation.CENTER).getItem())) {
            return false;
        }

        List<PedestalLocation> outerPedestals = List.of(
                PedestalLocation.NORTH_WEST,
                PedestalLocation.SOUTH_WEST,
                PedestalLocation.NORTH_EAST,
                PedestalLocation.SOUTH_EAST
        );

        List<PedestalLocation> pedestalWithLapis = new ArrayList<>();
        List<PedestalLocation> pedestalWithEyeOfEnder = new ArrayList<>();

        ExperienceGemManager experienceGemManager = this.mythicAltarFeature
                .getCustomItemManagerRegistry()
                .getCustomItemManager(ExperienceGemManager.class);
        for (PedestalLocation location : outerPedestals) {
            if (altar.getPedestal(location).getItem().getType().equals(Material.LAPIS_LAZULI)) {
                pedestalWithLapis.add(location);
            }

            if (altar.getPedestal(location).getItem().getType().equals(Material.ENDER_EYE)) {
                pedestalWithEyeOfEnder.add(location);
            }
        }

        if (pedestalWithEyeOfEnder.size() != 2 || pedestalWithLapis.size() != 2) {
            return false;
        }

        boolean netheriteOnNESW = pedestalWithEyeOfEnder.contains(PedestalLocation.NORTH_EAST)
                && pedestalWithEyeOfEnder.contains(PedestalLocation.SOUTH_WEST);
        boolean netheriteOnNWSE = pedestalWithEyeOfEnder.contains(PedestalLocation.NORTH_WEST)
                && pedestalWithEyeOfEnder.contains(PedestalLocation.SOUTH_EAST);

        boolean essenceOnNESW = pedestalWithLapis.contains(PedestalLocation.NORTH_EAST)
                && pedestalWithLapis.contains(PedestalLocation.SOUTH_WEST);
        boolean essenceOnNWSE = pedestalWithLapis.contains(PedestalLocation.NORTH_WEST)
                && pedestalWithLapis.contains(PedestalLocation.SOUTH_EAST);

        return (netheriteOnNESW && essenceOnNWSE) || (netheriteOnNWSE && essenceOnNESW);
    }

    @Override
    public Map<PedestalLocation, ItemStack> getRecipe() {
        // Standard mechanism is not used for this recipe due to custom items.
        return Map.of();
    }
}
