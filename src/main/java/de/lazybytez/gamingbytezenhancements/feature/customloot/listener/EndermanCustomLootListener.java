package de.lazybytez.gamingbytezenhancements.feature.customloot.listener;

import de.lazybytez.gamingbytezenhancements.feature.customloot.service.EnchantmentLevelOnItemDeterminer;
import de.lazybytez.gamingbytezenhancements.lib.util.ChanceUtil;
import it.unimi.dsi.fastutil.ints.Int2DoubleLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import java.util.SortedMap;

/**
 * Listener for Enderman deaths that modifies the dropped items.
 */
public class EndermanCustomLootListener implements Listener {
    private final SortedMap<Integer, SortedMap<Integer, Double>> lootLevelToProbabilityMap = new Int2ObjectAVLTreeMap<>();
    private final EnchantmentLevelOnItemDeterminer enchantmentLevelOnItemDeterminer;

    public EndermanCustomLootListener(EnchantmentLevelOnItemDeterminer enchantmentLevelOnItemDeterminer) {
        this.enchantmentLevelOnItemDeterminer = enchantmentLevelOnItemDeterminer;
        SortedMap<Integer, Double> noLootProbabilityMap = new Int2DoubleLinkedOpenHashMap();
        noLootProbabilityMap.put(0, 66.0);
        noLootProbabilityMap.put(1, 22.0);
        noLootProbabilityMap.put(2, 8.0);
        noLootProbabilityMap.put(3, 4.0);
        this.lootLevelToProbabilityMap.put(0, noLootProbabilityMap);
        SortedMap<Integer, Double> oneLootProbabilityMap = new Int2DoubleLinkedOpenHashMap();
        oneLootProbabilityMap.put(0, 33.0);
        oneLootProbabilityMap.put(1, 33.0);
        oneLootProbabilityMap.put(2, 20.0);
        oneLootProbabilityMap.put(3, 14.0);
        this.lootLevelToProbabilityMap.put(1, oneLootProbabilityMap);
        SortedMap<Integer, Double> twoLootProbabilityMap = new Int2DoubleLinkedOpenHashMap();
        twoLootProbabilityMap.put(0, 16.0);
        twoLootProbabilityMap.put(1, 33.0);
        twoLootProbabilityMap.put(2, 31.0);
        twoLootProbabilityMap.put(3, 20.0);
        this.lootLevelToProbabilityMap.put(2, twoLootProbabilityMap);
        SortedMap<Integer, Double> threeLootProbabilityMap = new Int2DoubleLinkedOpenHashMap();
        threeLootProbabilityMap.put(1, 34.0);
        threeLootProbabilityMap.put(2, 33.0);
        threeLootProbabilityMap.put(3, 33.0);
        this.lootLevelToProbabilityMap.put(3, threeLootProbabilityMap);
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

        SortedMap<Integer, Double> probabilityMap = this.lootLevelToProbabilityMap.get(lootLevel);
        int chorusFruitAmount = ChanceUtil.getRandomIntegerWithProbability(probabilityMap);

        // Cannot create item stacks with quantity 0
        if (0 == chorusFruitAmount) {
            return;
        }

        event.getDrops().add(new ItemStack(Material.CHORUS_FRUIT, chorusFruitAmount));
    }
}
