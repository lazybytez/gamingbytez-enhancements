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
package de.lazybytez.gamingbytezenhancements.feature.chatbot.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.lazybytez.gamingbytezenhancements.feature.chatbot.ChatBotFeature;
import de.lazybytez.gamingbytezenhancements.lib.command.PluginCommand;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePrefix;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatBotCommandTest {
    private static final String ADMIN_PERMISSION = "gamingbytez.chatbot.admin";

    private final ChatBotFeature feature = mock(ChatBotFeature.class);
    private final CommandSender sender = mock(CommandSender.class);
    private final CommandSourceStack source = mock(CommandSourceStack.class);
    private final Messenger messenger = new Messenger(MessagePrefix.of("ChatBot", NamedTextColor.GREEN));
    private final ChatBotCommand command = new ChatBotCommand(this.feature, this.messenger);

    @Test
    void isAPluginCommandCarryingItsLabelInTheBuiltNode() {
        assertInstanceOf(PluginCommand.class, this.command);
        assertEquals("chatbot", this.command.createNode().build().getName());
    }

    @Test
    void keepsTheDescriptionAndTheAliasTheServerRegisters() {
        assertEquals("Manage the Chat Bot of the GamingBytez Enhancements plugin", this.command.description());
        assertEquals(List.of("gbcb"), this.command.aliases());
    }

    @Test
    void exposesTheReloadGrammar() throws Exception {
        this.givenPermission(true);

        assertEquals(Set.of("reload"), this.suggestionsFor("chatbot "));
        assertEquals(Set.of("responses", "settings"), this.suggestionsFor("chatbot reload "));
    }

    @Test
    void hidesTheCommandFromASenderWithoutThePermission() throws Exception {
        this.givenPermission(false);

        assertEquals(Set.of(), this.suggestionsFor("chatbot "));
    }

    @Test
    void reloadsOnlyTheResponsesOnTheResponsesBranch() throws Exception {
        this.givenPermission(true);

        this.dispatcher().execute("chatbot reload responses", this.source);

        verify(this.feature).reloadStaticResponses(any());
        verify(this.feature, never()).reloadAiSettings();
    }

    @Test
    void reloadsOnlyTheSettingsOnTheSettingsBranch() throws Exception {
        this.givenPermission(true);

        this.dispatcher().execute("chatbot reload settings", this.source);

        verify(this.feature).reloadAiSettings();
        verify(this.feature, never()).reloadStaticResponses(any());
    }

    @Test
    void reloadsBothOnTheBareReload() throws Exception {
        this.givenPermission(true);

        this.dispatcher().execute("chatbot reload", this.source);

        verify(this.feature).reloadStaticResponses(any());
        verify(this.feature).reloadAiSettings();
    }

    @Test
    void wordsTheResponsesOutcomeToTheSenderWhoAsked() throws Exception {
        this.givenPermission(true);
        when(this.feature.staticResponseCount()).thenReturn(4);
        this.dispatcher().execute("chatbot reload responses", this.source);

        ArgumentCaptor<Consumer<Boolean>> callback = ArgumentCaptor.forClass(Consumer.class);
        verify(this.feature).reloadStaticResponses(callback.capture());
        callback.getValue().accept(true);

        ArgumentCaptor<Component> line = ArgumentCaptor.forClass(Component.class);
        verify(this.sender).sendMessage(line.capture());
        assertTrue(PlainTextComponentSerializer.plainText().serialize(line.getValue())
                .contains("Reloaded 4 static responses."));
    }

    @Test
    void paperPluginDeclaresTheChatBotAdminPermission() throws IOException {
        try (InputStream resource = this.getClass().getClassLoader().getResourceAsStream("paper-plugin.yml")) {
            String contents = new String(resource.readAllBytes(), StandardCharsets.UTF_8);

            assertTrue(contents.contains("gamingbytez.chatbot.admin:\n"
                    + "    description: Allows management of the Chat Bot\n"
                    + "    default: op"));
        }
    }

    private void givenPermission(boolean granted) {
        when(this.source.getSender()).thenReturn(this.sender);
        when(this.sender.hasPermission(ChatBotCommandTest.ADMIN_PERMISSION)).thenReturn(granted);
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
}
