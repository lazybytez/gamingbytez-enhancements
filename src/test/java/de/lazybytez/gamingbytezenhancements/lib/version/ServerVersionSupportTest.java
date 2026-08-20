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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerVersionSupportTest {
    private static final String SUPPORTED_VERSION = "26.2";

    @Test
    void supportsTheExactSupportedVersion() {
        assertTrue(ServerVersionSupport.isSupported("26.2", ServerVersionSupportTest.SUPPORTED_VERSION));
    }

    @ParameterizedTest
    @ValueSource(strings = {"26.2.1", "26.2.4"})
    void supportsEveryHotfixOfTheSupportedDrop(String runningVersion) {
        assertTrue(ServerVersionSupport.isSupported(runningVersion, ServerVersionSupportTest.SUPPORTED_VERSION));
    }

    @ParameterizedTest
    @ValueSource(strings = {"26.3", "27.1", "26.1.2", "1.21.11"})
    void rejectsEveryOtherDrop(String runningVersion) {
        assertFalse(ServerVersionSupport.isSupported(runningVersion, ServerVersionSupportTest.SUPPORTED_VERSION));
    }

    @Test
    void rejectsAReleaseCandidateOfTheSupportedDrop() {
        assertFalse(ServerVersionSupport.isSupported("26.2-rc-2", ServerVersionSupportTest.SUPPORTED_VERSION));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   ", "26", "nonsense"})
    void rejectsAnUnreadableRunningVersion(String runningVersion) {
        assertFalse(ServerVersionSupport.isSupported(runningVersion, ServerVersionSupportTest.SUPPORTED_VERSION));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   ", "26", "nonsense"})
    void rejectsAnUnreadableSupportedVersion(String supportedVersion) {
        assertFalse(ServerVersionSupport.isSupported("26.2", supportedVersion));
    }
}
