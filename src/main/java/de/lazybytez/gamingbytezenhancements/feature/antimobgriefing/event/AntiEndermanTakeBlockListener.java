package de.lazybytez.gamingbytezenhancements.feature.antimobgriefing.event;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class AntiEndermanTakeBlockListener implements Listener {
    @EventHandler
    public void onEndermanChangeBlock(EntityChangeBlockEvent e) {
        if (!e.getEntityType().equals(EntityType.ENDERMAN)) {
            return;
        }

        e.setCancelled(true);
    }
}
