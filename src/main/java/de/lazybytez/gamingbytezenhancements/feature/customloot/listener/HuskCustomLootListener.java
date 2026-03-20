/*
 * Gaming Bytez Enhancements - Gameplay enhancements used on our SMP servers.
 * Copyright (C) 2026 Lazy Bytez (Pascal Zarrad, Elias Knodel) and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.lazybytez.gamingbytezenhancements.feature.customloot.listener;

import de.lazybytez.gamingbytezenhancements.feature.customloot.service.EnchantmentLevelOnItemDeterminer;
import de.lazybytez.gamingbytezenhancements.lib.util.ChanceUtil;
import de.lazybytez.gamingbytezenhancements.lib.util.Pair;
import de.lazybytez.gamingbytezenhancements.lib.util.chance.ProbabilityMap;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;

/**
 * Listener for Husk deaths that modifies the dropped items.
 */
public class HuskCustomLootListener implements Listener {
    private final HashMap<Integer, ProbabilityMap<Integer>> lootLevelToProbabilityMap = new HashMap<>();
    private final EnchantmentLevelOnItemDeterminer enchantmentLevelOnItemDeterminer;

    public HuskCustomLootListener(EnchantmentLevelOnItemDeterminer enchantmentLevelOnItemDeterminer) {
        this.enchantmentLevelOnItemDeterminer = enchantmentLevelOnItemDeterminer;
        ProbabilityMap<Integer> noLootProbabilityMap = new ProbabilityMap<>();
        noLootProbabilityMap.put(
                Pair.of(0, 33.0),
                Pair.of(1, 22.0),
                Pair.of(2, 22.0),
                Pair.of(3, 11.0),
                Pair.of(4, 8.0),
                Pair.of(5, 4.0)
        );
        this.lootLevelToProbabilityMap.put(0, noLootProbabilityMap);
        ProbabilityMap<Integer> oneLootProbabilityMap = new ProbabilityMap<>();
        oneLootProbabilityMap.put(
                Pair.of(0, 11.0),
                Pair.of(1, 33.0),
                Pair.of(2, 22.0),
                Pair.of(3, 12.0),
                Pair.of(4, 12.0),
                Pair.of(5, 10.0)
        );
        this.lootLevelToProbabilityMap.put(1, oneLootProbabilityMap);
        ProbabilityMap<Integer> twoLootProbabilityMap = new ProbabilityMap<>();
        twoLootProbabilityMap.put(
                Pair.of(1, 22.0),
                Pair.of(2, 33.0),
                Pair.of(3, 15.0),
                Pair.of(4, 15.0),
                Pair.of(5, 15.0)
        );
        this.lootLevelToProbabilityMap.put(2, twoLootProbabilityMap);
        ProbabilityMap<Integer> threeLootProbabilityMap = new ProbabilityMap<>();
        threeLootProbabilityMap.put(
                Pair.of(2, 25.0),
                Pair.of(3, 25.0),
                Pair.of(4, 25.0),
                Pair.of(5, 25.0)
        );
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

        ProbabilityMap<Integer> probabilityMap = this.lootLevelToProbabilityMap.get(lootLevel);
        int sandAmount = ChanceUtil.getRandomIntegerWithProbability(probabilityMap);

        // Cannot create item stacks with quantity 0
        if (sandAmount == 0) {
            return;
        }

        event.getDrops().add(new ItemStack(Material.SAND, sandAmount));
    }
}
