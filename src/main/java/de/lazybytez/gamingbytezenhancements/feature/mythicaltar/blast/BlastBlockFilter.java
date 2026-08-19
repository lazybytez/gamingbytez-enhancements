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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast;

import org.bukkit.Material;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.ToDoubleFunction;

/**
 * Decides whether a {@link Material} may be destroyed by an Excavation Charge blast.
 * <p>
 * A material is destructible only when its blast resistance does not exceed obsidian's, it is not
 * air or a liquid, and it is not part of an explicit deny set. The deny set exists because blast
 * resistance alone does not describe intent: some blocks share obsidian's resistance but must never
 * be destroyed by a blast.
 * <p>
 * The blast resistance lookup is injected rather than called directly on {@link Material}, because
 * {@link Material#getBlastResistance()} requires a live server registry and cannot run in a unit
 * test. Production code obtains an instance via {@link #production()}.
 */
public final class BlastBlockFilter {
    private static final double MAX_BLAST_RESISTANCE = 1200.0;

    private static final Set<Material> DENIED_MATERIALS = EnumSet.of(
            Material.BEDROCK,
            Material.REINFORCED_DEEPSLATE,
            Material.BARRIER,
            Material.LIGHT,
            Material.COMMAND_BLOCK,
            Material.CHAIN_COMMAND_BLOCK,
            Material.REPEATING_COMMAND_BLOCK,
            Material.STRUCTURE_BLOCK,
            Material.JIGSAW,
            Material.END_PORTAL,
            Material.END_PORTAL_FRAME,
            Material.END_GATEWAY,
            Material.NETHER_PORTAL
    );

    private static final Set<Material> NON_DESTRUCTIBLE_MATERIALS = EnumSet.of(
            Material.AIR,
            Material.CAVE_AIR,
            Material.VOID_AIR,
            Material.WATER,
            Material.LAVA
    );

    private final ToDoubleFunction<Material> blastResistanceLookup;

    /**
     * Creates a filter backed by the given blast resistance lookup.
     *
     * @param blastResistanceLookup resolves the blast resistance of a material
     */
    public BlastBlockFilter(ToDoubleFunction<Material> blastResistanceLookup) {
        this.blastResistanceLookup = Objects.requireNonNull(
                blastResistanceLookup, "blastResistanceLookup must not be null");
    }

    /**
     * Creates a filter backed by the live server registry.
     *
     * @return a filter that resolves blast resistance via {@link Material#getBlastResistance()}
     */
    public static BlastBlockFilter production() {
        return new BlastBlockFilter(Material::getBlastResistance);
    }

    /**
     * Checks whether the given material may be destroyed by an Excavation Charge blast.
     *
     * @param material the material to check
     * @return true if the material is destructible
     */
    public boolean isDestructible(Material material) {
        Objects.requireNonNull(material, "material must not be null");

        if (BlastBlockFilter.NON_DESTRUCTIBLE_MATERIALS.contains(material)) {
            return false;
        }

        if (BlastBlockFilter.DENIED_MATERIALS.contains(material)) {
            return false;
        }

        return this.blastResistanceLookup.applyAsDouble(material) <= BlastBlockFilter.MAX_BLAST_RESISTANCE;
    }
}
