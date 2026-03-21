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
package de.lazybytez.gamingbytezenhancements.feature;

/**
 * Contract for all plugin features.
 * <p>
 * Implementations follow the lifecycle {@code onLoad → onEnable → onDisable} driven by
 * {@link de.lazybytez.gamingbytezenhancements.EnhancementsPlugin}. Features can be toggled
 * on or off via the {@code features} section in {@code config.yml}; disabled features are
 * skipped in every lifecycle phase.
 * </p>
 * <p>
 * Prefer extending {@link AbstractFeature} over implementing this interface directly,
 * as it provides default no-op implementations and the config-backed {@link #isEnabled()} logic.
 * </p>
 */
public interface Feature {
    /**
     * Called when the plugin is loaded.
     * <p>
     * Runs before the server has fully started. Only invoked when {@link #isEnabled()} returns {@code true}.
     * </p>
     */
    void onLoad();

    /**
     * Called when the plugin is enabled.
     * <p>
     * Primary setup hook: register event listeners, commands, and schedulers here.
     * Only invoked when {@link #isEnabled()} returns {@code true}.
     * </p>
     */
    void onEnable();

    /**
     * Called when the plugin is disabled.
     * <p>
     * Clean up resources (tasks, handles, open files) here.
     * Only invoked when {@link #isEnabled()} returns {@code true}.
     * </p>
     */
    void onDisable();

    /**
     * Returns the human-readable name of this feature.
     * <p>
     * Also used to derive the config toggle key via {@link AbstractFeature#getFeatureConfigKey()}:
     * spaces are stripped and the result is looked up under {@code features.<key>} in {@code config.yml}
     * (e.g. "Farmland Protection" → {@code features.FarmlandProtection}).
     * </p>
     */
    String getName();

    /**
     * Returns whether this feature is enabled.
     * <p>
     * The config key used is derived from {@link #getName()} with spaces removed,
     * under the {@code features} section (e.g. {@code features.MythicAltar}).
     * Defaults to {@code true} when the key is absent.
     * </p>
     */
    boolean isEnabled();
}
