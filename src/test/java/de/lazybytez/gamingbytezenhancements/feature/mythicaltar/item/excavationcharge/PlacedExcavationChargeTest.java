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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.excavationcharge;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastGeometry;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastLevel;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastShape;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastVector;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.CuboidBlastGeometry;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.CylinderBlastGeometry;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.SphereBlastGeometry;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.TunnelBlastGeometry;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Covers {@link PlacedExcavationCharge}.
 * <p>
 * A charge that already stands in a live world was written by an earlier build, so every fallback
 * this class applies is pinned here: dropping one of them would silently rewrite the state of a
 * charge a player placed before the change.
 */
@ExtendWith(MockitoExtension.class)
class PlacedExcavationChargeTest {

    private static final String NAMESPACE = "gamingbytez-enhancements";

    @Mock
    private Plugin plugin;

    @Mock
    private EnderCrystal crystal;

    @Mock
    private PersistentDataContainer container;

    @Test
    void keysReuseTheItemShapeAndLevelKeyNames() {
        PlacedExcavationCharge.Keys keys = this.keys();

        assertEquals(ExcavationChargeManager.PDC_KEY_SHAPE, keys.shapeKey().getKey());
        assertEquals(ExcavationChargeManager.PDC_KEY_LEVEL, keys.levelKey().getKey());
    }

    @Test
    void keysKeepTheStoredMarkerAndFacingNames() {
        PlacedExcavationCharge.Keys keys = this.keys();

        assertEquals("gamingbytez-excavation-charge-placed", keys.placedKey().getKey());
        assertEquals("gamingbytez-excavation-charge-facing", keys.facingKey().getKey());
        assertEquals(PlacedExcavationChargeTest.NAMESPACE, keys.placedKey().getNamespace());
    }

    @Test
    void ofReadsBackEveryValueAPlacedChargeCarries() {
        PlacedExcavationCharge charge = this.placedCharge("SPHERE", 3, "EAST");

        assertEquals(BlastShape.SPHERE, charge.shape());
        assertEquals(BlastLevel.LEVEL_3, charge.level());
        assertEquals(BlockFace.EAST, charge.facing());
    }

    @Test
    void ofIsEmptyForACrystalWithoutTheMarker() {
        PlacedExcavationCharge.Keys keys = this.keys();
        when(this.crystal.getPersistentDataContainer()).thenReturn(this.container);
        when(this.container.getOrDefault(keys.placedKey(), PersistentDataType.BOOLEAN, false))
                .thenReturn(false);

        Optional<PlacedExcavationCharge> charge = PlacedExcavationCharge.of(keys, this.crystal);

        assertTrue(charge.isEmpty());
    }

    @Test
    void shapeFallsBackToCuboidWhenMissing() {
        assertEquals(BlastShape.CUBOID, this.placedCharge(null, 1, "NORTH").shape());
    }

    @Test
    void shapeFallsBackToCuboidWhenUnknown() {
        assertEquals(BlastShape.CUBOID, this.placedCharge("PYRAMID", 1, "NORTH").shape());
    }

    @Test
    void levelFallsBackToTheMinimumWhenMissing() {
        assertEquals(BlastLevel.of(BlastLevel.MIN_LEVEL), this.placedCharge("CUBOID", null, "NORTH").level());
    }

    @Test
    void levelClampsAValueOutsideTheRange() {
        assertEquals(BlastLevel.of(BlastLevel.MAX_LEVEL), this.placedCharge("CUBOID", 99, "NORTH").level());
    }

    @Test
    void facingFallsBackToNorthWhenMissing() {
        assertEquals(BlockFace.NORTH, this.placedCharge("TUNNEL", 1, null).facing());
    }

    @Test
    void facingFallsBackToNorthWhenNotCardinal() {
        assertEquals(BlockFace.NORTH, this.placedCharge("TUNNEL", 1, "NORTH_EAST").facing());
        assertEquals(BlockFace.NORTH, this.placedCharge("TUNNEL", 1, "SELF").facing());
    }

    @Test
    void facingFallsBackToNorthWhenUnknown() {
        assertEquals(BlockFace.NORTH, this.placedCharge("TUNNEL", 1, "SIDEWAYS").facing());
    }

    @Test
    void geometryMatchesTheShapeStoredOnTheCharge() {
        assertInstanceOf(CuboidBlastGeometry.class, this.placedCharge("CUBOID", 1, "NORTH").geometry());
        assertInstanceOf(SphereBlastGeometry.class, this.placedCharge("SPHERE", 1, "NORTH").geometry());
        assertInstanceOf(CylinderBlastGeometry.class, this.placedCharge("CYLINDER", 1, "NORTH").geometry());
        assertInstanceOf(TunnelBlastGeometry.class, this.placedCharge("TUNNEL", 1, "NORTH").geometry());
    }

    @Test
    void geometryBoresTheTunnelAlongTheStoredFacing() {
        BlastGeometry geometry = this.placedCharge("TUNNEL", 1, "EAST").geometry();

        assertTrue(geometry.contains(new BlastVector(1, 0, 0)));
        assertFalse(geometry.contains(new BlastVector(-1, 0, 0)));
    }

    @Test
    void stampWritesMarkerShapeLevelAndFacing() {
        PlacedExcavationCharge.Keys keys = this.keys();
        when(this.crystal.getPersistentDataContainer()).thenReturn(this.container);

        PlacedExcavationCharge.stamp(keys, this.crystal, BlastShape.TUNNEL, 4, BlockFace.UP);

        verify(this.container).set(keys.placedKey(), PersistentDataType.BOOLEAN, true);
        verify(this.container).set(keys.shapeKey(), PersistentDataType.STRING, "TUNNEL");
        verify(this.container).set(keys.levelKey(), PersistentDataType.INTEGER, 4);
        verify(this.container).set(keys.facingKey(), PersistentDataType.STRING, "UP");
    }

    private PlacedExcavationCharge.Keys keys() {
        when(this.plugin.namespace()).thenReturn(PlacedExcavationChargeTest.NAMESPACE);

        return PlacedExcavationCharge.Keys.of(this.plugin);
    }

    /**
     * Stands up a crystal carrying the marker and the given raw state.
     *
     * @param rawShape  The raw shape name stored on the entity, may be null
     * @param rawLevel  The raw level stored on the entity, may be null
     * @param rawFacing The raw facing name stored on the entity, may be null
     * @return The wrapper reading that state
     */
    private PlacedExcavationCharge placedCharge(String rawShape, Integer rawLevel, String rawFacing) {
        PlacedExcavationCharge.Keys keys = this.keys();

        when(this.crystal.getPersistentDataContainer()).thenReturn(this.container);
        when(this.container.getOrDefault(keys.placedKey(), PersistentDataType.BOOLEAN, false))
                .thenReturn(true);
        lenient().when(this.container.get(keys.shapeKey(), PersistentDataType.STRING)).thenReturn(rawShape);
        lenient().when(this.container.get(keys.levelKey(), PersistentDataType.INTEGER)).thenReturn(rawLevel);
        lenient().when(this.container.get(keys.facingKey(), PersistentDataType.STRING)).thenReturn(rawFacing);

        return PlacedExcavationCharge.of(keys, this.crystal).orElseThrow();
    }
}
