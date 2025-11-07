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
