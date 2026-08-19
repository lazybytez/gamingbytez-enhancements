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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Counts a placed Excavation Charge down to its detonation and shows the countdown while it runs.
 * <p>
 * A charge burns at most one fuse at a time. The identities of the charges already counting down
 * are held here, so a second hit on a burning charge is ignored instead of restarting the countdown
 * or stacking a second one on top of it.
 */
public final class ExcavationChargeFuse {

    /**
     * The countdown of a charge a player set off, long enough to walk away from the blast.
     */
    static final long PLAYER_FUSE_TICKS = 100L;

    /**
     * The countdown of a charge woken by a neighbouring blast, short enough to read as one cascade.
     */
    static final long CHAIN_FUSE_TICKS = 10L;

    private static final long OUTLINE_TICKS = 100L;
    private static final long OUTLINE_INTERVAL_TICKS = 2L;
    private static final float OUTLINE_DUST_SIZE = 1.1f;
    private static final float EDGE_DUST_SIZE = 2.0f;
    private static final int MAX_OUTLINE_SAMPLES = 450;
    private static final int MAX_EDGE_SAMPLES = 500;
    private static final long TICK_DELAY = 1L;
    private static final long TICK_PERIOD = 1L;
    private static final long COLUMN_CYCLE_TICKS = 20L;
    private static final double COLUMN_HEIGHT = 2.5;
    private static final long SOUND_DIVISOR = 8L;
    private static final float MIN_CHARGE_PITCH = 0.7f;
    private static final float MAX_CHARGE_PITCH = 2.0f;
    private static final double BLOCK_CENTRE_OFFSET = 0.5;

    private static final List<BlastVector> NEIGHBOUR_STEPS = List.of(
            new BlastVector(1, 0, 0), new BlastVector(-1, 0, 0),
            new BlastVector(0, 1, 0), new BlastVector(0, -1, 0),
            new BlastVector(0, 0, 1), new BlastVector(0, 0, -1));

    private final EnhancementsPlugin plugin;
    private final ExcavationChargeDetonator detonator;
    private final Set<UUID> burning;

    /**
     * Creates a fuse arming charges on behalf of the given plugin.
     *
     * @param plugin    The plugin owning the countdown tasks
     * @param detonator The detonator the countdown hands the charge to
     */
    public ExcavationChargeFuse(EnhancementsPlugin plugin, ExcavationChargeDetonator detonator) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null");
        this.detonator = Objects.requireNonNull(detonator, "detonator must not be null");
        this.burning = new HashSet<>();
    }

    /**
     * Starts the countdown of a charge a player set off and opens a cascade for it.
     *
     * @param charge The placed charge that was hit
     * @return true when the countdown was started, false when the charge was already burning
     */
    public boolean armForPlayer(EnderCrystal charge) {
        Objects.requireNonNull(charge, "charge must not be null");

        return this.arm(charge, ExcavationChargeFuse.PLAYER_FUSE_TICKS, new ChainSession(charge.getUniqueId()));
    }

    /**
     * Starts the countdown of a charge.
     *
     * @param charge    The placed charge to count down
     * @param fuseTicks The number of ticks until the charge detonates
     * @param session   The shared state of the cascade the charge belongs to
     * @return true when the countdown was started, false when the charge was already burning
     */
    public boolean arm(EnderCrystal charge, long fuseTicks, ChainSession session) {
        Objects.requireNonNull(charge, "charge must not be null");
        Objects.requireNonNull(session, "session must not be null");

        if (!this.burning.add(charge.getUniqueId())) {
            return false;
        }

        new FuseTask(charge, fuseTicks, session)
                .runTaskTimer(this.plugin, ExcavationChargeFuse.TICK_DELAY, ExcavationChargeFuse.TICK_PERIOD);

        return true;
    }

    /**
     * Detonates the charge and starts the countdown of every charge its blast wakes.
     * <p>
     * A detonation that throws is logged and dropped: every charge of a cascade counts down on its
     * own task, so one failure costs a single charge instead of stranding the rest of the chain.
     *
     * @param charge  The charge whose countdown ran out
     * @param session The shared state of the cascade the charge belongs to
     */
    private void fire(EnderCrystal charge, ChainSession session) {
        List<EnderCrystal> woken;

        try {
            woken = this.detonator.detonate(charge, session);
        } catch (RuntimeException e) {
            this.plugin.getLogger().log(
                    Level.SEVERE, "Failed to detonate Excavation Charge " + charge.getUniqueId(), e);

            return;
        }

        for (EnderCrystal chained : woken) {
            if (this.arm(chained, ExcavationChargeFuse.CHAIN_FUSE_TICKS, session)) {
                continue;
            }

            session.release(chained.getUniqueId());
        }
    }

    /**
     * Returns the offsets on the surface of the volume, thinned down to the given sample count.
     * <p>
     * An offset belongs to the surface when at least one of its six axis neighbours lies outside the
     * volume. Drawing every one of them at level four would cost thousands of particles a tick, so
     * the surface is sampled at a fixed stride instead of rendered in full.
     *
     * @param geometry   The volume to trace
     * @param maxSamples The largest number of offsets to return
     * @return The sampled surface offsets, empty when no sample is allowed
     */
    static List<BlastVector> outlineOffsets(BlastGeometry geometry, int maxSamples) {
        Objects.requireNonNull(geometry, "geometry must not be null");

        if (maxSamples <= 0) {
            return List.of();
        }

        List<BlastVector> surface = geometry.offsets()
                .filter(offset -> ExcavationChargeFuse.exposedAxes(geometry, offset) >= 1)
                .toList();

        return ExcavationChargeFuse.thinDown(surface, maxSamples);
    }

    /**
     * Returns the offsets on the edges of the volume, thinned down to the given sample count.
     * <p>
     * An offset belongs to an edge when at least two of its six axis neighbours lie outside the
     * volume: the rims of a cuboid, the mouth of a tunnel, the curvature bands of a ball. Drawing
     * these on top of the sampled surface is what makes the boundary of the blast readable at a
     * glance instead of a loose cloud.
     *
     * @param geometry   The volume to trace
     * @param maxSamples The largest number of offsets to return
     * @return The sampled edge offsets, empty when no sample is allowed
     */
    static List<BlastVector> edgeOffsets(BlastGeometry geometry, int maxSamples) {
        Objects.requireNonNull(geometry, "geometry must not be null");

        if (maxSamples <= 0) {
            return List.of();
        }

        List<BlastVector> edges = geometry.offsets()
                .filter(offset -> ExcavationChargeFuse.exposedAxes(geometry, offset) >= 2)
                .toList();

        return ExcavationChargeFuse.thinDown(edges, maxSamples);
    }

    private static int exposedAxes(BlastGeometry geometry, BlastVector offset) {
        int exposed = 0;

        for (BlastVector step : ExcavationChargeFuse.NEIGHBOUR_STEPS) {
            BlastVector neighbour = new BlastVector(
                    offset.x() + step.x(), offset.y() + step.y(), offset.z() + step.z());

            if (!geometry.contains(neighbour)) {
                exposed++;
            }
        }

        return exposed;
    }

    private static List<BlastVector> thinDown(List<BlastVector> surface, int maxSamples) {
        if (surface.size() <= maxSamples) {
            return surface;
        }

        int stride = (surface.size() + maxSamples - 1) / maxSamples;
        List<BlastVector> sampled = new ArrayList<>(maxSamples);

        for (int index = 0; index < surface.size(); index += stride) {
            sampled.add(surface.get(index));
        }

        return List.copyOf(sampled);
    }

    /**
     * The repeating task counting one charge down and showing its countdown.
     */
    private final class FuseTask extends BukkitRunnable {

        private final EnderCrystal charge;
        private final long fuseTicks;
        private final ChainSession session;

        private long elapsed;
        private List<BlastVector> outline;
        private List<BlastVector> edges;
        private Particle.DustOptions fillDust;
        private Particle.DustOptions edgeDust;

        private FuseTask(EnderCrystal charge, long fuseTicks, ChainSession session) {
            this.charge = charge;
            this.fuseTicks = fuseTicks;
            this.session = session;
        }

        @Override
        public void run() {
            if (!this.charge.isValid()) {
                this.stop();

                return;
            }

            this.elapsed++;
            long remaining = this.fuseTicks - this.elapsed;

            if (remaining <= 0) {
                this.stop();
                ExcavationChargeFuse.this.fire(this.charge, this.session);

                return;
            }

            this.playChargeSound(remaining);
            this.drawColumn();
            this.drawOutline(remaining);
        }

        private void stop() {
            this.cancel();
            ExcavationChargeFuse.this.burning.remove(this.charge.getUniqueId());
        }

        private void playChargeSound(long remaining) {
            long interval = Math.max(1L, remaining / ExcavationChargeFuse.SOUND_DIVISOR);

            if (this.elapsed % interval != 0) {
                return;
            }

            float progress = (float) this.elapsed / this.fuseTicks;
            float pitch = ExcavationChargeFuse.MIN_CHARGE_PITCH
                    + (ExcavationChargeFuse.MAX_CHARGE_PITCH - ExcavationChargeFuse.MIN_CHARGE_PITCH) * progress;

            this.charge.getWorld().playSound(
                    this.charge.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, pitch);
        }

        /**
         * Draws the rising column above the charge in the colour of its level.
         * <p>
         * The colour is the same one the outline and the detonation burst carry, so everything a
         * player sees of one charge speaks the same severity from green to red.
         */
        private void drawColumn() {
            double height = ExcavationChargeFuse.COLUMN_HEIGHT
                    * (this.elapsed % ExcavationChargeFuse.COLUMN_CYCLE_TICKS) / ExcavationChargeFuse.COLUMN_CYCLE_TICKS;
            Location column = this.charge.getLocation().add(0.0, height, 0.0);

            this.charge.getWorld().spawnParticle(Particle.DUST, column, 3, 0.15, 0.05, 0.15, 0.0, this.fillDust());
            this.charge.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, column, 1, 0.1, 0.0, 0.1, 0.0);
        }

        /**
         * Draws the volume about to be carved: a dense sampled fill of the surface, and the edges
         * on top in larger dust so the boundary reads as a line rather than a cloud.
         *
         * @param remaining The ticks left on the fuse.
         */
        private void drawOutline(long remaining) {
            if (remaining > ExcavationChargeFuse.OUTLINE_TICKS) {
                return;
            }

            if (remaining % ExcavationChargeFuse.OUTLINE_INTERVAL_TICKS != 0) {
                return;
            }

            this.drawOffsets(this.outline(), this.fillDust());
            this.drawOffsets(this.edges(), this.edgeDust());
        }

        private void drawOffsets(List<BlastVector> offsets, Particle.DustOptions dust) {
            Location centre = this.charge.getLocation();
            World world = this.charge.getWorld();

            for (BlastVector offset : offsets) {
                world.spawnParticle(
                        Particle.DUST,
                        new Location(
                                world,
                                centre.getBlockX() + offset.x() + ExcavationChargeFuse.BLOCK_CENTRE_OFFSET,
                                centre.getBlockY() + offset.y() + ExcavationChargeFuse.BLOCK_CENTRE_OFFSET,
                                centre.getBlockZ() + offset.z() + ExcavationChargeFuse.BLOCK_CENTRE_OFFSET),
                        1, 0.0, 0.0, 0.0, 0.0, dust);
            }
        }

        private Particle.DustOptions fillDust() {
            if (this.fillDust == null) {
                this.fillDust = new Particle.DustOptions(
                        this.armingColour(), ExcavationChargeFuse.OUTLINE_DUST_SIZE);
            }

            return this.fillDust;
        }

        private Particle.DustOptions edgeDust() {
            if (this.edgeDust == null) {
                this.edgeDust = new Particle.DustOptions(
                        this.armingColour(), ExcavationChargeFuse.EDGE_DUST_SIZE);
            }

            return this.edgeDust;
        }

        private Color armingColour() {
            return Color.fromRGB(
                    ExcavationChargeFuse.this.detonator.levelOf(this.charge).getArmingColour());
        }

        private List<BlastVector> outline() {
            if (this.outline == null) {
                this.outline = ExcavationChargeFuse.outlineOffsets(
                        ExcavationChargeFuse.this.detonator.geometryOf(this.charge),
                        ExcavationChargeFuse.MAX_OUTLINE_SAMPLES);
            }

            return this.outline;
        }

        private List<BlastVector> edges() {
            if (this.edges == null) {
                this.edges = ExcavationChargeFuse.edgeOffsets(
                        ExcavationChargeFuse.this.detonator.geometryOf(this.charge),
                        ExcavationChargeFuse.MAX_EDGE_SAMPLES);
            }

            return this.edges;
        }
    }
}
