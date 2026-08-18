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

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.PortalConfiguration;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.model.MinecartPortal;
import de.lazybytez.gamingbytezenhancements.lib.command.PluginCommand;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePrefix;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MinecartPortalCommandTest {
    private static final String ADMIN_PERMISSION = "gamingbytez.minecartportals.admin";

    private final Plugin plugin = mock(Plugin.class);
    private final PortalConfiguration portalConfig = mock(PortalConfiguration.class);
    private final CommandSender sender = mock(CommandSender.class);
    private final CommandSourceStack source = mock(CommandSourceStack.class);
    private final Messenger messenger = new Messenger(MessagePrefix.of("MinecartPortals", NamedTextColor.LIGHT_PURPLE));
    private final MinecartPortalCommand command =
            new MinecartPortalCommand(this.plugin, this.portalConfig, this.messenger);

    @Test
    void isAPluginCommandCarryingItsLabelInTheBuiltNode() {
        assertInstanceOf(PluginCommand.class, this.command);
        assertEquals("minecartportals", this.command.createNode().build().getName());
    }

    @Test
    void keepsTheDescriptionAndTheAliasTheServerRegisters() {
        assertEquals("Manage the Minecart Portals of the GamingBytez Enhancements plugin", this.command.description());
        assertEquals(List.of("gbmcp"), this.command.aliases());
    }

    @Test
    void exposesEverySubcommandOfTheGrammar() throws Exception {
        this.givenPermission(true);

        Set<String> suggestions = this.suggestionsFor("minecartportals ");

        assertEquals(Set.of("add", "entry", "exit", "delete", "list", "inspect", "reload"), suggestions);
    }

    @Test
    void suggestsNoPortalNameWhenANewPortalIsNamed() throws Exception {
        this.givenPermission(true);
        when(this.portalConfig.getPortals()).thenReturn(List.of(new MinecartPortal("spawn", null, null)));

        assertEquals(Set.of(), this.suggestionsFor("minecartportals add "));
    }

    @Test
    void suggestsTheRegisteredPortalNamesWhenAnExistingPortalIsNamed() throws Exception {
        this.givenPermission(true);
        when(this.portalConfig.getPortals()).thenReturn(List.of(new MinecartPortal("spawn", null, null)));

        assertEquals(Set.of("spawn"), this.suggestionsFor("minecartportals inspect "));
    }

    @Test
    void allowsASenderHoldingTheAdministrationPermission() {
        this.givenPermission(true);

        assertTrue(this.command.createNode().build().canUse(this.source));
    }

    @Test
    void rejectsAnOperatorWithoutTheAdministrationPermission() {
        this.givenPermission(false);
        when(this.sender.isOp()).thenReturn(true);

        assertFalse(this.command.createNode().build().canUse(this.source));
    }

    @Test
    void answersTheBareCommandWithTheHelpReadFromTheTree() throws Exception {
        this.givenPermission(true);

        this.dispatcher().execute("minecartportals", this.source);

        assertEquals(Set.of(
                "[MinecartPortals] /minecartportals",
                "/minecartportals add <name>",
                "/minecartportals entry <name>",
                "/minecartportals exit <name>",
                "/minecartportals delete <name>",
                "/minecartportals list",
                "/minecartportals inspect <name>",
                "/minecartportals reload"), Set.copyOf(this.renderedLines(8)));
    }

    @Test
    void paperPluginDeclaresMinecartPortalAdminPermission() throws IOException {
        try (InputStream resource = this.getClass().getClassLoader().getResourceAsStream("paper-plugin.yml")) {
            String contents = new String(resource.readAllBytes(), StandardCharsets.UTF_8);

            assertTrue(contents.contains("permissions:\n"
                    + "  gamingbytez.minecartportals.admin:\n"
                    + "    description: Allows management of Minecart Portals\n"
                    + "    default: op"));
        }
    }

    private void givenPermission(boolean granted) {
        when(this.source.getSender()).thenReturn(this.sender);
        when(this.sender.hasPermission(MinecartPortalCommandTest.ADMIN_PERMISSION)).thenReturn(granted);
    }

    private CommandDispatcher<CommandSourceStack> dispatcher() {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        LiteralCommandNode<CommandSourceStack> root = this.command.createNode().build();
        dispatcher.getRoot().addChild(root);

        return dispatcher;
    }

    private Set<String> suggestionsFor(String input) throws Exception {
        CommandDispatcher<CommandSourceStack> dispatcher = this.dispatcher();

        return dispatcher.getCompletionSuggestions(dispatcher.parse(input, this.source)).get().getList().stream()
                .map(suggestion -> suggestion.getText())
                .collect(Collectors.toSet());
    }

    private List<String> renderedLines(int expectedCount) {
        ArgumentCaptor<Component> captor = ArgumentCaptor.forClass(Component.class);
        verify(this.sender, times(expectedCount)).sendMessage(captor.capture());

        return captor.getAllValues().stream()
                .map(line -> PlainTextComponentSerializer.plainText().serialize(line).trim())
                .toList();
    }
}
