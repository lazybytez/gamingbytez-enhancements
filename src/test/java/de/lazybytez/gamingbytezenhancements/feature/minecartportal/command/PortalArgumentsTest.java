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

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.PortalConfiguration;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.model.MinecartPortal;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PortalArgumentsTest {
    private static final String COMMAND = "/minecartportals inspect ";

    private final PortalConfiguration portalConfig = mock(PortalConfiguration.class);
    private final PortalArguments arguments = new PortalArguments(this.portalConfig);

    @Test
    void newNameAttachesNoSuggestionProvider() {
        RequiredArgumentBuilder<CommandSourceStack, String> builder = this.arguments.newName();

        assertNull(builder.getSuggestionsProvider());
    }

    @Test
    void existingNameSuggestsRegisteredPortalNames() throws CommandSyntaxException {
        List<MinecartPortal> portals = List.of(new MinecartPortal("north", null, null),
                new MinecartPortal("south", null, null));
        when(this.portalConfig.getPortals()).thenReturn(portals);

        assertEquals(Set.of("north", "south"), suggest(this.arguments.existingName(), ""));
    }

    @Test
    void existingNameReflectsPortalsAddedLater() throws CommandSyntaxException {
        List<MinecartPortal> portals = new ArrayList<>(List.of(new MinecartPortal("north", null, null)));
        when(this.portalConfig.getPortals()).thenReturn(portals);
        SuggestionProvider<CommandSourceStack> provider = this.arguments.existingName().getSuggestionsProvider();

        assertEquals(Set.of("north"), suggest(provider, ""));

        portals.add(new MinecartPortal("south", null, null));

        assertEquals(Set.of("north", "south"), suggest(provider, ""));
    }

    @Test
    void everyFactoryCallReturnsItsOwnBuilder() {
        assertNotSame(this.arguments.newName(), this.arguments.newName());
        assertNotSame(this.arguments.existingName(), this.arguments.existingName());
    }

    @Test
    void bothFactoriesUseTheSameArgumentName() {
        assertEquals(this.arguments.newName().getName(), this.arguments.existingName().getName());
    }

    @Test
    void readsTheNameArgumentFromTheContext() {
        String argumentName = this.arguments.newName().getName();
        CommandContext<CommandSourceStack> context = context();
        when(context.getArgument(argumentName, String.class)).thenReturn("north");

        assertEquals("north", this.arguments.name(context));
    }

    @Test
    void suggestsNothingWhenNoPortalIsRegistered() throws CommandSyntaxException {
        when(this.portalConfig.getPortals()).thenReturn(List.of());

        assertEquals(Set.of(), suggest(this.arguments.existingName(), ""));
    }

    @Test
    void existingNameFiltersOnTheTypedInput() throws CommandSyntaxException {
        List<MinecartPortal> portals = List.of(new MinecartPortal("north", null, null),
                new MinecartPortal("south", null, null));
        when(this.portalConfig.getPortals()).thenReturn(portals);

        assertEquals(Set.of("north"), suggest(this.arguments.existingName(), "no"));
    }

    private static Set<String> suggest(RequiredArgumentBuilder<CommandSourceStack, String> builder, String remaining)
            throws CommandSyntaxException {
        return suggest(builder.getSuggestionsProvider(), remaining);
    }

    private static Set<String> suggest(SuggestionProvider<CommandSourceStack> provider, String remaining)
            throws CommandSyntaxException {
        SuggestionsBuilder builder = new SuggestionsBuilder(COMMAND + remaining, COMMAND.length());
        List<Suggestion> suggestions = provider.getSuggestions(context(), builder).join().getList();

        return Set.copyOf(suggestions.stream().map(Suggestion::getText).toList());
    }

    @SuppressWarnings("unchecked")
    private static CommandContext<CommandSourceStack> context() {
        return mock(CommandContext.class);
    }
}
