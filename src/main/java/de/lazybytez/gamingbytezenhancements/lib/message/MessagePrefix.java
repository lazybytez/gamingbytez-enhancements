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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

/**
 * The bracketed feature name that opens every prefixed line of a feature.
 *
 * The brackets carry {@link MessagePalette#DECORATION} while the feature name
 * carries the brand colour the feature chose, which is what lets players tell
 * two features apart at a glance without the palette owning brand identity.
 */
public final class MessagePrefix {
    private final Component component;

    private MessagePrefix(Component component) {
        this.component = component;
    }

    /**
     * Build the prefix of a feature.
     *
     * @param featureName the name shown between the brackets
     * @param brandColor  the colour identifying the feature
     * @return the rendered prefix
     */
    public static MessagePrefix of(String featureName, TextColor brandColor) {
        Objects.requireNonNull(featureName, "featureName must not be null");
        Objects.requireNonNull(brandColor, "brandColor must not be null");

        return new MessagePrefix(Component.text("[", MessagePalette.DECORATION)
                .append(Component.text(featureName, brandColor))
                .append(Component.text("] ", MessagePalette.DECORATION)));
    }

    /**
     * Get the rendered prefix, trailing separator space included.
     *
     * @return the prefix component
     */
    public Component component() {
        return this.component;
    }
}
