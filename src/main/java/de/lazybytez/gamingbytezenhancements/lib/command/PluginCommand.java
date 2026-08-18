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
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.List;

/**
 * A command a feature contributes to the server.
 *
 * There is no label accessor on purpose: the label lives in the returned
 * builder and is read back from it, so it cannot be declared twice and drift.
 */
public interface PluginCommand {
    /**
     * Build the command tree, root literal included.
     *
     * @return the root node of the command
     */
    LiteralArgumentBuilder<CommandSourceStack> createNode();

    /**
     * Get the description the server shows for this command.
     *
     * @return the description
     */
    String description();

    /**
     * Get the alternative labels the command answers to.
     *
     * @return the aliases, empty when the command has none
     */
    default List<String> aliases() {
        return List.of();
    }
}
