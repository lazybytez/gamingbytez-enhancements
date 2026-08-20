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
package de.lazybytez.gamingbytezenhancements.lib.gameplay.item;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Binds the {@code DataComponentTypes} registry stub before any test class runs.
 * <p>
 * {@code DataComponentTypes} resolves every constant through {@code org.bukkit.Registry} in its
 * static initialiser, so the class initialises exactly once per JVM and is permanently poisoned for
 * the rest of the run if that first touch happens without the stub in place. Registered as a global
 * extension through {@code META-INF/services} together with
 * {@code junit.jupiter.extensions.autodetection.enabled=true}, this extension is added to the root of
 * the extension registry, ahead of every class level extension, so {@link #beforeAll(ExtensionContext)}
 * runs before the first class-level {@code @BeforeAll} of the very first test class the run touches,
 * whichever class that is.
 */
public final class ItemDataComponentBindingExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        ItemDataComponentStubs.bindComponentTypes();
    }
}
