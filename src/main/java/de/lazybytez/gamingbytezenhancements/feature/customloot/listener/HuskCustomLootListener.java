package de.lazybytez.gamingbytezenhancements.feature.customloot.listener;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener for Husk deaths that modifies the dropped items.
 */
public class HuskCustomLootListener implements Listener {
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();

        if (!entity.getType().equals(EntityType.HUSK)) {
            return;
        }

        this.addSandToDrops(event);
    }

    /**
     * Add 1 to 5 sand to the drops of the entity.
     */
    private void addSandToDrops(EntityDeathEvent event) {
        int sandAmount = ((int) (Math.random() * 5));

        event.getDrops().add(new ItemStack(Material.SAND, sandAmount));
    }
}
