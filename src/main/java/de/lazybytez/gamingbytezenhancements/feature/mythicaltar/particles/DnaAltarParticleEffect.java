package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.particles;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.*;
import org.bukkit.entity.ItemFrame;
import org.bukkit.plugin.Plugin;

/**
 * This class represents a DNAltarParticleEffect.
 * A DNAAltarParticleEffect is responsible for executing a particle effect that
 * draws two intertwined helices (like DNA) rotating in opposite directions.
 * The double helix starts from the bottom and spirals upward.
 * After the animation completes, a dying wither sound is played.
 * Finally, the action associated with the particle effect is executed.
 */
public class DnaAltarParticleEffect implements AltarParticleEffectInterface {
    private final Plugin plugin;
    private final Color color1;
    private final Color color2;
    private final double particleSpacing;
    private final double helixRadius;
    private final double helixHeight;
    private final int rotations;
    private final int animationDurationTicks;

    /**
     * Constructs a new DNA AltarParticleEffect with default configurations.
     *
     * @param plugin The plugin instance.
     * @param color1 The primary color representing the first strand.
     * @param color2 The secondary color representing the second strand.
     */
    public DnaAltarParticleEffect(Plugin plugin, Color color1, Color color2) {
        this(plugin, color1, color2, 0.1, 2.5, 5.0, 3, 120);
    }

    /**
     * Constructs a new DNA AltarParticleEffect with customizable configurations.
     *
     * @param plugin                 The plugin instance.
     * @param color1                 The primary color representing the first strand.
     * @param color2                 The secondary color representing the second strand.
     * @param particleSpacing        Distance between particles per step.
     * @param helixRadius            Radius of each helix.
     * @param helixHeight            Total height of the DNA structure in blocks.
     * @param rotations              Number of complete helix rotations.
     * @param animationDurationTicks Duration of animation in ticks.
     */
    public DnaAltarParticleEffect(
            Plugin plugin,
            Color color1,
            Color color2,
            double particleSpacing,
            double helixRadius,
            double helixHeight,
            int rotations,
            int animationDurationTicks
    ) {
        this.plugin = plugin;
        this.color1 = color1;
        this.color2 = color2;
        this.particleSpacing = particleSpacing;
        this.helixRadius = helixRadius;
        this.helixHeight = helixHeight;
        this.rotations = rotations;
        this.animationDurationTicks = animationDurationTicks;
    }

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

        double startX = centerLocation.getX();
        double startY = centerLocation.getY();
        double startZ = centerLocation.getZ();

        double totalAngle = rotations * 2 * Math.PI;
        int steps = (int) (helixHeight / particleSpacing);
        int animationFrames = animationDurationTicks;
        int frameDelay = 1;

        for (int frame = 0; frame < animationFrames; frame++) {
            int currentFrame = frame;

            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                double progress = (double) (currentFrame + 1) / animationFrames;
                int particlesToDraw = (int) (steps * progress);

                for (int i = 0; i < particlesToDraw; i++) {
                    double heightProgress = (double) i / steps;

                    // First strand rotates clockwise
                    double angle1 = heightProgress * totalAngle;
                    // Second strand rotates counter-clockwise (negative angle)
                    double angle2 = -heightProgress * totalAngle;

                    double y = startY + heightProgress * helixHeight;

                    // First helix strand (clockwise rotation)
                    double x1 = startX + helixRadius * Math.cos(angle1);
                    double z1 = startZ + helixRadius * Math.sin(angle1);

                    world.spawnParticle(
                            Particle.DUST,
                            x1,
                            y,
                            z1,
                            1,
                            new Particle.DustOptions(this.color1, 1.0F)
                    );

                    // Second helix strand (counter-clockwise rotation)
                    double x2 = startX + helixRadius * Math.cos(angle2);
                    double z2 = startZ + helixRadius * Math.sin(angle2);

                    world.spawnParticle(
                            Particle.DUST,
                            x2,
                            y,
                            z2,
                            1,
                            new Particle.DustOptions(this.color2, 1.0F)
                    );
                }

                // Play sound during animation
                if (currentFrame % 15 == 0) {
                    world.playSound(
                            centerLocation,
                            Sound.BLOCK_NOTE_BLOCK_CHIME,
                            0.8f,
                            1.0f + (float) progress * 0.5f
                    );
                }
            }, frame * frameDelay);
        }

        // Schedule the final action after animation completes
        Bukkit.getScheduler().scheduleSyncDelayedTask(
                this.plugin,
                () -> {
                    world.playSound(
                            centerLocation,
                            Sound.ENTITY_WITHER_DEATH,
                            1.0f,
                            1.0f
                    );

                    action.onRecipeComplete(plugin, altar, event);
                }, animationFrames * frameDelay + 10
        );
    }
}