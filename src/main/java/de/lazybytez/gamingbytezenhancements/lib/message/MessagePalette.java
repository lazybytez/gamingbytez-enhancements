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
package de.lazybytez.gamingbytezenhancements.lib.message;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/**
 * The semantic colour vocabulary shared by every message the plugin sends.
 *
 * Call sites pick a token by the role the text plays, never by the colour they
 * want, so a role can be recoloured plugin-wide in one place. No two tokens
 * resolve to the same colour, which keeps the roles distinguishable in chat.
 *
 * A feature brand colour is deliberately absent: each feature owns its brand and
 * supplies it when building its {@link MessagePrefix}.
 */
public final class MessagePalette {
    /**
     * Brackets, bullets and separators.
     */
    public static final TextColor DECORATION = NamedTextColor.DARK_GRAY;

    /**
     * Neutral copy and item lore body.
     */
    public static final TextColor BODY = NamedTextColor.GRAY;

    /**
     * Data values inside a line.
     */
    public static final TextColor VALUE = NamedTextColor.WHITE;

    /**
     * The named thing an operation acts on.
     */
    public static final TextColor SUBJECT = NamedTextColor.YELLOW;

    /**
     * Section headings.
     */
    public static final TextColor HEADING = NamedTextColor.AQUA;

    /**
     * Calls to action, broadcasts and recoverable problems.
     */
    public static final TextColor EMPHASIS = NamedTextColor.GOLD;

    /**
     * A completed mutation.
     */
    public static final TextColor SUCCESS = NamedTextColor.GREEN;

    /**
     * A rejected or failed operation.
     */
    public static final TextColor ERROR = NamedTextColor.RED;

    private MessagePalette() {
    }
}
