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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.schema.structure;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.MythicAltar;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the structure of a Mythic Altar.
 * <p>
 * The mythic altar consists of a center item frame with four pedestals at the corners (1 block distance).
 * Below the item frame is a diamond block, and the pedestals are made of emerald blocks.
 * The altar is surrounded by a ring of chiseled quartz blocks, sea lanterns, gold blocks, quartz stairs and pillars.
 */
public class MythicAltarStructure implements AltarStructureInterface {
    private final Map<Vector, Material> altarStructure;

    private final Map<Vector, EntityType> pedestalStructure;

    public MythicAltarStructure() {
        this.altarStructure = this.initializeAltarStructure();
        this.pedestalStructure = this.initializePedestalStructure();
    }

    /**
     * Initializes the block structure of the altar.
     *
     * @return The block structure of the altar.
     */
    private Map<Vector, Material> initializeAltarStructure() {
        Map<Vector, Material> altarStructure = new HashMap<>();

        // Center (1. Layer)
        altarStructure.put(new Vector(0, 0, 0), Material.DIAMOND_BLOCK);

        // Corner blocks (1. Layer)
        altarStructure.put(new Vector(2, 0, 2), Material.EMERALD_BLOCK);
        altarStructure.put(new Vector(2, 0, -2), Material.EMERALD_BLOCK);
        altarStructure.put(new Vector(-2, 0, 2), Material.EMERALD_BLOCK);
        altarStructure.put(new Vector(-2, 0, -2), Material.EMERALD_BLOCK);

        // Second layer
        altarStructure.put(new Vector(0, -1, 0), Material.CHISELED_QUARTZ_BLOCK);
        altarStructure.put(new Vector(1, -1, 0), Material.CHISELED_QUARTZ_BLOCK);
        altarStructure.put(new Vector(-1, -1, 0), Material.CHISELED_QUARTZ_BLOCK);
        altarStructure.put(new Vector(0, -1, 1), Material.CHISELED_QUARTZ_BLOCK);

        altarStructure.put(new Vector(1, -1, 1), Material.SEA_LANTERN);
        altarStructure.put(new Vector(-1, -1, 1), Material.SEA_LANTERN);
        altarStructure.put(new Vector(1, -1, -1), Material.SEA_LANTERN);
        altarStructure.put(new Vector(-1, -1, -1), Material.SEA_LANTERN);

        // Ring of chiseled quartz blocks (2. Layer, inner circle)
        // North
        altarStructure.put(new Vector(0, -1, 2), Material.CHISELED_QUARTZ_BLOCK);
        altarStructure.put(new Vector(1, -1, 2), Material.CHISELED_QUARTZ_BLOCK);
        altarStructure.put(new Vector(-1, -1, 2), Material.CHISELED_QUARTZ_BLOCK);

        // East
        altarStructure.put(new Vector(2, -1, 0), Material.CHISELED_QUARTZ_BLOCK);
        altarStructure.put(new Vector(2, -1, 1), Material.CHISELED_QUARTZ_BLOCK);
        altarStructure.put(new Vector(2, -1, -1), Material.CHISELED_QUARTZ_BLOCK);

        // South
        altarStructure.put(new Vector(0, -1, -2), Material.CHISELED_QUARTZ_BLOCK);
        altarStructure.put(new Vector(1, -1, -2), Material.CHISELED_QUARTZ_BLOCK);
        altarStructure.put(new Vector(-1, -1, -2), Material.CHISELED_QUARTZ_BLOCK);

        // West
        altarStructure.put(new Vector(-2, -1, 0), Material.CHISELED_QUARTZ_BLOCK);
        altarStructure.put(new Vector(-2, -1, 1), Material.CHISELED_QUARTZ_BLOCK);
        altarStructure.put(new Vector(-2, -1, -1), Material.CHISELED_QUARTZ_BLOCK);

        // Corner blocks (2. Layer)
        altarStructure.put(new Vector(2, -1, 2), Material.CHISELED_QUARTZ_BLOCK);
        altarStructure.put(new Vector(2, -1, -2), Material.CHISELED_QUARTZ_BLOCK);
        altarStructure.put(new Vector(-2, -1, 2), Material.CHISELED_QUARTZ_BLOCK);
        altarStructure.put(new Vector(-2, -1, -2), Material.CHISELED_QUARTZ_BLOCK);

        // Ring of three stairs, quartz pillars and gold block corners (2. Layer, outer circle)
        // North
        altarStructure.put(new Vector(0, -1, 3), Material.QUARTZ_STAIRS);
        altarStructure.put(new Vector(1, -1, 3), Material.QUARTZ_STAIRS);
        altarStructure.put(new Vector(-1, -1, 3), Material.QUARTZ_STAIRS);
        altarStructure.put(new Vector(2, -1, 3), Material.QUARTZ_PILLAR);
        altarStructure.put(new Vector(-2, -1, 3), Material.QUARTZ_PILLAR);

        // East
        altarStructure.put(new Vector(3, -1, 0), Material.QUARTZ_STAIRS);
        altarStructure.put(new Vector(3, -1, 1), Material.QUARTZ_STAIRS);
        altarStructure.put(new Vector(3, -1, -1), Material.QUARTZ_STAIRS);
        altarStructure.put(new Vector(3, -1, 2), Material.QUARTZ_PILLAR);
        altarStructure.put(new Vector(3, -1, -2), Material.QUARTZ_PILLAR);

        // South
        altarStructure.put(new Vector(0, -1, -3), Material.QUARTZ_STAIRS);
        altarStructure.put(new Vector(1, -1, -3), Material.QUARTZ_STAIRS);
        altarStructure.put(new Vector(-1, -1, -3), Material.QUARTZ_STAIRS);
        altarStructure.put(new Vector(2, -1, -3), Material.QUARTZ_PILLAR);
        altarStructure.put(new Vector(-2, -1, -3), Material.QUARTZ_PILLAR);

        // West
        altarStructure.put(new Vector(-3, -1, 0), Material.QUARTZ_STAIRS);
        altarStructure.put(new Vector(-3, -1, 1), Material.QUARTZ_STAIRS);
        altarStructure.put(new Vector(-3, -1, -1), Material.QUARTZ_STAIRS);
        altarStructure.put(new Vector(-3, -1, 2), Material.QUARTZ_PILLAR);
        altarStructure.put(new Vector(-3, -1, -2), Material.QUARTZ_PILLAR);

        // Corner blocks (3. Layer)
        altarStructure.put(new Vector(3, -1, 3), Material.GOLD_BLOCK);
        altarStructure.put(new Vector(3, -1, -3), Material.GOLD_BLOCK);
        altarStructure.put(new Vector(-3, -1, 3), Material.GOLD_BLOCK);
        altarStructure.put(new Vector(-3, -1, -3), Material.GOLD_BLOCK);

        return altarStructure;
    }

    /**
     * Initializes the pedestal structure of the altar.
     *
     * @return The pedestal structure of the altar.
     */
    private Map<Vector, EntityType> initializePedestalStructure() {
        Map<Vector, EntityType> pedestalStructure = new HashMap<>();

        // Center
        pedestalStructure.put(new Vector(0, 1, 0), EntityType.GLOW_ITEM_FRAME);

        // Corners
        pedestalStructure.put(new Vector(2, 1, 2), EntityType.GLOW_ITEM_FRAME);
        pedestalStructure.put(new Vector(2, 1, -2), EntityType.GLOW_ITEM_FRAME);
        pedestalStructure.put(new Vector(-2, 1, 2), EntityType.GLOW_ITEM_FRAME);
        pedestalStructure.put(new Vector(-2, 1, -2), EntityType.GLOW_ITEM_FRAME);

        return pedestalStructure;
    }

    @Override
    public Map<Vector, Material> getAltarStructure() {
        return altarStructure;
    }

    @Override
    public Map<Vector, EntityType> getPedestalStructure() {
        return pedestalStructure;
    }

    @Override
    public Class<? extends AltarInterface> getAltarClass() {
        return MythicAltar.class;
    }
}
