package de.lazybytez.gamingbytezenhancements.feature.customloot.listener;

import de.lazybytez.gamingbytezenhancements.feature.customloot.service.EnchantmentLevelOnItemDeterminer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener for Enderman deaths that modifies the dropped items.
 */
public class EndermanCustomLootListener implements Listener {
    private static final float PERCENTAGE_TO_DROP_CHORUS_FRUITS = 30;
    private static final float PERCENTAGE_TO_GET_THREE_CHORUS_FRUIT = 10;
    private static final float PERCENTAGE_TO_GET_TWO_CHORUS_FRUIT = 30;

    private final EnchantmentLevelOnItemDeterminer enchantmentLevelOnItemDeterminer;

    public EndermanCustomLootListener(EnchantmentLevelOnItemDeterminer enchantmentLevelOnItemDeterminer) {
        this.enchantmentLevelOnItemDeterminer = enchantmentLevelOnItemDeterminer;
    }

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
        int lootLevel = this.enchantmentLevelOnItemDeterminer.getEnchantmentLevelOnMeleeWeapon(event, Enchantment.LOOTING);

        if (lootLevel > 0) {
            event.getDrops().add(new ItemStack(Material.APPLE, 3));
            return;
        }

        if (Math.random() > (PERCENTAGE_TO_DROP_CHORUS_FRUITS * (lootLevel + 1) / 100)) {
            return;
        }

        int chorusFruitAmount = 1;

        if (Math.random() < (PERCENTAGE_TO_GET_THREE_CHORUS_FRUIT * (lootLevel + 1) / 100)) {
            chorusFruitAmount = 3;
        } else if (Math.random() < (PERCENTAGE_TO_GET_TWO_CHORUS_FRUIT * (lootLevel + 1) / 100)) {
            chorusFruitAmount = 2;
        }

        event.getDrops().add(new ItemStack(Material.CHORUS_FRUIT, chorusFruitAmount));
    }
}
