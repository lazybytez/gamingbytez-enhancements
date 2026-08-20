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

/**
 * Composes the operator-facing warning for a version mismatch.
 * <p>
 * Kept apart from {@link ServerVersionSupport} because that class only answers
 * whether a version is supported, and apart from plugin startup because
 * message wording holds no server state and does not belong in the lifecycle
 * class that decides when to log it.
 */
public final class ServerVersionWarning {
    private static final String BORDER = "==================================================================";
    private static final String UNKNOWN_VERSION_LABEL = "unknown";

    /**
     * Builds the warning lines for a supported/running version pair.
     * <p>
     * The result is empty when the pair is supported, so a caller can log
     * every line unconditionally without a separate support check. An
     * unreadable supported version is rendered as "unknown" rather than
     * skipped, because the running version and the fact that something is
     * wrong are still worth telling the operator.
     *
     * @param supportedVersion the version the plugin was built against, may be null
     * @param runningVersion the version reported by the running server, may be null
     * @return the warning as ordered lines, or an empty list when the versions are supported
     */
    public static List<String> compose(String supportedVersion, String runningVersion) {
        if (ServerVersionSupport.isSupported(runningVersion, supportedVersion)) {
            return List.of();
        }

        String supportedLabel = ServerVersionWarning.label(supportedVersion);
        String runningLabel = ServerVersionWarning.label(runningVersion);

        return List.of(
                ServerVersionWarning.BORDER,
                "  UNSUPPORTED MINECRAFT VERSION",
                "  This plugin was built for Minecraft " + supportedLabel + ", but the",
                "  server is running " + runningLabel + ".",
                "  The plugin will keep running anyway and may misbehave.",
                ServerVersionWarning.BORDER
        );
    }

    private static String label(String version) {
        if (version == null || version.isBlank()) {
            return ServerVersionWarning.UNKNOWN_VERSION_LABEL;
        }

        return version;
    }

    private ServerVersionWarning() {
    }
}
