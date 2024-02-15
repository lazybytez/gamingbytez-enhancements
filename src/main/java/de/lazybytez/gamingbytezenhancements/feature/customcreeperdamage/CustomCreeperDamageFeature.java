package de.lazybytez.gamingbytezenhancements.feature.customcreeperdamage;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.AbstractFeature;
import de.lazybytez.gamingbytezenhancements.feature.customcreeperdamage.event.CreeperDamageListener;
import de.lazybytez.gamingbytezenhancements.feature.customcreeperdamage.service.ArmorBasedCreeperDamageCalculator;

public class CustomCreeperDamageFeature extends AbstractFeature {
    public CustomCreeperDamageFeature(EnhancementsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        this.registerEvents();
    }

    public void registerEvents() {
        this.registerEvent(new CreeperDamageListener(new ArmorBasedCreeperDamageCalculator()));
    }

    @Override
    public String getName() {
        return "CustomCreeperDamage";
    }
}
