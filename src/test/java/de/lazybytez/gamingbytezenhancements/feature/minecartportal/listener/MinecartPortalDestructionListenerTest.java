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
package de.lazybytez.gamingbytezenhancements.feature.minecartportal.listener;

import de.lazybytez.gamingbytezenhancements.feature.minecartportal.PortalConfiguration;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.messages.MinecartPortalMessages;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.model.MinecartPortal;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePalette;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePrefix;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class MinecartPortalDestructionListenerTest {
    private static final MessagePrefix PREFIX = MessagePrefix.of("MinecartPortals", NamedTextColor.LIGHT_PURPLE);

    private final PortalConfiguration portalConfig = mock(PortalConfiguration.class);
    private final Messenger messenger = new Messenger(MinecartPortalDestructionListenerTest.PREFIX);
    private final Player player = mock(Player.class);
    private final MinecartPortalDestructionListener listener =
            new MinecartPortalDestructionListener(this.portalConfig, this.messenger);

    private Location entryLocation;
    private World world;

    @BeforeEach
    void setUp() {
        this.world = mock(World.class);
        when(this.world.getUID()).thenReturn(UUID.randomUUID());
        this.entryLocation = new Location(this.world, 10, 64, 10);

        MinecartPortal portal = new MinecartPortal("spawn", this.entryLocation, null);
        when(this.portalConfig.getPortals()).thenReturn(List.of(portal));
    }

    @Test
    void cancelsThePlayerBreakAndSendsThePrefixedErrorMessage() {
        Block block = mock(Block.class);
        when(block.getType()).thenReturn(Material.DETECTOR_RAIL);
        when(block.getLocation()).thenReturn(this.entryLocation.clone());

        BlockBreakEvent event = new BlockBreakEvent(block, this.player);

        this.listener.onMinecartPortalDestroyedByPlayer(event);

        assertTrue(event.isCancelled());
        verify(this.player).sendMessage(this.error(MinecartPortalMessages.blockedDestruction()));
    }

    @Test
    void leavesUnrelatedBlockBreaksUncancelledAndSilent() {
        Block block = mock(Block.class);
        when(block.getType()).thenReturn(Material.STONE);

        BlockBreakEvent event = new BlockBreakEvent(block, this.player);

        this.listener.onMinecartPortalDestroyedByPlayer(event);

        assertFalse(event.isCancelled());
        verifyNoInteractions(this.player);
    }

    private Component error(Component body) {
        return this.messenger.prefixed(body.colorIfAbsent(MessagePalette.ERROR));
    }
}
