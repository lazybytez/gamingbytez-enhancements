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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MinecraftVersionTest {
    @Test
    void parsesADropWithoutHotfixSegment() {
        assertEquals(Optional.of(new MinecraftVersion(26, 2)), MinecraftVersion.parse("26.2"));
    }

    @Test
    void ignoresTheHotfixSegment() {
        assertEquals(MinecraftVersion.parse("26.2.4"), MinecraftVersion.parse("26.2.1"));
    }

    @Test
    void parsesTheLegacyScheme() {
        assertEquals(Optional.of(new MinecraftVersion(1, 21)), MinecraftVersion.parse("1.21.11"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   ", "26", "26.", ".2", "26.x", "x.2", "26.2-rc-2", "26.-2"})
    void refusesAnythingWithoutTwoNumericLeadingSegments(String rawVersion) {
        assertEquals(Optional.empty(), MinecraftVersion.parse(rawVersion));
    }

    @Test
    void neverReturnsNullFromTheFactory() {
        assertNotNull(MinecraftVersion.parse(null));
        assertNotNull(MinecraftVersion.parse("26.2"));
    }

    @Test
    void treatsEqualDropsAsEqualValues() {
        MinecraftVersion release = MinecraftVersion.parse("26.2").orElseThrow();
        MinecraftVersion hotfix = MinecraftVersion.parse("26.2.4").orElseThrow();

        assertEquals(release, hotfix);
        assertEquals(release.hashCode(), hotfix.hashCode());
    }

    @Test
    void treatsDifferentDropsAsDifferentValues() {
        assertNotEquals(MinecraftVersion.parse("26.2"), MinecraftVersion.parse("26.3"));
        assertNotEquals(MinecraftVersion.parse("26.2"), MinecraftVersion.parse("27.2"));
    }

    @Test
    void rendersTheDropIdentity() {
        assertEquals("26.2", MinecraftVersion.parse("26.2.4").orElseThrow().toString());
    }
}
