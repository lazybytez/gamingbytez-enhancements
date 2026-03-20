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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.particles;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.*;
import org.bukkit.entity.ItemFrame;
import org.bukkit.plugin.Plugin;

/**
 * This class represents a DoubleHelixAltarParticleEffect.
 * A DoubleHelixAltarParticleEffect is responsible for executing a particle effect that
 * draws two helices.
 * The double helix starts from the bottom and spirals upward.
 * After the animation completes, a dying wither sound is played.
 * Finally, the action associated with the particle effect is executed.
 */
public class DoubleHelixAltarParticleEffect implements AltarParticleEffectInterface {
    private final Plugin plugin;
    private final Color color;
    private final double particleSpacing;
    private final double helixRadius;
    private final double helixHeight;
    private final int rotations;
    private final int animationDurationTicks;

    /**
     * Constructs a new DoubleHelixAltarParticleEffect with default parameters.
     *
     * @param plugin The plugin instance.
     * @param color  The color of the particles.
     */
    public DoubleHelixAltarParticleEffect(Plugin plugin, Color color) {
        this(plugin, color, 0.05, 2.5, 5, 3, 50);
    }

    /**
     * Constructs a new DoubleHelixAltarParticleEffect with custom parameters.
     *
     * @param plugin                 The plugin instance.
     * @param color                  The color of the particles.
     * @param particleSpacing        Distance between particles (smaller = denser).
     * @param helixRadius            Radius of the helix in blocks.
     * @param helixHeight            Height of the helix in blocks.
     * @param rotations              Number of complete rotations.
     * @param animationDurationTicks Duration of the animation in ticks.
     */
    public DoubleHelixAltarParticleEffect(Plugin plugin, Color color, double particleSpacing,
                                          double helixRadius, double helixHeight, int rotations,
                                          int animationDurationTicks) {
        this.plugin = plugin;
        this.color = color;
        this.particleSpacing = particleSpacing;
        this.helixRadius = helixRadius;
        this.helixHeight = helixHeight;
        this.rotations = rotations;
        this.animationDurationTicks = animationDurationTicks;
    }

    /**
     * Executes the particle effect on the given Altar.
     * The particle effect draws two intertwined helices like a DNA structure
     * with overlapping connectors rotating in opposite directions.
     * After the animation completes, a dying wither sound is played.
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

        // Number of animation frames (1 frame per tick for smooth animation)
        int animationFrames = animationDurationTicks;
        int frameDelay = 1; // 1 tick between frames for smooth animation

        for (int frame = 0; frame < animationFrames; frame++) {
            int currentFrame = frame;

            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                // Calculate how much of the helix to draw in this frame
                double progress = (double) (currentFrame + 1) / animationFrames;
                int particlesToDraw = (int) (steps * progress);

                for (int i = 0; i < particlesToDraw; i++) {
                    double heightProgress = (double) i / steps;
                    double angle = heightProgress * totalAngle;

                    // First helix strand (rotating clockwise)
                    double x1 = startX + helixRadius * Math.cos(angle);
                    double y = startY + heightProgress * helixHeight;
                    double z1 = startZ + helixRadius * Math.sin(angle);

                    world.spawnParticle(
                            Particle.DUST,
                            x1,
                            y,
                            z1,
                            1,
                            new Particle.DustOptions(this.color, 1.0F)
                    );

                    // Second helix strand (rotating counter-clockwise)
                    double x2 = startX + helixRadius * Math.cos(angle + Math.PI);
                    double z2 = startZ + helixRadius * Math.sin(angle + Math.PI);

                    world.spawnParticle(
                            Particle.DUST,
                            x2,
                            y,
                            z2,
                            1,
                            new Particle.DustOptions(this.color, 1.0F)
                    );

                    // Connecting line between the two strands for a true DNA-like structure
                    if (i % 10 == 0) {
                        int connectionParticles = 6;  // Number of particles in the connecting line
                        for (int j = 0; j < connectionParticles; j++) {
                            double t = (double) j / (connectionParticles - 1); // Interpolation factor
                            double connectorX = x1 + t * (x2 - x1);
                            double connectorZ = z1 + t * (z2 - z1);

                            world.spawnParticle(
                                    Particle.DUST,
                                    connectorX,
                                    y,
                                    connectorZ,
                                    1,
                                    new Particle.DustOptions(this.color, 0.5F)
                            );
                        }
                    }
                }

                // Play sound periodically during animation
                if (currentFrame % 10 == 0) {
                    world.playSound(
                            centerLocation,
                            Sound.BLOCK_NOTE_BLOCK_CHIME,
                            0.8f,
                            0.8f + (float) progress * 1.2f
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
