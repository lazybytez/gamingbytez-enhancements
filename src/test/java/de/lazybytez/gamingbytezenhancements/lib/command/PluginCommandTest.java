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
package de.lazybytez.gamingbytezenhancements.lib.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePrefix;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PluginCommandTest {
    private static final String PERMISSION = "gamingbytez.fake.admin";

    private final CommandSender sender = mock(CommandSender.class);
    private final CommandSourceStack source = mock(CommandSourceStack.class);
    private final Messenger messenger = new Messenger(MessagePrefix.of("Fake", NamedTextColor.AQUA));
    private final PluginCommand command = this.fakeCommand();

    @Test
    void canUseAsksTheSenderForThePermissionTheCommandDeclares() {
        when(this.source.getSender()).thenReturn(this.sender);
        when(this.sender.hasPermission(PluginCommandTest.PERMISSION)).thenReturn(true);

        assertTrue(this.command.canUse(this.source));

        when(this.sender.hasPermission(PluginCommandTest.PERMISSION)).thenReturn(false);

        assertFalse(this.command.canUse(this.source));
    }

    @Test
    void sendHelpRendersTheInvokedTreeThroughTheCommandsMessenger() throws Exception {
        when(this.source.getSender()).thenReturn(this.sender);
        when(this.sender.hasPermission(PluginCommandTest.PERMISSION)).thenReturn(true);

        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        dispatcher.getRoot().addChild(this.command.createNode().build());

        dispatcher.execute("fake", this.source);

        ArgumentCaptor<Component> captor = ArgumentCaptor.forClass(Component.class);
        verify(this.sender, times(2)).sendMessage(captor.capture());
        List<String> lines = captor.getAllValues().stream()
                .map(line -> PlainTextComponentSerializer.plainText().serialize(line).trim())
                .toList();

        assertEquals(List.of("[Fake] /fake", "/fake run"), lines);
    }

    private PluginCommand fakeCommand() {
        return new PluginCommand() {
            @Override
            public LiteralArgumentBuilder<CommandSourceStack> createNode() {
                return Commands.literal("fake")
                        .requires(this::canUse)
                        .executes(this::sendHelp)
                        .then(Commands.literal("run").executes(context -> CommandResults.SUCCESS));
            }

            @Override
            public String description() {
                return "A fake command exercising the shared defaults";
            }

            @Override
            public String permission() {
                return PluginCommandTest.PERMISSION;
            }

            @Override
            public Messenger messenger() {
                return PluginCommandTest.this.messenger;
            }
        };
    }
}
