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

import java.util.Objects;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

/**
 * The line vocabulary of one feature, bound to that feature's prefix.
 *
 * Line names are semantic rather than visual, so a call site states what a line
 * means and {@link MessagePalette} decides how it looks. Prefixed lines open a
 * message; the unprefixed indented lines continue one that is already open.
 *
 * Bodies passed as components keep the colours they already carry and inherit
 * the line colour everywhere they carry none, which lets a caller highlight a
 * subject inside an otherwise uniform line.
 *
 * Every send method targets an Adventure {@link Audience}, so command senders,
 * players and broadcast targets all go through one API.
 */
public final class Messenger {
    private static final Component INDENT = Component.text("  ");
    private static final Component BULLET = Component.text("• ", MessagePalette.DECORATION);
    private static final Component FIELD_SEPARATOR = Component.text(": ", MessagePalette.DECORATION);

    private final MessagePrefix prefix;

    /**
     * Bind a line vocabulary to a feature prefix.
     *
     * @param prefix the prefix opening every prefixed line
     */
    public Messenger(MessagePrefix prefix) {
        this.prefix = Objects.requireNonNull(prefix, "prefix must not be null");
    }

    /**
     * Send a section heading.
     *
     * @param audience the receiver
     * @param body     the heading text
     */
    public void heading(Audience audience, Component body) {
        this.line(audience, body, MessagePalette.HEADING);
    }

    /**
     * Send a section heading.
     *
     * @param audience the receiver
     * @param body     the heading text
     */
    public void heading(Audience audience, String body) {
        this.heading(audience, Component.text(body));
    }

    /**
     * Send neutral information.
     *
     * @param audience the receiver
     * @param body     the message text
     */
    public void info(Audience audience, Component body) {
        this.line(audience, body, MessagePalette.BODY);
    }

    /**
     * Send neutral information.
     *
     * @param audience the receiver
     * @param body     the message text
     */
    public void info(Audience audience, String body) {
        this.info(audience, Component.text(body));
    }

    /**
     * Report a completed mutation.
     *
     * @param audience the receiver
     * @param body     the message text
     */
    public void success(Audience audience, Component body) {
        this.line(audience, body, MessagePalette.SUCCESS);
    }

    /**
     * Report a completed mutation.
     *
     * @param audience the receiver
     * @param body     the message text
     */
    public void success(Audience audience, String body) {
        this.success(audience, Component.text(body));
    }

    /**
     * Report a recoverable problem or a call to action.
     *
     * @param audience the receiver
     * @param body     the message text
     */
    public void warning(Audience audience, Component body) {
        this.line(audience, body, MessagePalette.EMPHASIS);
    }

    /**
     * Report a recoverable problem or a call to action.
     *
     * @param audience the receiver
     * @param body     the message text
     */
    public void warning(Audience audience, String body) {
        this.warning(audience, Component.text(body));
    }

    /**
     * Report a rejected or failed operation.
     *
     * @param audience the receiver
     * @param body     the message text
     */
    public void error(Audience audience, Component body) {
        this.line(audience, body, MessagePalette.ERROR);
    }

    /**
     * Report a rejected or failed operation.
     *
     * @param audience the receiver
     * @param body     the message text
     */
    public void error(Audience audience, String body) {
        this.error(audience, Component.text(body));
    }

    /**
     * Send an indented continuation line without a prefix.
     *
     * @param audience the receiver
     * @param body     the continuation text
     */
    public void detail(Audience audience, Component body) {
        audience.sendMessage(INDENT.append(body.colorIfAbsent(MessagePalette.BODY)));
    }

    /**
     * Send an indented list entry marked with a bullet glyph and no prefix.
     *
     * @param audience the receiver
     * @param body     the entry text
     */
    public void bullet(Audience audience, Component body) {
        audience.sendMessage(INDENT.append(BULLET).append(body.colorIfAbsent(MessagePalette.BODY)));
    }

    /**
     * Send an indented labelled value without a prefix.
     *
     * The separator between label and value belongs to the presentation, so a
     * caller passes the bare label rather than punctuating it.
     *
     * @param audience the receiver
     * @param label    the name of the value
     * @param value    the value itself
     */
    public void field(Audience audience, String label, Component value) {
        audience.sendMessage(INDENT.append(Component.text(label, MessagePalette.BODY))
                .append(FIELD_SEPARATOR)
                .append(value.colorIfAbsent(MessagePalette.VALUE)));
    }

    /**
     * Build a prefixed line without sending it, for callers that deliver it themselves.
     *
     * @param body the message text
     * @return the prefix followed by the body
     */
    public Component prefixed(Component body) {
        return this.prefix.component().append(body);
    }

    private void line(Audience audience, Component body, TextColor color) {
        audience.sendMessage(this.prefixed(body.colorIfAbsent(color)));
    }
}
