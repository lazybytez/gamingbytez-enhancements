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

/**
 * The rule that decides whether a running server matches the version the plugin was built for.
 *
 * The plugin targets exactly one drop at a time, so the answer is a drop
 * comparison rather than an ordering: an older and a newer drop are both
 * unsupported, and every hotfix of the targeted drop is supported.
 */
public final class ServerVersionSupport {
    /**
     * Answers whether the running version belongs to the supported drop.
     *
     * A version that cannot be read counts as unsupported, so an unrecognised
     * string produces a warning instead of silent confidence.
     *
     * @param runningVersion the version reported by the running server, may be null
     * @param supportedVersion the version the plugin was built against, may be null
     * @return true only if both versions read and name the same drop
     */
    public static boolean isSupported(String runningVersion, String supportedVersion) {
        Optional<MinecraftVersion> running = MinecraftVersion.parse(runningVersion);
        Optional<MinecraftVersion> supported = MinecraftVersion.parse(supportedVersion);

        if (running.isEmpty() || supported.isEmpty()) {
            return false;
        }

        return running.get().equals(supported.get());
    }

    private ServerVersionSupport() {
    }
}
