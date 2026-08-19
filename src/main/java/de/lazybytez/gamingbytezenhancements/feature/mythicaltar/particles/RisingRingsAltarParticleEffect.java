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
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

/**
 * A particle effect drawing rings that rise from the altar and tighten as they climb.
 * <p>
 * Each ring starts wide at the altar floor and closes towards the centre as it lifts, which reads
 * as something being drawn upwards and concentrated rather than dispersed. That is the reason it
 * belongs to an upgrade: the shape of the animation says the item is being made denser.
 * <p>
 * A chime rises in pitch with every ring, and the last ring hands over to the recipe action.
 */
public class RisingRingsAltarParticleEffect implements AltarParticleEffectInterface {

    /**
     * The sounds of the animation, named rather than taken from {@code Sound}.
     * <p>
     * The sound registry is only populated on a running server, so referring to a constant of it
     * from a class a unit test exercises fails before the test body runs.
     */
    private static final String CHIME_SOUND = "block.amethyst_block.chime";
    private static final String COMPLETE_SOUND = "block.beacon.power_select";

    /**
     * The volume of the chimes and the completion sound.
     * <p>
     * Values above one extend the audible range, so the ritual is heard across the altar area
     * rather than only right next to the pedestal.
     */
    private static final float SOUND_VOLUME = 2.5f;

    private static final int RING_COUNT = 5;
    private static final long RING_INTERVAL_TICKS = 12L;
    private static final int POINTS_PER_RING = 40;
    private static final double START_RADIUS = 3.5;
    private static final double END_RADIUS = 0.6;
    private static final double RING_HEIGHT = 0.6;
    private static final float PARTICLE_SIZE = 1.2f;
    private static final float MIN_CHIME_PITCH = 0.8f;
    private static final float MAX_CHIME_PITCH = 1.8f;

    private final Plugin plugin;
    private final Color color;

    /**
     * Constructs a new RisingRingsAltarParticleEffect with the given plugin and colour.
     *
     * @param plugin The plugin instance.
     * @param color  The colour of the particles.
     */
    public RisingRingsAltarParticleEffect(Plugin plugin, Color color) {
        this.plugin = plugin;
        this.color = color;
    }

    /**
     * Draws the rising rings and runs the given action once the last one has closed.
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
        for (int ring = 0; ring < RisingRingsAltarParticleEffect.RING_COUNT; ring++) {
            int index = ring;

            Bukkit.getScheduler().scheduleSyncDelayedTask(
                    this.plugin,
                    () -> this.drawRing(altar, index),
                    index * RisingRingsAltarParticleEffect.RING_INTERVAL_TICKS);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(
                this.plugin,
                () -> {
                    Location centre = altar.getLocation();

                    centre.getWorld().playSound(
                            centre,
                            RisingRingsAltarParticleEffect.COMPLETE_SOUND,
                            RisingRingsAltarParticleEffect.SOUND_VOLUME,
                            1.4f
                    );
                    action.onRecipeComplete(this.plugin, altar, event);
                },
                RisingRingsAltarParticleEffect.RING_COUNT * RisingRingsAltarParticleEffect.RING_INTERVAL_TICKS);
    }

    /**
     * Draws a single ring of the animation.
     *
     * @param altar The altar the ring is drawn above.
     * @param index The position of the ring in the sequence, counted from zero.
     */
    private void drawRing(AltarInterface altar, int index) {
        Location centre = altar.getLocation();
        World world = centre.getWorld();

        double progress = (double) index / RisingRingsAltarParticleEffect.RING_COUNT;
        double radius = RisingRingsAltarParticleEffect.START_RADIUS
                + (RisingRingsAltarParticleEffect.END_RADIUS - RisingRingsAltarParticleEffect.START_RADIUS) * progress;
        double height = RisingRingsAltarParticleEffect.RING_HEIGHT * index;

        Particle.DustOptions dust =
                new Particle.DustOptions(this.color, RisingRingsAltarParticleEffect.PARTICLE_SIZE);

        for (int point = 0; point < RisingRingsAltarParticleEffect.POINTS_PER_RING; point++) {
            double angle = 2 * Math.PI * point / RisingRingsAltarParticleEffect.POINTS_PER_RING;

            world.spawnParticle(
                    Particle.DUST,
                    centre.getX() + radius * Math.cos(angle),
                    centre.getY() + height,
                    centre.getZ() + radius * Math.sin(angle),
                    1, 0.0, 0.0, 0.0, 0.0, dust);
        }

        float pitch = RisingRingsAltarParticleEffect.MIN_CHIME_PITCH
                + (RisingRingsAltarParticleEffect.MAX_CHIME_PITCH - RisingRingsAltarParticleEffect.MIN_CHIME_PITCH)
                * (float) progress;

        world.playSound(
                centre,
                RisingRingsAltarParticleEffect.CHIME_SOUND,
                RisingRingsAltarParticleEffect.SOUND_VOLUME,
                pitch
        );
    }
}
