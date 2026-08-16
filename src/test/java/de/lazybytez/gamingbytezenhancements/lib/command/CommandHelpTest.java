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

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CommandHelpTest {
    private static final Messenger MESSENGER = new Messenger(MessagePrefix.of("Test", NamedTextColor.AQUA));

    private final CommandSender sender = mock(CommandSender.class);
    private final CommandSourceStack source = mock(CommandSourceStack.class);

    @Test
    void rendersOneLinePerExecutableBranch() {
        LiteralCommandNode<CommandSourceStack> root = Commands.literal("minecartportals")
                .then(Commands.literal("list").executes(context -> CommandResults.SUCCESS))
                .then(Commands.literal("reload").executes(context -> CommandResults.SUCCESS))
                .build();

        this.sendHelp(root);

        assertEquals(List.of("/minecartportals list", "/minecartportals reload"), this.renderedLines(2));
    }

    @Test
    void rendersArgumentsInAngleBracketsAndLiteralsVerbatim() {
        LiteralCommandNode<CommandSourceStack> root = Commands.literal("minecartportals")
                .then(Commands.literal("add")
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(context -> CommandResults.SUCCESS)))
                .build();

        this.sendHelp(root);

        assertEquals(List.of("/minecartportals add <name>"), this.renderedLines(1));
    }

    @Test
    void rendersTheInvokedLabelWhenTheCommandIsCalledThroughItsAlias() {
        LiteralCommandNode<CommandSourceStack> target = Commands.literal("minecartportals")
                .then(Commands.literal("list").executes(context -> CommandResults.SUCCESS))
                .build();
        LiteralCommandNode<CommandSourceStack> alias = Commands.literal("gbmcp").redirect(target).build();

        this.sendHelp(alias);

        assertEquals(List.of("/gbmcp list"), this.renderedLines(1));
    }

    @Test
    void omitsBranchesTheSourceCannotUse() {
        LiteralCommandNode<CommandSourceStack> root = Commands.literal("minecartportals")
                .then(Commands.literal("list").executes(context -> CommandResults.SUCCESS))
                .then(Commands.literal("delete").requires(candidate -> false)
                        .then(Commands.argument("name", StringArgumentType.word())
                                .executes(context -> CommandResults.SUCCESS)))
                .build();

        this.sendHelp(root);

        assertEquals(List.of("/minecartportals list"), this.renderedLines(1));
    }

    @Test
    void rendersNothingWhenNoBranchIsExecutable() {
        LiteralCommandNode<CommandSourceStack> root = Commands.literal("minecartportals")
                .then(Commands.literal("portal").then(Commands.literal("orphan")))
                .build();

        this.sendHelp(root);

        verifyNoInteractions(this.sender);
    }

    private void sendHelp(LiteralCommandNode<CommandSourceStack> invokedNode) {
        when(this.source.getSender()).thenReturn(this.sender);

        CommandHelp.send(this.source, MESSENGER, invokedNode);
    }

    private List<String> renderedLines(int expectedCount) {
        ArgumentCaptor<Component> captor = ArgumentCaptor.forClass(Component.class);
        verify(this.sender, times(expectedCount)).sendMessage(captor.capture());

        return captor.getAllValues().stream()
                .map(line -> PlainTextComponentSerializer.plainText().serialize(line).trim())
                .toList();
    }
}
