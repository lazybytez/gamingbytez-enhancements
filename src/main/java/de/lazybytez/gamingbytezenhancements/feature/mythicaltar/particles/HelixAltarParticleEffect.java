package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.particles;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.*;
import org.bukkit.entity.ItemFrame;
import org.bukkit.plugin.Plugin;

/**
 * This class represents a HelixAltarParticleEffect.
 * A HelixAltarParticleEffect is responsible for executing a particle effect that
 * draws a dense helix starting from the bottom and spiraling 3 blocks into the air.
 * The helix has a radius of approximately 4 blocks.
 * After the helix animation completes, a dying wither sound is played.
 * Finally, the action associated with the particle effect is executed.
 */
public class HelixAltarParticleEffect implements AltarParticleEffectInterface {
    private final Plugin plugin;
    private final Color color;
    private final double particleSpacing;
    private final double helixRadius;
    private final double helixHeight;
    private final int rotations;

    /**
     * Constructs a new HelixAltarParticleEffect with the given plugin and color.
     *
     * @param plugin The plugin instance.
     * @param color  The color of the particles.
     */
    public HelixAltarParticleEffect(Plugin plugin, Color color) {
        this(plugin, color, 0.1, 4.0, 3.0, 3);
    }

    /**
     * Constructs a new HelixAltarParticleEffect with the given plugin, color,
     * particle spacing, radius, height, and number of rotations.
     *
     * @param plugin          The plugin instance.
     * @param color           The color of the particles.
     * @param particleSpacing The distance between particles (default: 0.1).
     * @param helixRadius     The radius of the helix (default: 4.0).
     * @param helixHeight     The height of the helix (default: 3.0).
     * @param rotations       The number of helix rotations (default: 3).
     */
    public HelixAltarParticleEffect(
            Plugin plugin,
            Color color,
            double particleSpacing,
            double helixRadius,
            double helixHeight,
            int rotations
    ) {
        this.plugin = plugin;
        this.color = color;
        this.particleSpacing = particleSpacing;
        this.helixRadius = helixRadius;
        this.helixHeight = helixHeight;
        this.rotations = rotations;
    }

    /**
     * Executes the particle effect on the given Altar.
     * The particle effect draws a dense helix from the bottom spiraling 3 blocks upward.
     * After the helix animation completes, a dying wither sound is played.
     * Finally, the action associated with the particle effect is executed.
     *
     * @param altar  The altar on which to execute the particle effect.
     * @param event  The event that triggered the particle effect.
     * @param action The action to execute after the particle effect.
     */
    @Override
    public void executeParticleEffect(
            AltarInterface altar,
            PlayerItemFrameChangeEvent event,
            AltarParticleActionWrapper action
    ) {
        ItemFrame centerPedestal = altar.getPedestal(PedestalLocation.CENTER);
        if (centerPedestal == null) {
            return;
        }

        Location centerLocation = altar.getLocation();
        World world = centerLocation.getWorld();

        // Calculate the starting position (bottom of the helix)
        double startX = centerLocation.getX();
        double startY = centerLocation.getY();
        double startZ = centerLocation.getZ();

        // Total angle to rotate (rotations * 2π)
        double totalAngle = rotations * 2 * Math.PI;

        // Calculate number of steps for a dense helix
        int steps = (int) (helixHeight / particleSpacing);

        // Number of animation frames
        int animationFrames = 60;
        int frameDelay = 2; // ticks between frames

        for (int frame = 0; frame < animationFrames; frame++) {
            int currentFrame = frame;

            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                // Calculate how much of the helix to draw in this frame
                double progress = (double) (currentFrame + 1) / animationFrames;
                int particlesToDraw = (int) (steps * progress);

                for (int i = 0; i < particlesToDraw; i++) {
                    double heightProgress = (double) i / steps;
                    double angle = heightProgress * totalAngle;

                    // Calculate position on helix
                    double x = startX + helixRadius * Math.cos(angle);
                    double y = startY + heightProgress * helixHeight;
                    double z = startZ + helixRadius * Math.sin(angle);

                    world.spawnParticle(
                            Particle.DUST,
                            x,
                            y,
                            z,
                            1,
                            new Particle.DustOptions(this.color, 1.0F)
                    );
                }

                // Play sound periodically during animation
                if (currentFrame % 10 == 0) {
                    world.playSound(
                            centerLocation,
                            Sound.BLOCK_NOTE_BLOCK_CHIME,
                            1.0f,
                            0.5f + (float) progress * 1.5f
                    );
                }
            }, frame * frameDelay);
        }

        // Schedule the final action after animation completes
        Bukkit.getScheduler().scheduleSyncDelayedTask(
                this.plugin,
                () -> {
                    // Play dying wither sound
                    world.playSound(
                            centerLocation,
                            Sound.ENTITY_WITHER_DEATH,
                            1.0f,
                            1.0f
                    );

                    // Execute the action
                    action.onRecipeComplete(plugin, altar, event);
                }, animationFrames * frameDelay + 10
        );
    }
}
