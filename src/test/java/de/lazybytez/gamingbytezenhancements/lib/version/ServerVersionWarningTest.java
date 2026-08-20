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

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerVersionWarningTest {
    private static final String SUPPORTED_VERSION = "26.2";

    @Test
    void composesNoLinesWhenTheRunningVersionIsSupported() {
        List<String> lines = ServerVersionWarning.compose(
                ServerVersionWarningTest.SUPPORTED_VERSION,
                "26.2.4"
        );

        assertEquals(List.of(), lines);
    }

    @Test
    void composesMultipleLinesWhenTheRunningVersionIsUnsupported() {
        List<String> lines = ServerVersionWarning.compose(
                ServerVersionWarningTest.SUPPORTED_VERSION,
                "26.3"
        );

        assertTrue(lines.size() > 1, "the warning must span multiple lines");
    }

    @Test
    void namesBothVersionsWhenUnsupported() {
        List<String> lines = ServerVersionWarning.compose(
                ServerVersionWarningTest.SUPPORTED_VERSION,
                "26.3"
        );
        String joined = String.join("\n", lines);

        assertTrue(joined.contains("26.2"), "warning must name the supported version");
        assertTrue(joined.contains("26.3"), "warning must name the running version");
    }

    @Test
    void statesThePluginKeepsRunningAndMayMisbehave() {
        List<String> lines = ServerVersionWarning.compose(
                ServerVersionWarningTest.SUPPORTED_VERSION,
                "26.3"
        );
        String joined = String.join("\n", lines).toLowerCase();

        assertTrue(joined.contains("running"), "warning must state the plugin keeps running");
        assertTrue(joined.contains("misbehave"), "warning must state the plugin may misbehave");
    }

    @Test
    void handlesANullSupportedVersionWithoutThrowing() {
        List<String> lines = ServerVersionWarning.compose(null, "26.3");

        assertTrue(lines.size() > 1, "an unreadable supported version is still an unsupported result");
        assertTrue(String.join("\n", lines).contains("26.3"), "the running version must still be named");
    }
}
