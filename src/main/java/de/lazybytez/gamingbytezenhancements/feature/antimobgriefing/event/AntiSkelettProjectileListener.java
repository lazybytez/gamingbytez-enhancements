package de.lazybytez.gamingbytezenhancements.feature.antimobgriefing.event;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

import java.util.Set;

public class AntiSkelettProjectileListener implements Listener {
    private final Set<EntityType> protectedEntities = Set.of(
            EntityType.ARMOR_STAND,
            EntityType.MINECART,
            EntityType.MINECART_CHEST,
            EntityType.MINECART_COMMAND,
            EntityType.MINECART_FURNACE,
            EntityType.MINECART_HOPPER,
            EntityType.MINECART_MOB_SPAWNER,
            EntityType.MINECART_TNT,
            EntityType.BOAT,
            EntityType.CHEST_BOAT
    );

    private final Set<EntityType> protectedHangingEntities = Set.of(
            EntityType.ITEM_FRAME,
            EntityType.GLOW_ITEM_FRAME,
            EntityType.PAINTING
    );

    @EventHandler
    public void onHangingEntityBreak(HangingBreakByEntityEvent e) {
        if (!e.getCause().equals(HangingBreakEvent.RemoveCause.ENTITY)) {
            return;
        }

        if (!e.getRemover().getType().equals(EntityType.SKELETON)) {
            return;
        }

        if (!this.protectedHangingEntities.contains(e.getEntity().getType())) {
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

        if (!this.protectedEntities.contains(e.getEntity().getType())) {
            return;
        }

        e.setCancelled(true);
    }
}
