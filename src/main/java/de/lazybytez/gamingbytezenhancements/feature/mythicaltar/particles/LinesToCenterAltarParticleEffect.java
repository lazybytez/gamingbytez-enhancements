package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.particles;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.*;
import org.bukkit.entity.ItemFrame;
import org.bukkit.plugin.Plugin;

/**
 * This class represents a LinesToCenterAltarParticleEffect.
 * A LinesToCenterAltarParticleEffect is responsible for executing a particle effect that
 * draws lines from the pedestals to the center of the altar.
 * The particle effect is executed in a delayed manner, with the delay increasing for each line drawn.
 * After all lines have been drawn, a lightning effect is executed at the location of the altar.
 * Finally, the action associated with the particle effect is executed.
 */
public class LinesToCenterAltarParticleEffect implements AltarParticleEffectInterface {
    private final Plugin plugin;
    private final Color color;
    private final double distance = 0.1;

    /**
     * Constructs a new LinesToCenterAltarParticleEffect with the given plugin and color.
     *
     * @param plugin The plugin instance.
     * @param color The color of the particles.
     */
    public LinesToCenterAltarParticleEffect(Plugin plugin, Color color) {
        this.plugin = plugin;
        this.color = color;
    }

    /**
     * Executes the particle effect on the given Altar.
     * The particle effect draws lines from the pedestals to the center of the altar.
     * After all lines have been drawn, a lightning effect is executed at the location of the altar.
     * Finally, the action associated with the particle effect is executed.
     *
     * @param altar The altar on which to execute the particle effect.
     * @param event The event that triggered the particle effect.
     * @param action The action to execute after the particle effect.
     */
    @Override
    public void executeParticleEffect(
            AltarInterface altar,
            PlayerItemFrameChangeEvent event,
            AltarParticleActionWrapper action
    ) {
        for (int i = 0; i < 100; i += 20) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                ItemFrame centerPedestal = altar.getPedestal(PedestalLocation.CENTER);
                if (centerPedestal == null) {
                    return;
                }

                Location centerPedestalLocation = centerPedestal.getLocation();

                altar.getPedestals().forEach((location, pedestal) -> {
                    Location pedestalLocation = pedestal.getLocation();

                    double x = centerPedestalLocation.getX() - pedestalLocation.getX();
                    double y = centerPedestalLocation.getY() - pedestalLocation.getY();
                    double z = centerPedestalLocation.getZ() - pedestalLocation.getZ();

                    double distance = Math.sqrt(x * x + y * y + z * z);

                    double xDistance = x / distance;
                    double yDistance = y / distance;
                    double zDistance = z / distance;

                    for (double j = 0; j < distance; j += this.distance) {
                        double xPosition = pedestalLocation.getX() + xDistance * j;
                        double yPosition = pedestalLocation.getY() + yDistance * j;
                        double zPosition = pedestalLocation.getZ() + zDistance * j;

                        centerPedestalLocation.getWorld().spawnParticle(
                                Particle.REDSTONE,
                                xPosition,
                                yPosition,
                                zPosition,
                                1,
                                new Particle.DustOptions(this.color, 1.0F)
                        );
                    }
                    centerPedestalLocation.getWorld().playSound(
                            centerPedestalLocation,
                            Sound.BLOCK_NOTE_BLOCK_HARP,
                            1,
                            1
                    );
                });
            }, i);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(
                this.plugin,
                () -> {
                    altar.getLocation().getWorld().strikeLightningEffect(altar.getLocation());
                    altar.getLocation().getWorld().strikeLightningEffect(altar.getLocation());
                    altar.getLocation().getWorld().strikeLightningEffect(altar.getLocation());
                    altar.getLocation().getWorld().strikeLightningEffect(altar.getLocation());
                    action.onRecipeComplete(plugin, altar, event);
                }, 100
        );
    }
}
