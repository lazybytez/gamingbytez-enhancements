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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.excavationcharge;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastScheduler;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.ExcavationChargeAuditLog;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.ExcavationChargeDetonator;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.ExcavationChargeFuse;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.excavationcharge.PlacedExcavationCharge;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import java.util.Objects;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Listener that sets a placed Excavation Charge off when it is hit.
 * <p>
 * The damage is always cancelled. Vanilla answers any damage to an end crystal with a large
 * explosion of its own, and letting that one through would add an uncarved vanilla blast on top of
 * the volume the charge is meant to carve, so the charge is never allowed to take damage at all.
 * <p>
 * An end crystal without the placed Excavation Charge marker belongs to vanilla or to another plugin and
 * is left alone.
 */
public class DetonateExcavationChargeListener implements Listener {

    private final ExcavationChargeDetonator detonator;
    private final ExcavationChargeFuse fuse;
    private final ExcavationChargeAuditLog auditLog;
    private final Messenger messenger;
    private final PlacedExcavationCharge.Keys keys;

    /**
     * Creates the listener and the detonation pipeline it drives.
     *
     * @param mythicAltarFeature The feature owning the plugin instance and its namespace
     * @param blastScheduler     The scheduler owning every block mutation a blast performs
     * @param auditLog           The audit trail ignitions and detonations are recorded on
     * @param messenger          The messenger every player facing line of this feature is sent through
     */
    public DetonateExcavationChargeListener(
            MythicAltarFeature mythicAltarFeature,
            BlastScheduler blastScheduler,
            ExcavationChargeAuditLog auditLog,
            Messenger messenger
    ) {
        Objects.requireNonNull(mythicAltarFeature, "mythicAltarFeature must not be null");

        EnhancementsPlugin plugin = mythicAltarFeature.getPlugin();

        this.auditLog = Objects.requireNonNull(auditLog, "auditLog must not be null");
        this.keys = PlacedExcavationCharge.Keys.of(plugin);
        this.detonator = new ExcavationChargeDetonator(blastScheduler, this.auditLog, this.keys);
        this.fuse = new ExcavationChargeFuse(plugin, this.detonator, this.auditLog);
        this.messenger = Objects.requireNonNull(messenger, "messenger must not be null");
    }

    /**
     * Start the countdown of a placed Excavation Charge that was hit.
     *
     * @param event The entity damage event
     */
    @EventHandler(ignoreCancelled = true)
    public void onDamageExcavationCharge(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof EnderCrystal charge)) {
            return;
        }

        if (PlacedExcavationCharge.of(this.keys, charge).isEmpty()) {
            return;
        }

        event.setCancelled(true);

        if (!this.fuse.armForPlayer(charge)) {
            return;
        }

        this.logIgnition(event, charge);
        this.announceArming(event);
    }

    /**
     * Arm the given crystal if it is a placed Excavation Charge.
     * <p>
     * This is the entry point for triggers that are not damage, such as a redstone pulse next to
     * the charge. It shares the fuse with the damage path, so a charge already counting down is
     * left alone rather than restarted.
     *
     * @param crystal The end crystal a trigger fired next to
     * @return True when a countdown was started
     */
    public boolean ignite(EnderCrystal crystal) {
        if (PlacedExcavationCharge.of(this.keys, crystal).isEmpty()) {
            return false;
        }

        return this.fuse.armForPlayer(crystal);
    }

    /**
     * Tell whether the given crystal is a charge currently counting down.
     *
     * @param crystal The end crystal to check
     * @return True when a fuse is burning on it
     */
    public boolean isBurning(EnderCrystal crystal) {
        return this.fuse.isBurning(crystal);
    }

    /**
     * Record who or what set the charge off on the audit trail.
     *
     * @param event  The entity damage event that started the countdown
     * @param charge The charge whose countdown started
     */
    private void logIgnition(EntityDamageEvent event, EnderCrystal charge) {
        if (event instanceof EntityDamageByEntityEvent damageByEntity
                && damageByEntity.getDamager() instanceof Player player) {
            this.auditLog.ignitedByPlayer(player, charge.getLocation());

            return;
        }

        this.auditLog.ignitedBy(event.getCause().name() + " damage", charge.getLocation());
    }

    /**
     * Tell the player who hit the charge that its countdown is running.
     *
     * @param event The entity damage event that started the countdown
     */
    private void announceArming(EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent damageByEntity)) {
            return;
        }

        if (!(damageByEntity.getDamager() instanceof Player player)) {
            return;
        }

        this.messenger.warning(player, "The Excavation Charge is armed and about to detonate.");
    }
}
