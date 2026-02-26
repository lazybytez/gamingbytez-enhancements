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
 * Listener for Husk deaths that modifies the dropped items.
 */
public class HuskCustomLootListener implements Listener {
    private final SortedMap<Integer, SortedMap<Integer, Double>> lootLevelToProbabilityMap = new Int2ObjectAVLTreeMap<>();
    private final EnchantmentLevelOnItemDeterminer enchantmentLevelOnItemDeterminer;

    public HuskCustomLootListener(EnchantmentLevelOnItemDeterminer enchantmentLevelOnItemDeterminer) {
        this.enchantmentLevelOnItemDeterminer = enchantmentLevelOnItemDeterminer;
        SortedMap<Integer, Double> noLootProbabilityMap = new Int2DoubleLinkedOpenHashMap();
        noLootProbabilityMap.put(0, 33.0);
        noLootProbabilityMap.put(1, 22.0);
        noLootProbabilityMap.put(2, 22.0);
        noLootProbabilityMap.put(3, 11.0);
        noLootProbabilityMap.put(4, 8.0);
        noLootProbabilityMap.put(5, 4.0);
        this.lootLevelToProbabilityMap.put(0, noLootProbabilityMap);
        SortedMap<Integer, Double> oneLootProbabilityMap = new Int2DoubleLinkedOpenHashMap();
        oneLootProbabilityMap.put(0, 11.0);
        oneLootProbabilityMap.put(1, 33.0);
        oneLootProbabilityMap.put(2, 22.0);
        oneLootProbabilityMap.put(3, 12.0);
        oneLootProbabilityMap.put(4, 12.0);
        oneLootProbabilityMap.put(5, 10.0);
        this.lootLevelToProbabilityMap.put(1, oneLootProbabilityMap);
        SortedMap<Integer, Double> twoLootProbabilityMap = new Int2DoubleLinkedOpenHashMap();
        twoLootProbabilityMap.put(1, 22.0);
        twoLootProbabilityMap.put(2, 33.0);
        twoLootProbabilityMap.put(3, 15.0);
        twoLootProbabilityMap.put(4, 15.0);
        twoLootProbabilityMap.put(5, 15.0);
        this.lootLevelToProbabilityMap.put(2, twoLootProbabilityMap);
        SortedMap<Integer, Double> threeLootProbabilityMap = new Int2DoubleLinkedOpenHashMap();
        threeLootProbabilityMap.put(2, 25.0);
        threeLootProbabilityMap.put(3, 25.0);
        threeLootProbabilityMap.put(4, 25.0);
        threeLootProbabilityMap.put(5, 25.0);
        this.lootLevelToProbabilityMap.put(3, threeLootProbabilityMap);
    }

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
        int lootLevel = this.enchantmentLevelOnItemDeterminer.getEnchantmentLevelOnMeleeWeapon(event, Enchantment.LOOTING);

        SortedMap<Integer, Double> probabilityMap = this.lootLevelToProbabilityMap.get(lootLevel);
        int sandAmount = ChanceUtil.getRandomIntegerWithProbability(probabilityMap);

        // Cannot create item stacks with quantity 0
        if (sandAmount == 0) {
            return;
        }

        event.getDrops().add(new ItemStack(Material.SAND, sandAmount));
    }
}
