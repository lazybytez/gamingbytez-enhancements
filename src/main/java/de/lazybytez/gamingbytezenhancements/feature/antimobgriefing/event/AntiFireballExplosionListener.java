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

import java.util.Set;

public class AntiFireballExplosionListener implements Listener {
   private final Set<EntityType> destructionDisabledEntities = Set.of(
            EntityType.GHAST,
            EntityType.BLAZE,
            EntityType.WITHER,
            EntityType.FIREBALL
    );

    @EventHandler
    public void onEntityExplosion(EntityExplodeEvent e) {
        if (!e.getEntityType().equals(EntityType.FIREBALL)) {
            return;
        }

        e.blockList().clear();
    }

    @EventHandler
    public void onHangingEntityBreak(HangingBreakByEntityEvent e) {
        if (!e.getCause().equals(HangingBreakEvent.RemoveCause.EXPLOSION)
                && !e.getCause().equals(HangingBreakEvent.RemoveCause.ENTITY)) {
            return;
        }

        if (!this.destructionDisabledEntities.contains(e.getRemover().getType())) {
            return;
        }

        if (!GriefProtectionRegistry.PROTECTED_HANGING_ENTITIES.contains(e.getEntity().getType())) {
            return;
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByFireball(EntityDamageByEntityEvent e) {
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
                        && !e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            return;
        }

        if (!this.destructionDisabledEntities.contains(e.getDamager().getType())) {
            return;
        }

        if (!GriefProtectionRegistry.PROTECTED_ENTITIES.contains(e.getEntity().getType())) {
            return;
        }

        e.setCancelled(true);
    }
}
