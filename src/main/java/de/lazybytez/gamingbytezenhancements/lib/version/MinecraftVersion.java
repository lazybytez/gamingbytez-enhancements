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
package de.lazybytez.gamingbytezenhancements.lib.version;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.regex.Pattern;

/**
 * The drop a Minecraft version belongs to.
 *
 * A version reads as {@code YY.D.H}, where the first two segments name the drop
 * and the third counts hotfixes within it. The legacy scheme has the same shape,
 * so {@code 1.21.11} is patch 11 of drop {@code 1.21}. Only the drop is modelled,
 * because a hotfix essentially never breaks plugin API and a comparison at exact
 * version granularity would flag every Mojang hotfix as a mismatch.
 */
public record MinecraftVersion(int major, int minor) {
    private static final int DROP_SEGMENT_COUNT = 2;
    private static final Pattern SEGMENT_SEPARATOR = Pattern.compile("\\.");
    private static final Pattern NUMERIC_SEGMENT = Pattern.compile("\\d+");

    /**
     * Guards the canonical constructor, which is public because the record is.
     *
     * @throws IllegalArgumentException if a segment is negative
     */
    public MinecraftVersion {
        if (major < 0 || minor < 0) {
            throw new IllegalArgumentException("A version segment must not be negative");
        }
    }

    /**
     * Reads the drop out of a raw version string.
     *
     * Unreadable input is absent rather than an exception, because the caller
     * decides what an unknown version means and a server hands over whatever
     * string it likes. A pre-release such as {@code 26.2-rc-2} is unreadable on
     * purpose: a release candidate is not the release it precedes.
     *
     * @param rawVersion the version string to read, may be null
     * @return the drop, or empty if the first two segments are not both numeric
     */
    public static Optional<MinecraftVersion> parse(String rawVersion) {
        if (rawVersion == null || rawVersion.isBlank()) {
            return Optional.empty();
        }

        String[] segments = MinecraftVersion.SEGMENT_SEPARATOR.split(rawVersion);

        if (segments.length < MinecraftVersion.DROP_SEGMENT_COUNT) {
            return Optional.empty();
        }

        OptionalInt major = MinecraftVersion.parseSegment(segments[0]);
        OptionalInt minor = MinecraftVersion.parseSegment(segments[1]);

        if (major.isEmpty() || minor.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new MinecraftVersion(major.getAsInt(), minor.getAsInt()));
    }

    /**
     * Renders the drop the way Mojang writes it, so it can go straight into a message.
     *
     * @return the drop identity, such as {@code 26.2}
     */
    @Override
    public String toString() {
        return this.major + "." + this.minor;
    }

    private static OptionalInt parseSegment(String segment) {
        if (!MinecraftVersion.NUMERIC_SEGMENT.matcher(segment).matches()) {
            return OptionalInt.empty();
        }

        try {
            return OptionalInt.of(Integer.parseInt(segment));
        } catch (NumberFormatException exception) {
            // Reachable only for a digit run too long to fit an int.
            return OptionalInt.empty();
        }
    }
}
