package de.lazybytez.gamingbytezenhancements.feature.antimobgriefing;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.AbstractFeature;
import de.lazybytez.gamingbytezenhancements.feature.antimobgriefing.event.AntiMobGriefingListener;

public class AntiMobGriefingFeature extends AbstractFeature {
    public AntiMobGriefingFeature(EnhancementsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        this.registerEvents();
    }

    public void registerEvents() {
        this.registerEvent(new AntiMobGriefingListener());
    }

    @Override
    public String getName() {
        return "AntiMobGriefing";
    }
}
