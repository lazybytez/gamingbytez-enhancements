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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlastBlockFilterTest {
    private static final Material[] DENY_SET_MATERIALS = {
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
    };

    @Test
    void isDestructible_acceptsMaterialAtObsidianResistanceCeiling() {
        BlastBlockFilter filter = new BlastBlockFilter(material -> 1200.0);

        assertTrue(filter.isDestructible(Material.OBSIDIAN));
    }

    @Test
    void isDestructible_rejectsMaterialAboveObsidianResistanceCeiling() {
        BlastBlockFilter filter = new BlastBlockFilter(material -> 1200.1);

        assertFalse(filter.isDestructible(Material.OBSIDIAN));
    }

    @ParameterizedTest
    @EnumSource(
            value = Material.class,
            names = {
                    "BEDROCK", "REINFORCED_DEEPSLATE", "BARRIER", "LIGHT",
                    "COMMAND_BLOCK", "CHAIN_COMMAND_BLOCK", "REPEATING_COMMAND_BLOCK",
                    "STRUCTURE_BLOCK", "JIGSAW", "END_PORTAL", "END_PORTAL_FRAME",
                    "END_GATEWAY", "NETHER_PORTAL"
            }
    )
    void isDestructible_rejectsDenySetMaterialsEvenAtZeroResistance(Material material) {
        BlastBlockFilter filter = new BlastBlockFilter(candidate -> 0.0);

        assertFalse(filter.isDestructible(material));
    }

    @Test
    void isDestructible_rejectsAir() {
        BlastBlockFilter filter = new BlastBlockFilter(material -> 0.0);

        assertFalse(filter.isDestructible(Material.AIR));
        assertFalse(filter.isDestructible(Material.CAVE_AIR));
        assertFalse(filter.isDestructible(Material.VOID_AIR));
    }

    @Test
    void isDestructible_rejectsLiquids() {
        BlastBlockFilter filter = new BlastBlockFilter(material -> 0.0);

        assertFalse(filter.isDestructible(Material.WATER));
        assertFalse(filter.isDestructible(Material.LAVA));
    }

    @Test
    void isDestructible_acceptsOrdinaryLowResistanceBlock() {
        BlastBlockFilter filter = new BlastBlockFilter(material -> 6.0);

        assertTrue(filter.isDestructible(Material.STONE));
    }

    @Test
    void production_bindsToGetBlastResistanceAndReturnsAnInstance() {
        assertNotNull(BlastBlockFilter.create());
    }
}
