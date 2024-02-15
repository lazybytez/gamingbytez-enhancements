package de.lazybytez.gamingbytezenhancements.feature.antimobgriefing;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.AbstractFeature;
import de.lazybytez.gamingbytezenhancements.feature.antimobgriefing.event.AntiCreeperExplosionListener;
import de.lazybytez.gamingbytezenhancements.feature.antimobgriefing.event.AntiEndermanTakeBlockListener;
import de.lazybytez.gamingbytezenhancements.feature.antimobgriefing.event.AntiFireballExplosionListener;

public class AntiMobGriefingFeature extends AbstractFeature {
    public AntiMobGriefingFeature(EnhancementsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        this.registerEvents();
    }

    public void registerEvents() {
        this.registerEvent(new AntiCreeperExplosionListener());
        this.registerEvent(new AntiEndermanTakeBlockListener());
        this.registerEvent(new AntiFireballExplosionListener());
    }

    @Override
    public String getName() {
        return "AntiMobGriefing";
    }
}
