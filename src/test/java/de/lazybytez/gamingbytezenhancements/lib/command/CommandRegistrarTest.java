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

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CommandRegistrarTest {
    @Test
    void handsNodeDescriptionAndAliasesToTheRegistrar() {
        Commands registrar = mock(Commands.class);

        CommandRegistrar.registerAll(
                registrar,
                List.of(command("minecartportals", "Manage portals", List.of("gbmcp")))
        );

        verify(registrar).register(
                argThat(node -> "minecartportals".equals(node.getName())),
                eq("Manage portals"),
                eq(List.of("gbmcp"))
        );
    }

    @Test
    void registersSeveralCommandsInOneCall() {
        Commands registrar = mock(Commands.class);

        CommandRegistrar.registerAll(
                registrar,
                List.of(command("first", "First", List.of()), command("second", "Second", List.of()))
        );

        verify(registrar).register(argThat(node -> "first".equals(node.getName())), eq("First"), eq(List.of()));
        verify(registrar).register(argThat(node -> "second".equals(node.getName())), eq("Second"), eq(List.of()));
    }

    @Test
    void buildsEachCommandTreeFreshlyOnEveryRegistrationPass() {
        Commands firstRegistrar = mock(Commands.class);
        Commands secondRegistrar = mock(Commands.class);
        List<PluginCommand> commands = List.of(command("reloadable", "Reloadable", List.of()));

        CommandRegistrar.registerAll(firstRegistrar, commands);
        CommandRegistrar.registerAll(secondRegistrar, commands);

        verify(firstRegistrar).register(argThat(node -> "reloadable".equals(node.getName())),
                eq("Reloadable"), eq(List.of()));
        verify(secondRegistrar).register(argThat(node -> "reloadable".equals(node.getName())),
                eq("Reloadable"), eq(List.of()));
    }

    private static PluginCommand command(String label, String description, List<String> aliases) {
        return new PluginCommand() {
            @Override
            public LiteralArgumentBuilder<CommandSourceStack> createNode() {
                return Commands.literal(label);
            }

            @Override
            public String description() {
                return description;
            }

            @Override
            public List<String> aliases() {
                return aliases;
            }

            @Override
            public String permission() {
                return "gamingbytez.test.admin";
            }

            @Override
            public Messenger messenger() {
                return mock(Messenger.class);
            }
        };
    }
}
