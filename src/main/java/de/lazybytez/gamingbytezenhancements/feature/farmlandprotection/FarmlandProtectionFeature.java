package de.lazybytez.gamingbytezenhancements.feature.farmlandprotection;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.AbstractFeature;
import de.lazybytez.gamingbytezenhancements.feature.farmlandprotection.event.FarmlandDestroyListener;

public class FarmlandProtectionFeature extends AbstractFeature {
    public FarmlandProtectionFeature(EnhancementsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        this.registerEvents();
    }

    private void registerEvents() {
        this.registerEvent(new FarmlandDestroyListener());
    }

    @Override
    public String getName() {
        return "Farmland Protection";
    }
}
