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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ExcavationChargeAuditLogTest {

    private Plugin plugin;
    private Logger logger;
    private ExcavationChargeAuditLog auditLog;

    @BeforeEach
    void setUp() {
        this.plugin = mock(Plugin.class);
        this.logger = mock(Logger.class);
        when(this.plugin.getLogger()).thenReturn(this.logger);

        this.auditLog = new ExcavationChargeAuditLog(this.plugin);
    }

    @Test
    void placed_namesThePlayerTheChargeAndThePosition() {
        this.auditLog.placed(
                this.playerNamed("Steve"), BlastShape.CUBOID, 3, BlockFace.NORTH, this.locationAt(10, 64, -20));

        String line = this.loggedLine();
        assertTrue(line.contains("Steve"));
        assertTrue(line.contains("level 3"));
        assertTrue(line.contains(BlastShape.CUBOID.getDisplayName()));
        assertTrue(line.contains("NORTH"));
        assertTrue(line.contains("world (10, 64, -20)"));
    }

    @Test
    void detonated_reportsTheCountAndTheCornerToCornerBounds() {
        List<Block> carved = List.of(
                this.blockAt(-3, 60, 5),
                this.blockAt(4, 64, -2),
                this.blockAt(0, 62, 0));

        this.auditLog.detonated(BlastShape.SPHERE, BlastLevel.LEVEL_2, this.locationAt(0, 64, 0), carved);

        String line = this.loggedLine();
        assertTrue(line.contains("level 2"));
        assertTrue(line.contains("carving 3 blocks"));
        assertTrue(line.contains("between (-3, 60, -2) and (4, 64, 5)"));
    }

    @Test
    void detonated_withNothingCarved_reportsAnEmptyRegion() {
        this.auditLog.detonated(BlastShape.CUBOID, BlastLevel.LEVEL_1, this.locationAt(0, 64, 0), List.of());

        assertTrue(this.loggedLine().contains("in an empty region"));
    }

    @Test
    void chainIgnited_linksTheBlastToTheWokenCharge() {
        this.auditLog.chainIgnited(this.locationAt(0, 64, 0), this.locationAt(5, 60, 0));

        String line = this.loggedLine();
        assertTrue(line.contains("blast at world (0, 64, 0)"));
        assertTrue(line.contains("charge at world (5, 60, 0)"));
    }

    private String loggedLine() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(this.logger).info(captor.capture());

        return captor.getValue();
    }

    private Player playerNamed(String name) {
        Player player = mock(Player.class);
        when(player.getName()).thenReturn(name);

        return player;
    }

    private Location locationAt(int x, int y, int z) {
        World world = mock(World.class);
        when(world.getName()).thenReturn("world");

        Location location = mock(Location.class);
        when(location.getWorld()).thenReturn(world);
        when(location.getBlockX()).thenReturn(x);
        when(location.getBlockY()).thenReturn(y);
        when(location.getBlockZ()).thenReturn(z);

        return location;
    }

    private Block blockAt(int x, int y, int z) {
        Block block = mock(Block.class);
        when(block.getX()).thenReturn(x);
        when(block.getY()).thenReturn(y);
        when(block.getZ()).thenReturn(z);

        return block;
    }
}
