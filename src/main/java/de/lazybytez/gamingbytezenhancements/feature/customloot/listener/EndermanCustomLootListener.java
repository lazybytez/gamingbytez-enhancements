package de.lazybytez.gamingbytezenhancements.feature.customloot.listener;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Listener for Enderman deaths that modifies the dropped items.
 */
public class EndermanCustomLootListener implements Listener {
    private static final float PERCENTAGE_TO_DROP_CHORUS_FRUITS = 30;
    private static final float PERCENTAGE_TO_GET_THREE_CHORUS_FRUIT = 10;
    private static final float PERCENTAGE_TO_GET_TWO_CHORUS_FRUIT = 30;

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();

        if (!entity.getType().equals(EntityType.ENDERMAN)) {
            return;
        }

        this.addChorusFruitToDrops(event);
    }

    /**
     * Add 1 to 3 chorus fruit to the possible drops of the entity.
     */
    private void addChorusFruitToDrops(EntityDeathEvent event) {
        if (Math.random() > (PERCENTAGE_TO_DROP_CHORUS_FRUITS / 100)) {
            return;
        }

        int chorusFruitAmount = 1;

        if (Math.random() < (PERCENTAGE_TO_GET_THREE_CHORUS_FRUIT / 100)) {
            chorusFruitAmount = 3;
        } else if (Math.random() < (PERCENTAGE_TO_GET_TWO_CHORUS_FRUIT / 100)) {
            chorusFruitAmount = 2;
        }

        event.getDrops().add(new ItemStack(Material.CHORUS_FRUIT, chorusFruitAmount));
    }
}
