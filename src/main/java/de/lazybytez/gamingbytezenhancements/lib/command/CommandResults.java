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

import com.mojang.brigadier.Command;

/**
 * The two status codes a command handler returns.
 *
 * Brigadier reports success through a result count, which reads as a bare
 * literal at the call site. These names say what the number means.
 */
public final class CommandResults {
    /**
     * The handler did its work.
     */
    public static final int SUCCESS = Command.SINGLE_SUCCESS;

    /**
     * The handler refused or could not do its work.
     */
    public static final int FAILURE = 0;

    private CommandResults() {
    }
}
