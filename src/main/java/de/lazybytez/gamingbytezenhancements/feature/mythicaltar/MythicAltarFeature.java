package de.lazybytez.gamingbytezenhancements.feature.mythicaltar;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.AbstractFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.AltarCraftingListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.schema.validator.SimpleAltarSchemaValidator;

/**
 * Feature that provides a new crafting altar to do some special stuff.
 */
public class MythicAltarFeature extends AbstractFeature {
    public MythicAltarFeature(EnhancementsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        this.registerEvents();
    }

    private void registerEvents() {
        this.registerEvent(new AltarCraftingListener(new SimpleAltarSchemaValidator()));
    }

    @Override
    public String getName() {
        return "MythicAltar";
    }
}
