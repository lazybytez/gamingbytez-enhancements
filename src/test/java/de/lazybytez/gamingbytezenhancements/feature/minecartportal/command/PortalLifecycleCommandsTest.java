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
import org.bukkit.command.CommandSender;
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

class PortalLifecycleCommandsTest {
    private static final MessagePrefix PREFIX = MessagePrefix.of("MinecartPortals", NamedTextColor.LIGHT_PURPLE);

    private final PortalConfiguration portalConfig = mock(PortalConfiguration.class);
    private final PortalAutoSave autoSave = mock(PortalAutoSave.class);
    private final Messenger messenger = new Messenger(PortalLifecycleCommandsTest.PREFIX);
    private final CommandSender sender = mock(CommandSender.class);
    private final CommandSourceStack source = mock(CommandSourceStack.class);
    private final PortalLifecycleCommands commands = new PortalLifecycleCommands(
            this.portalConfig,
            this.messenger,
            new PortalArguments(this.portalConfig),
            this.autoSave);

    @Test
    void addRejectsANameLongerThanTheLimit() throws Exception {
        int result = this.execute("add 12345678901234567");

        assertEquals(CommandResults.FAILURE, result);
        verify(this.sender).sendMessage(this.error(MinecartPortalMessages.nameTooLong()));
        verify(this.portalConfig, never()).addPortal(any());
        verifyNoInteractions(this.autoSave);
    }

    @Test
    void addRejectsANonAlphanumericName() throws Exception {
        int result = this.execute("add spawn-portal");

        assertEquals(CommandResults.FAILURE, result);
        verify(this.sender).sendMessage(this.error(MinecartPortalMessages.nameNotAlphanumeric()));
        verify(this.portalConfig, never()).addPortal(any());
        verifyNoInteractions(this.autoSave);
    }

    @Test
    void addRejectsANameThatIsAlreadyTaken() throws Exception {
        when(this.portalConfig.addPortal(any())).thenReturn(false);

        int result = this.execute("add spawn");

        assertEquals(CommandResults.FAILURE, result);
        verify(this.sender).sendMessage(this.error(MinecartPortalMessages.alreadyExists("spawn")));
        verifyNoInteractions(this.autoSave);
    }

    @Test
    void addRegistersThePortalAndRequestsExactlyOneSave() throws Exception {
        when(this.portalConfig.addPortal(any())).thenReturn(true);

        int result = this.execute("add spawn");

        assertEquals(CommandResults.SUCCESS, result);
        verify(this.portalConfig).addPortal(argThat(portal -> portal.getName().equals("spawn")
                && portal.getPortal() == null
                && portal.getDestination() == null));
        verify(this.autoSave, times(1)).requestSave(this.sender);
        verify(this.sender).sendMessage(this.success(MinecartPortalMessages.added("spawn")));
    }

    @Test
    void deleteReportsAPortalThatDoesNotExist() throws Exception {
        int result = this.execute("delete spawn");

        assertEquals(CommandResults.FAILURE, result);
        verify(this.sender).sendMessage(this.error(MinecartPortalMessages.notFound("spawn")));
        verify(this.portalConfig, never()).deletePortal(any());
        verifyNoInteractions(this.autoSave);
    }

    @Test
    void deleteReportsADeletionThatDidNotTakeEffect() throws Exception {
        MinecartPortal portal = new MinecartPortal("spawn", null, null);
        when(this.portalConfig.getPortalByName("spawn")).thenReturn(portal);
        when(this.portalConfig.deletePortal(portal)).thenReturn(false);

        int result = this.execute("delete spawn");

        assertEquals(CommandResults.FAILURE, result);
        verify(this.sender).sendMessage(this.error(MinecartPortalMessages.deleteFailed("spawn")));
        verifyNoInteractions(this.autoSave);
    }

    @Test
    void deleteRemovesThePortalAndRequestsExactlyOneSave() throws Exception {
        MinecartPortal portal = new MinecartPortal("spawn", null, null);
        when(this.portalConfig.getPortalByName("spawn")).thenReturn(portal);
        when(this.portalConfig.deletePortal(portal)).thenReturn(true);

        int result = this.execute("delete spawn");

        assertEquals(CommandResults.SUCCESS, result);
        verify(this.portalConfig).deletePortal(portal);
        verify(this.autoSave, times(1)).requestSave(this.sender);
        verify(this.sender).sendMessage(this.success(MinecartPortalMessages.deleted("spawn")));
    }

    private int execute(String input) throws Exception {
        when(this.source.getSender()).thenReturn(this.sender);

        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        dispatcher.register(this.commands.add());
        dispatcher.register(this.commands.delete());

        return dispatcher.execute(input, this.source);
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
