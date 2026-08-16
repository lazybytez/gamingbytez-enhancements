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

import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Builds suggestion providers over collections that change while the server runs.
 */
public final class CommandSuggestions {
    private CommandSuggestions() {
    }

    /**
     * Suggest the candidates a supplier offers, narrowed to what the sender has typed.
     *
     * @param candidates supplies the candidates, asked again on every invocation
     * @return a provider suggesting the matching candidates
     */
    public static SuggestionProvider<CommandSourceStack> fromSupplier(Supplier<Collection<String>> candidates) {
        Objects.requireNonNull(candidates, "candidates must not be null");

        return (context, builder) -> {
            String typed = builder.getRemaining().toLowerCase(Locale.ROOT);

            for (String candidate : candidates.get()) {
                if (candidate.toLowerCase(Locale.ROOT).startsWith(typed)) {
                    builder.suggest(candidate);
                }
            }

            return builder.buildFuture();
        };
    }
}
