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

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.ExcavationChargeAuditLog;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

/**
 * Listener that arms an Excavation Charge when a redstone signal rises next to it.
 * <p>
 * A rising signal on any redstone component within reach of a placed charge starts the normal
 * fuse, so a charge can be wired to a lever, a button or a clock without sacrificing a TNT block
 * as a trigger. Only the rising edge arms, a signal that stays on does not re-arm the charge.
 */
public class RedstoneIgniteExcavationChargeListener implements Listener {

    /**
     * How far a powered component reaches, in blocks from its centre.
     * <p>
     * Two blocks covers wire or a repeater beside the charge's support block as well as one step
     * below it, without letting a distant circuit set off charges through walls.
     */
    private static final double IGNITE_RANGE = 2.0;

    private final DetonateExcavationChargeListener detonation;
    private final ExcavationChargeAuditLog auditLog;

    /**
     * Bind the listener to the detonation path it arms charges through.
     *
     * @param detonation The listener owning the shared fuse
     * @param auditLog   The audit trail redstone ignitions are recorded on
     */
    public RedstoneIgniteExcavationChargeListener(
            DetonateExcavationChargeListener detonation,
            ExcavationChargeAuditLog auditLog
    ) {
        this.detonation = Objects.requireNonNull(detonation, "detonation must not be null");
        this.auditLog = Objects.requireNonNull(auditLog, "auditLog must not be null");
    }

    /**
     * Arm every placed charge in reach of a component whose signal just rose.
     *
     * @param event The redstone current change
     */
    @EventHandler
    public void onRedstoneRise(BlockRedstoneEvent event) {
        if (event.getOldCurrent() > 0 || event.getNewCurrent() <= 0) {
            return;
        }

        Location centre = event.getBlock().getLocation().add(0.5, 0.5, 0.5);

        for (EnderCrystal crystal : centre.getWorld().getNearbyEntitiesByType(
                EnderCrystal.class, centre, RedstoneIgniteExcavationChargeListener.IGNITE_RANGE)) {
            if (this.detonation.ignite(crystal)) {
                this.auditLog.ignitedByRedstone(event.getBlock().getLocation(), crystal.getLocation());
            }
        }
    }
}
