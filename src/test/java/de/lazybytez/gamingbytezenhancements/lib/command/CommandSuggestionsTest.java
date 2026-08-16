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

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class CommandSuggestionsTest {
    private static final String COMMAND = "/portal inspect ";

    @Test
    void suggestsEveryCandidateForEmptyInput() throws CommandSyntaxException {
        SuggestionProvider<CommandSourceStack> provider =
                CommandSuggestions.fromSupplier(() -> List.of("north", "south", "east"));

        assertEquals(Set.of("north", "south", "east"), suggest(provider, ""));
    }

    @Test
    void filtersCaseInsensitivelyOnTheRemainingInput() throws CommandSyntaxException {
        SuggestionProvider<CommandSourceStack> provider =
                CommandSuggestions.fromSupplier(() -> List.of("north", "NORTHWEST", "south"));

        assertEquals(Set.of("north", "NORTHWEST"), suggest(provider, "No"));
    }

    @Test
    void asksTheSupplierOnEveryInvocation() throws CommandSyntaxException {
        List<String> candidates = new ArrayList<>(List.of("north"));
        SuggestionProvider<CommandSourceStack> provider = CommandSuggestions.fromSupplier(() -> candidates);

        assertEquals(Set.of("north"), suggest(provider, ""));

        candidates.add("south");

        assertEquals(Set.of("north", "south"), suggest(provider, ""));
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
