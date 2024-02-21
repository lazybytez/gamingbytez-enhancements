package de.lazybytez.gamingbytezenhancements.feature.antimobgriefing;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.AbstractFeature;
import de.lazybytez.gamingbytezenhancements.feature.antimobgriefing.event.*;

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
        this.registerEvent(new AntiShulkerProjectileListener());
        this.registerEvent(new AntiSkelettProjectileListener());
    }

    @Override
    public String getName() {
        return "AntiMobGriefing";
    }
}
