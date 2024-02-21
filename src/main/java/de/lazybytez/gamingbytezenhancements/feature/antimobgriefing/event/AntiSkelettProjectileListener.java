package de.lazybytez.gamingbytezenhancements.feature.antimobgriefing.event;

import de.lazybytez.gamingbytezenhancements.feature.antimobgriefing.GriefProtectionRegistry;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

public class AntiSkelettProjectileListener implements Listener {
    @EventHandler
    public void onHangingEntityBreak(HangingBreakByEntityEvent e) {
        if (!e.getCause().equals(HangingBreakEvent.RemoveCause.ENTITY)) {
            return;
        }

        if (!e.getRemover().getType().equals(EntityType.SKELETON)) {
            return;
        }

        if (!GriefProtectionRegistry.PROTECTED_HANGING_ENTITIES.contains(e.getEntity().getType())) {
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageBySkelett(EntityDamageByEntityEvent e) {
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            return;
        }

        if (!e.getDamager().getType().equals(EntityType.ARROW)
                && !e.getDamager().getType().equals(EntityType.SPECTRAL_ARROW)) {
            return;
        }

        AbstractArrow arrow = (AbstractArrow) e.getDamager();
        if (arrow.getShooter() == null || !(arrow.getShooter() instanceof Skeleton)) {
            return;
        }

        if (!GriefProtectionRegistry.PROTECTED_ENTITIES.contains(e.getEntity().getType())) {
            return;
        }

        e.setCancelled(true);
    }
}
