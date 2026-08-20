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

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.excavationcharge.PlacedExcavationCharge;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.ExplosionResult;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

/**
 * Detonates a placed Excavation Charge: it announces the blast, damages what stands in it, hands the
 * carving to the {@link BlastScheduler} and reports which neighbouring charges the blast wakes.
 * <p>
 * Waking the neighbours is deliberately left to the caller. This class only resolves them against
 * the running {@link ChainSession}, which keeps the arming of a fuse in one place and lets a
 * detonation stay free of any scheduling of its own.
 */
public final class ExcavationChargeDetonator {

    private static final float EXPLOSION_YIELD = 0.0f;
    private static final double KNOCKBACK_STRENGTH = 1.2;
    private static final double MIN_KNOCKBACK_DISTANCE = 0.1;

    /**
     * The detonation sounds, named rather than taken from {@code Sound}.
     * <p>
     * The sound registry is only populated on a running server, so the class names its sounds and
     * binds to no constant of that registry.
     */
    private static final String BLAST_SOUND = "entity.generic.explode";
    private static final String BLAST_BOOM_SOUND = "entity.dragon_fireball.explode";
    private static final String BLAST_RUMBLE_SOUND = "entity.lightning_bolt.thunder";
    private static final String FIZZLE_SOUND = "block.fire.extinguish";
    private static final int FIZZLE_PARTICLE_COUNT = 30;

    private static final float MIN_BLAST_VOLUME = 4.0f;
    private static final float MAX_BLAST_VOLUME = 10.0f;
    private static final float MIN_BLAST_PITCH = 0.4f;
    private static final float MAX_BLAST_PITCH = 1.0f;

    /**
     * The level from which a detonation carries the low thunder layer.
     * <p>
     * Down pitched thunder under the explosion is what makes a big blast land as an event rather
     * than a pop; the small levels stay a clean explosion without it.
     */
    private static final int RUMBLE_LEVEL = 3;
    private static final float RUMBLE_PITCH = 0.55f;
    private static final int BURST_PARTICLE_COUNT = 40;
    private static final double BURST_SPREAD = 2.0;
    private static final float BURST_DUST_SIZE = 2.0f;

    private final BlastScheduler blastScheduler;
    private final BlastPlanner blastPlanner;
    private final ExcavationChargeAuditLog auditLog;
    private final PlacedExcavationCharge.Keys keys;

    /**
     * Creates a detonator carving through the given scheduler.
     *
     * @param blastScheduler The scheduler owning every block mutation a blast performs
     * @param auditLog       The audit trail every detonation is recorded on
     * @param keys           The keys a placed Excavation Charge stores its state under
     */
    public ExcavationChargeDetonator(
            BlastScheduler blastScheduler,
            ExcavationChargeAuditLog auditLog,
            PlacedExcavationCharge.Keys keys
    ) {
        this.blastScheduler = Objects.requireNonNull(blastScheduler, "blastScheduler must not be null");
        this.blastPlanner = new BlastPlanner(BlastBlockFilter.create());
        this.auditLog = Objects.requireNonNull(auditLog, "auditLog must not be null");
        this.keys = Objects.requireNonNull(keys, "keys must not be null");
    }

    /**
     * Detonates the given charge and returns the charges its blast wakes.
     * <p>
     * A cancelled {@link EntityExplodeEvent} aborts the whole detonation: nothing is carved, nothing
     * is damaged, no neighbour is woken and the charge stays in the world, so another plugin can
     * veto a blast without leaving the charge in a half detonated state.
     * <p>
     * A crystal that lost its marker between arming and firing is left standing for the same
     * reason: without the state it was placed with there is no volume to carve.
     *
     * @param charge  The placed charge going off
     * @param session The shared state of the running cascade
     * @return The charges to wake, empty when the detonation was vetoed
     */
    public List<EnderCrystal> detonate(EnderCrystal charge, ChainSession session) {
        Objects.requireNonNull(charge, "charge must not be null");
        Objects.requireNonNull(session, "session must not be null");

        Optional<PlacedExcavationCharge> placedCharge = PlacedExcavationCharge.of(this.keys, charge);

        if (placedCharge.isEmpty()) {
            return List.of();
        }

        Location detonationPoint = charge.getLocation();
        BlastLevel level = placedCharge.get().level();
        BlastGeometry geometry = placedCharge.get().geometry();

        List<Block> plan = this.blastPlanner.plan(geometry, detonationPoint);

        if (!this.blastScheduler.canAccept(plan.size())) {
            ExcavationChargeDetonator.fizzle(detonationPoint);

            return List.of();
        }

        List<Block> carved = ExcavationChargeDetonator.announce(charge, detonationPoint, plan);

        if (carved == null) {
            return List.of();
        }

        this.auditLog.detonated(placedCharge.get().shape(), level, detonationPoint, carved);
        ExcavationChargeDetonator.announceToSenses(level, detonationPoint);
        this.damageCaughtEntities(geometry, level, detonationPoint);
        this.blastScheduler.submit(new ActiveBlast(
                carved,
                BlastDropTally.create(),
                detonationPoint,
                level.getWaveSpeed()
        ));

        List<EnderCrystal> woken = this.resolveChain(charge, level, geometry, detonationPoint, session);
        charge.remove();

        return woken;
    }

    /**
     * Shows a refused blast fizzling out instead of failing silently.
     * <p>
     * A refusal happens when the queued carving ceiling is reached, so after a full countdown the
     * charge would otherwise just sit there looking broken. The hiss and the smoke say the charge
     * gave up on purpose and can be set off again once the queue has drained.
     *
     * @param detonationPoint The place the charge tried to go off.
     */
    private static void fizzle(Location detonationPoint) {
        World world = detonationPoint.getWorld();

        world.playSound(detonationPoint, ExcavationChargeDetonator.FIZZLE_SOUND, 2.0f, 0.8f);
        world.spawnParticle(
                Particle.LARGE_SMOKE,
                detonationPoint,
                ExcavationChargeDetonator.FIZZLE_PARTICLE_COUNT,
                0.4, 0.4, 0.4,
                0.02
        );
    }

    /**
     * Announces the detonation to everyone in earshot.
     * <p>
     * Pitch falls and volume rises with the level, so a level four charge reads as a deeper and
     * heavier detonation than a level one before its wave has travelled far enough to be judged by
     * eye.
     *
     * @param level           The level of the charge going off
     * @param detonationPoint The place the charge went off
     */
    private static void announceToSenses(BlastLevel level, Location detonationPoint) {
        World world = detonationPoint.getWorld();
        float progress = (float) (level.getLevel() - BlastLevel.MIN_LEVEL)
                / (BlastLevel.MAX_LEVEL - BlastLevel.MIN_LEVEL);

        float volume = ExcavationChargeDetonator.MIN_BLAST_VOLUME
                + (ExcavationChargeDetonator.MAX_BLAST_VOLUME - ExcavationChargeDetonator.MIN_BLAST_VOLUME) * progress;
        float pitch = ExcavationChargeDetonator.MAX_BLAST_PITCH
                - (ExcavationChargeDetonator.MAX_BLAST_PITCH - ExcavationChargeDetonator.MIN_BLAST_PITCH) * progress;

        world.playSound(detonationPoint, ExcavationChargeDetonator.BLAST_SOUND, volume, pitch);
        world.playSound(detonationPoint, ExcavationChargeDetonator.BLAST_BOOM_SOUND, volume, pitch);

        if (level.getLevel() >= ExcavationChargeDetonator.RUMBLE_LEVEL) {
            world.playSound(
                    detonationPoint,
                    ExcavationChargeDetonator.BLAST_RUMBLE_SOUND,
                    volume,
                    ExcavationChargeDetonator.RUMBLE_PITCH
            );
        }

        world.spawnParticle(Particle.EXPLOSION_EMITTER, detonationPoint, level.getLevel());
        world.spawnParticle(
                Particle.DUST,
                detonationPoint,
                ExcavationChargeDetonator.BURST_PARTICLE_COUNT * level.getLevel(),
                ExcavationChargeDetonator.BURST_SPREAD,
                ExcavationChargeDetonator.BURST_SPREAD,
                ExcavationChargeDetonator.BURST_SPREAD,
                0.0,
                new Particle.DustOptions(
                        Color.fromRGB(level.getArmingColour()), ExcavationChargeDetonator.BURST_DUST_SIZE));
    }

    /**
     * Fires the one explosion event the blast announces and returns the blocks it may still take.
     * <p>
     * Listeners may remove entries from {@link EntityExplodeEvent#blockList()} to spare individual
     * blocks, so the list is read back after the call rather than reusing the planned one.
     *
     * @param charge          The charge going off
     * @param detonationPoint The location the charge detonates in
     * @param plan            The blocks the blast would remove
     * @return The blocks to carve, or null when a listener cancelled the explosion
     */
    private static List<Block> announce(EnderCrystal charge, Location detonationPoint, List<Block> plan) {
        EntityExplodeEvent event = new EntityExplodeEvent(
                charge,
                detonationPoint,
                new ArrayList<>(plan),
                ExcavationChargeDetonator.EXPLOSION_YIELD,
                ExplosionResult.DESTROY);

        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return null;
        }

        return List.copyOf(event.blockList());
    }

    /**
     * Applies the blast damage to every living entity standing inside the carved volume.
     *
     * @param geometry        The volume the blast occupies
     * @param level           The blast level supplying the centre damage
     * @param detonationPoint The location the charge detonates in
     */
    private void damageCaughtEntities(BlastGeometry geometry, BlastLevel level, Location detonationPoint) {
        double maxExtent = level.getSize() / 2.0;
        DamageSource damageSource = DamageSource.builder(DamageType.EXPLOSION)
                .withDamageLocation(detonationPoint)
                .build();

        for (LivingEntity victim : detonationPoint.getWorld().getNearbyLivingEntities(detonationPoint, maxExtent)) {
            ExcavationChargeDetonator.damageVictim(victim, geometry, detonationPoint, level, maxExtent, damageSource);
        }
    }

    /**
     * Applies the falloff damage and the knockback impulse to a single entity.
     *
     * @param victim          The entity caught in the blast
     * @param geometry        The volume the blast occupies
     * @param detonationPoint The location the charge detonates in
     * @param level           The blast level supplying the centre damage
     * @param maxExtent       The distance at which the damage reaches zero
     * @param damageSource    The explosion source armour and enchantments are judged against
     */
    private static void damageVictim(
            LivingEntity victim,
            BlastGeometry geometry,
            Location detonationPoint,
            BlastLevel level,
            double maxExtent,
            DamageSource damageSource
    ) {
        Location victimLocation = victim.getLocation();

        if (!geometry.contains(ExcavationChargeDetonator.offsetBetween(detonationPoint, victimLocation))) {
            return;
        }

        double damage = BlastDamage.falloff(
                level.getCentreDamage(), victimLocation.distance(detonationPoint), maxExtent);

        if (damage <= 0.0) {
            return;
        }

        victim.damage(damage, damageSource);
        victim.setVelocity(victim.getVelocity().add(ExcavationChargeDetonator.knockback(
                detonationPoint, victimLocation, damage / level.getCentreDamage())));
    }

    /**
     * Builds the impulse pushing an entity away from the blast centre.
     *
     * @param detonationPoint The location the charge detonates in
     * @param victimLocation  The location of the entity caught in the blast
     * @param share           The share of the centre damage the entity took
     * @return The velocity to add to the entity
     */
    private static Vector knockback(Location detonationPoint, Location victimLocation, double share) {
        Vector direction = victimLocation.toVector().subtract(detonationPoint.toVector());

        if (direction.length() < ExcavationChargeDetonator.MIN_KNOCKBACK_DISTANCE) {
            return new Vector(0.0, ExcavationChargeDetonator.KNOCKBACK_STRENGTH * share, 0.0);
        }

        return direction.normalize().multiply(ExcavationChargeDetonator.KNOCKBACK_STRENGTH * share);
    }

    /**
     * Resolves which neighbouring charges the blast wakes and books them into the cascade.
     *
     * @param charge          The charge going off
     * @param level           The blast level bounding the neighbour scan
     * @param geometry        The volume the blast carves, deciding which neighbours wake
     * @param detonationPoint The location the charge detonates in
     * @param session         The shared state of the running cascade
     * @return The charges to wake, at most as many as the session has capacity left
     */
    private List<EnderCrystal> resolveChain(
            EnderCrystal charge,
            BlastLevel level,
            BlastGeometry geometry,
            Location detonationPoint,
            ChainSession session
    ) {
        // The scan radius only bounds the entity search; the exact wake decision is the
        // geometry's containment check. The largest offset any shape reaches is its size along
        // one axis plus half of it on the others, so twice the size covers every shape.
        Map<UUID, EnderCrystal> inRange = this.chargesInRange(detonationPoint, level.getSize() * 2);

        List<ChainCandidate> placed = new ArrayList<>(inRange.size());
        for (Map.Entry<UUID, EnderCrystal> entry : inRange.entrySet()) {
            placed.add(ExcavationChargeDetonator.toCandidate(entry.getKey(), entry.getValue().getLocation()));
        }

        List<ChainCandidate> woken = ChainResolver.resolve(
                ExcavationChargeDetonator.toCandidate(charge.getUniqueId(), detonationPoint),
                geometry,
                placed,
                session);

        List<EnderCrystal> chained = new ArrayList<>(woken.size());
        for (ChainCandidate candidate : woken) {
            chained.add(inRange.get(candidate.id()));
        }

        return List.copyOf(chained);
    }

    /**
     * Collects the placed Excavation Charges standing within the scan radius of the detonation.
     *
     * @param detonationPoint The location the charge detonates in
     * @param scanRadius      The straight-line radius bounding the entity search
     * @return The charges in range, keyed by their identity
     */
    private Map<UUID, EnderCrystal> chargesInRange(Location detonationPoint, int scanRadius) {
        Map<UUID, EnderCrystal> inRange = new LinkedHashMap<>();

        for (EnderCrystal candidate : detonationPoint.getWorld()
                .getNearbyEntitiesByType(EnderCrystal.class, detonationPoint, scanRadius)) {
            if (PlacedExcavationCharge.of(this.keys, candidate).isEmpty()) {
                continue;
            }

            inRange.put(candidate.getUniqueId(), candidate);
        }

        return inRange;
    }

    /**
     * Returns the block offset of a location relative to the blast centre.
     *
     * @param detonationPoint The location the charge detonates in
     * @param location        The location to measure
     * @return The offset relative to the blast centre
     */
    private static BlastVector offsetBetween(Location detonationPoint, Location location) {
        return new BlastVector(
                location.getBlockX() - detonationPoint.getBlockX(),
                location.getBlockY() - detonationPoint.getBlockY(),
                location.getBlockZ() - detonationPoint.getBlockZ());
    }

    /**
     * Turns an identity and a world location into a chain candidate.
     *
     * @param id       The identity of the placed charge
     * @param location The location the charge stands in
     * @return The candidate the chain resolver measures
     */
    static ChainCandidate toCandidate(UUID id, Location location) {
        return new ChainCandidate(
                id, new BlastVector(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
    }
}
