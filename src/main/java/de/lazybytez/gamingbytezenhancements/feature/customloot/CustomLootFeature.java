package de.lazybytez.gamingbytezenhancements.feature.customloot;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.AbstractFeature;
import de.lazybytez.gamingbytezenhancements.feature.customloot.listener.EndermanCustomLootListener;
import de.lazybytez.gamingbytezenhancements.feature.customloot.listener.HuskCustomLootListener;

/**
 * Feature that adds listeners that provide custom loot to entities.
 */
public class CustomLootFeature extends AbstractFeature {
    public CustomLootFeature(EnhancementsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        this.registerEvents();
    }

    private void registerEvents() {
        this.registerEvent(new HuskCustomLootListener());
        this.registerEvent(new EndermanCustomLootListener());
    }

    @Override
    public String getName() {
        return "Custom Loot";
    }
}
