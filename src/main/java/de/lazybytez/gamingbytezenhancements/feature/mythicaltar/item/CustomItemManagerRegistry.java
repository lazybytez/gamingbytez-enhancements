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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item;

import java.util.HashMap;
import java.util.Map;

/**
 * This registry is used to manage all custom item managers.
 */
public final class CustomItemManagerRegistry {
    /**
     * Map holding all registered custom item managers.
     */
    private final Map<Class<? extends AbstractCustomItemManager>, AbstractCustomItemManager> customItemManagers;

    public CustomItemManagerRegistry() {
        this.customItemManagers = new HashMap<>();
    }

    /**
     * Register a custom item manager.
     * <p>
     * This method is synchronized to ensure new item managers cannot be added concurrently.
     */
    public synchronized void registerCustomItemManager(AbstractCustomItemManager customItemManager) {
        customItemManagers.put(customItemManager.getClass(), customItemManager);
    }

    /**
     * Get a custom item manager by its class.
     */
    public <T extends AbstractCustomItemManager> T getCustomItemManager(
            Class<T> customItemManagerClass
    ) {
        AbstractCustomItemManager customItemManager = customItemManagers.get(customItemManagerClass);

        if (customItemManagerClass.isInstance(customItemManager)) {
            return customItemManagerClass.cast(customItemManager);
        }

        throw new IllegalArgumentException("No custom item manager found for class " + customItemManagerClass.getName());
    }
}
