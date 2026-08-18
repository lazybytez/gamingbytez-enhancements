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
package de.lazybytez.gamingbytezenhancements.feature.minecartportal.command;

import de.lazybytez.gamingbytezenhancements.feature.minecartportal.messages.MinecartPortalMessages;
import com.mojang.brigadier.CommandDispatcher;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.PortalConfiguration;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.model.MinecartPortal;
import de.lazybytez.gamingbytezenhancements.lib.command.CommandResults;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePalette;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePrefix;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PortalLocationCommandsTest {
    private static final MessagePrefix PREFIX = MessagePrefix.of("MinecartPortals", NamedTextColor.LIGHT_PURPLE);

    private final PortalConfiguration portalConfig = mock(PortalConfiguration.class);
    private final PortalAutoSave autoSave = mock(PortalAutoSave.class);
    private final Messenger messenger = new Messenger(PortalLocationCommandsTest.PREFIX);
    private final CommandSender sender = mock(CommandSender.class);
    private final CommandSourceStack source = mock(CommandSourceStack.class);
    private final PortalLocationCommands commands = new PortalLocationCommands(
            this.portalConfig,
            this.messenger,
            new PortalArguments(this.portalConfig),
            this.autoSave);

    @Test
    void entryRejectsASourceWithoutAPlayerExecutor() throws Exception {
        int result = this.execute("entry spawn", null, null);

        assertEquals(CommandResults.FAILURE, result);
        verify(this.sender).sendMessage(this.error(MinecartPortalMessages.playerOnly()));
        verify(this.portalConfig, never()).getPortalByName(any());
        verifyNoInteractions(this.autoSave);
    }

    @Test
    void exitRejectsASourceWithoutAPlayerExecutor() throws Exception {
        int result = this.execute("exit spawn", null, null);

        assertEquals(CommandResults.FAILURE, result);
        verify(this.sender).sendMessage(this.error(MinecartPortalMessages.playerOnly()));
        verify(this.portalConfig, never()).getPortalByName(any());
        verifyNoInteractions(this.autoSave);
    }

    @Test
    void entryReportsAPortalThatDoesNotExist() throws Exception {
        int result = this.execute("entry spawn", this.playerOn(Material.DETECTOR_RAIL), this.rail(Material.DETECTOR_RAIL));

        assertEquals(CommandResults.FAILURE, result);
        verify(this.sender).sendMessage(this.error(MinecartPortalMessages.notFound("spawn")));
        verify(this.portalConfig, never()).updatePortal(any());
        verifyNoInteractions(this.autoSave);
    }

    @Test
    void entryRejectsABlockThatIsNotADetectorRail() throws Exception {
        when(this.portalConfig.getPortalByName("spawn")).thenReturn(new MinecartPortal("spawn", null, null));

        int result = this.execute("entry spawn", this.playerOn(Material.RAIL), this.rail(Material.RAIL));

        assertEquals(CommandResults.FAILURE, result);
        verify(this.sender).sendMessage(this.error(MinecartPortalMessages.entryNeedsDetectorRail()));
        verify(this.portalConfig, never()).updatePortal(any());
        verifyNoInteractions(this.autoSave);
    }

    @Test
    void exitRejectsABlockThatIsNotARail() throws Exception {
        when(this.portalConfig.getPortalByName("spawn")).thenReturn(new MinecartPortal("spawn", null, null));

        int result = this.execute("exit spawn", this.playerOn(Material.DETECTOR_RAIL), this.rail(Material.DETECTOR_RAIL));

        assertEquals(CommandResults.FAILURE, result);
        verify(this.sender).sendMessage(this.error(MinecartPortalMessages.exitNeedsRail()));
        verify(this.portalConfig, never()).updatePortal(any());
        verifyNoInteractions(this.autoSave);
    }

    @Test
    void entryReportsAnUpdateThatDidNotTakeEffect() throws Exception {
        when(this.portalConfig.getPortalByName("spawn")).thenReturn(new MinecartPortal("spawn", null, null));
        when(this.portalConfig.updatePortal(any())).thenReturn(false);

        int result = this.execute("entry spawn", this.playerOn(Material.DETECTOR_RAIL), this.rail(Material.DETECTOR_RAIL));

        assertEquals(CommandResults.FAILURE, result);
        verify(this.sender).sendMessage(this.error(MinecartPortalMessages.updateFailed("spawn")));
        verifyNoInteractions(this.autoSave);
    }

    @Test
    void entryStoresThePlayerLocationRatherThanTheSourceLocation() throws Exception {
        Location sourceLocation = this.rail(Material.DETECTOR_RAIL);
        Location playerLocation = this.rail(Material.DETECTOR_RAIL);
        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(playerLocation);
        when(this.portalConfig.getPortalByName("spawn")).thenReturn(new MinecartPortal("spawn", null, null));
        when(this.portalConfig.updatePortal(any())).thenReturn(true);

        int result = this.execute("entry spawn", player, sourceLocation);

        assertEquals(CommandResults.SUCCESS, result);
        verify(this.portalConfig).updatePortal(argThat(updated -> updated.getName().equals("spawn")
                && updated.getPortal() == playerLocation
                && updated.getDestination() == null));
        verify(this.autoSave, times(1)).requestSave(this.sender);
        verify(this.sender).sendMessage(this.success(MinecartPortalMessages.entryUpdated("spawn")));
    }

    @Test
    void entryChecksTheRailUnderThePlayerRatherThanUnderTheSource() throws Exception {
        Location sourceOnRail = this.rail(Material.DETECTOR_RAIL);
        Location playerOffRail = this.rail(Material.STONE);
        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(playerOffRail);
        when(this.portalConfig.getPortalByName("spawn")).thenReturn(new MinecartPortal("spawn", null, null));

        int result = this.execute("entry spawn", player, sourceOnRail);

        assertEquals(CommandResults.FAILURE, result);
        verify(this.sender).sendMessage(this.error(MinecartPortalMessages.entryNeedsDetectorRail()));
        verify(this.portalConfig, never()).updatePortal(any());
    }

    @Test
    void exitStoresThePlayerLocationAndKeepsTheExistingEntry() throws Exception {
        Location existingEntry = mock(Location.class);
        when(existingEntry.clone()).thenReturn(existingEntry);
        Location railLocation = this.rail(Material.RAIL);
        Location playerLocation = this.rail(Material.RAIL);
        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(playerLocation);
        MinecartPortal existing = new MinecartPortal("spawn", existingEntry, null);
        when(this.portalConfig.getPortalByName("spawn")).thenReturn(existing);
        when(this.portalConfig.updatePortal(any())).thenReturn(true);

        int result = this.execute("exit spawn", player, railLocation);

        assertEquals(CommandResults.SUCCESS, result);
        verify(this.portalConfig).updatePortal(argThat(updated -> updated.getPortal() == existingEntry
                && updated.getDestination() == playerLocation));
        verify(this.autoSave, times(1)).requestSave(this.sender);
        verify(this.sender).sendMessage(this.success(MinecartPortalMessages.exitUpdated("spawn")));
    }

    private int execute(String input, Player executor, Location location) throws Exception {
        when(this.source.getSender()).thenReturn(this.sender);
        when(this.source.getExecutor()).thenReturn(executor);
        when(this.source.getLocation()).thenReturn(location);

        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        dispatcher.register(this.commands.entry());
        dispatcher.register(this.commands.exit());

        return dispatcher.execute(input, this.source);
    }

    private Player playerOn(Material material) {
        Location location = this.rail(material);
        Player player = mock(Player.class);
        when(player.getLocation()).thenReturn(location);

        return player;
    }

    private Location rail(Material material) {
        Location location = mock(Location.class);
        Block block = mock(Block.class);
        when(block.getType()).thenReturn(material);
        when(location.getBlock()).thenReturn(block);
        when(location.clone()).thenReturn(location);

        return location;
    }

    private Component error(Component body) {
        return this.line(body, MessagePalette.ERROR);
    }

    private Component success(Component body) {
        return this.line(body, MessagePalette.SUCCESS);
    }

    private Component line(Component body, TextColor color) {
        return this.messenger.prefixed(body.colorIfAbsent(color));
    }
}
