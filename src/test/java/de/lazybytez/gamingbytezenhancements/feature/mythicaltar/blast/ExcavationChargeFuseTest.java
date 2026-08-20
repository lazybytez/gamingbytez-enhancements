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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ExcavationChargeFuseTest {

    private static final int GENEROUS_SAMPLE_CAP = 100_000;

    @Test
    void playerFuseRunsLongerThanTheChainFuse() {
        assertEquals(100L, ExcavationChargeFuse.PLAYER_FUSE_TICKS);
        assertEquals(10L, ExcavationChargeFuse.CHAIN_FUSE_TICKS);
    }

    @Test
    void outlineKeepsTheCornerOfTheVolume() {
        List<BlastVector> outline = ExcavationChargeFuse.outlineOffsets(
                new CuboidBlastGeometry(BlastLevel.LEVEL_1), ExcavationChargeFuseTest.GENEROUS_SAMPLE_CAP);

        assertTrue(outline.contains(new BlastVector(-4, -4, -4)));
    }

    @Test
    void outlineDropsOffsetsSurroundedByTheVolume() {
        List<BlastVector> outline = ExcavationChargeFuse.outlineOffsets(
                new CuboidBlastGeometry(BlastLevel.LEVEL_1), ExcavationChargeFuseTest.GENEROUS_SAMPLE_CAP);

        assertFalse(outline.contains(new BlastVector(0, -1, 0)));
    }

    @Test
    void outlineOnlyContainsOffsetsInsideTheVolume() {
        CuboidBlastGeometry geometry = new CuboidBlastGeometry(BlastLevel.LEVEL_1);

        List<BlastVector> outline =
                ExcavationChargeFuse.outlineOffsets(geometry, ExcavationChargeFuseTest.GENEROUS_SAMPLE_CAP);

        assertTrue(outline.stream().allMatch(geometry::contains));
    }

    @Test
    void outlineIsThinnedDownToTheSampleCap() {
        List<BlastVector> outline =
                ExcavationChargeFuse.outlineOffsets(new CuboidBlastGeometry(BlastLevel.LEVEL_4), 50);

        assertFalse(outline.isEmpty());
        assertTrue(outline.size() <= 50);
    }

    @Test
    void outlineIsEmptyWhenNoSampleIsAllowed() {
        List<BlastVector> outline =
                ExcavationChargeFuse.outlineOffsets(new CuboidBlastGeometry(BlastLevel.LEVEL_1), 0);

        assertTrue(outline.isEmpty());
    }
}
