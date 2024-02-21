package de.lazybytez.gamingbytezenhancements.feature.antimobgriefing.event;

import de.lazybytez.gamingbytezenhancements.feature.antimobgriefing.GriefProtectionRegistry;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

public class AntiCreeperExplosionListener implements Listener {
    @EventHandler
    public void onEntityExplosion(EntityExplodeEvent e) {
        if (!e.getEntityType().equals(EntityType.CREEPER)) {
            return;
        }

        e.blockList().clear();
    }

    @EventHandler
    public void onHangingEntityBreak(HangingBreakByEntityEvent e) {
        if (!e.getCause().equals(HangingBreakEvent.RemoveCause.EXPLOSION)) {
            return;
        }

        if (!e.getRemover().getType().equals(EntityType.CREEPER)) {
            return;
        }

        if (!GriefProtectionRegistry.PROTECTED_HANGING_ENTITIES.contains(e.getEntity().getType())) {
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByCreeper(EntityDamageByEntityEvent e) {
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
            return;
        }

        if (!e.getDamager().getType().equals(EntityType.CREEPER)) {
            return;
        }

        if (!GriefProtectionRegistry.PROTECTED_ENTITIES.contains(e.getEntity().getType())) {
            return;
        }

        e.setCancelled(true);
    }
}
