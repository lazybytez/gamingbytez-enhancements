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
package de.lazybytez.gamingbytezenhancements.feature.antimobgriefing.event;

import de.lazybytez.gamingbytezenhancements.feature.antimobgriefing.GriefProtectionRegistry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.projectiles.ProjectileSource;

public class AntiMobGriefingListener implements Listener {
    @EventHandler
    public void onEntityExplosion(EntityExplodeEvent e) {
        if (!GriefProtectionRegistry.GRIEFING_DISABLED_ENTITY_TYPES.contains(e.getEntityType())) {
            return;
        }

        e.blockList().clear();
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent e) {
        if (!GriefProtectionRegistry.GRIEFING_DISABLED_ENTITY_TYPES.contains(e.getEntityType())) {
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onHangingEntityBreak(HangingBreakByEntityEvent e) {
        // Entity and explosion are the only causes that should be cancelled
        // as they are the only ones that are the product of entity interaction.
        if (!e.getCause().equals(HangingBreakByEntityEvent.RemoveCause.ENTITY)
                && !e.getCause().equals(HangingBreakByEntityEvent.RemoveCause.EXPLOSION)) {
            return;
        }

        if (!GriefProtectionRegistry.GRIEFING_DISABLED_ENTITY_TYPES.contains(e.getRemover().getType())) {
            return;
        }

        if (!GriefProtectionRegistry.PROTECTED_ENTITIES.contains(e.getEntity().getType())) {
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)
                && !e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
            return;
        }

        if (!GriefProtectionRegistry.GRIEFING_DISABLED_ENTITY_TYPES.contains(e.getDamager().getType())) {
            return;
        }

        if (!GriefProtectionRegistry.PROTECTED_ENTITIES.contains(e.getEntity().getType())) {
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByProjectile(EntityDamageByEntityEvent e) {
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            return;
        }

        Entity attacker = e.getDamager();

        // Projectile and affected entity checks
        if (!GriefProtectionRegistry.GRIEFING_DISABLED_PROJECTILES_WITH_SHOOTERS_CHECK.contains(attacker.getType())) {
            return;
        }

        if (!GriefProtectionRegistry.PROTECTED_ENTITIES.contains(e.getEntity().getType())) {
            return;
        }

        // Complex shooter checks using instanceof
        // As this one is expensive (compared to the others), it is the last check.
        if (!(attacker instanceof Projectile projectile)) {
            return;
        }

        if (projectile.getShooter() == null) {
            return;
        }

        ProjectileSource shooter = projectile.getShooter();
        for (Class<? extends Entity> disabledShooters : GriefProtectionRegistry.GRIEFING_DISABLED_PROJECTILE_SHOOTERS) {
            if (disabledShooters.isInstance(shooter)) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent e) {
        Entity attacker = e.getAttacker();
        if (attacker == null) {
            return;
        }

        if (!GriefProtectionRegistry.GRIEFING_DISABLED_ENTITY_TYPES.contains(attacker.getType())) {
            return;
        }

        if (!GriefProtectionRegistry.PROTECTED_ENTITIES.contains(e.getVehicle().getType())) {
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onVehicleDestroyByProjectile(VehicleDestroyEvent e) {
        Entity attacker = e.getAttacker();
        if (attacker == null) {
            return;
        }

        if (!GriefProtectionRegistry.GRIEFING_DISABLED_PROJECTILES_WITH_SHOOTERS_CHECK.contains(attacker.getType())) {
            return;
        }

        if (!GriefProtectionRegistry.PROTECTED_ENTITIES.contains(e.getVehicle().getType())) {
            return;
        }

        if (!(attacker instanceof Projectile projectile)) {
            return;
        }

        if (projectile.getShooter() == null) {
            return;
        }

        ProjectileSource shooter = projectile.getShooter();
        for (Class<? extends Entity> disabledShooters : GriefProtectionRegistry.GRIEFING_DISABLED_PROJECTILE_SHOOTERS) {
            if (disabledShooters.isInstance(shooter)) {
                e.setCancelled(true);
                return;
            }
        }
    }
}
