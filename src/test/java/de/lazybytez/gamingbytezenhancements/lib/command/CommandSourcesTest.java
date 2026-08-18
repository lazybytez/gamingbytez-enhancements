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

import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.Optional;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommandSourcesTest {
    @Test
    void returnsThePlayerWhenTheExecutorIsAPlayer() {
        Player player = mock(Player.class);
        CommandSourceStack source = mock(CommandSourceStack.class);
        when(source.getExecutor()).thenReturn(player);

        assertEquals(Optional.of(player), CommandSources.playerExecutor(source));
    }

    @Test
    void returnsEmptyForAConsoleSource() {
        CommandSourceStack source = mock(CommandSourceStack.class);
        when(source.getExecutor()).thenReturn(null);

        assertTrue(CommandSources.playerExecutor(source).isEmpty());
    }

    @Test
    void returnsEmptyForANonPlayerEntity() {
        CommandSourceStack source = mock(CommandSourceStack.class);
        when(source.getExecutor()).thenReturn(mock(Entity.class));

        assertTrue(CommandSources.playerExecutor(source).isEmpty());
    }
}
