package de.lazybytez.gamingbytezenhancements.feature.temporarycart;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.AbstractFeature;
import de.lazybytez.gamingbytezenhancements.feature.temporarycart.event.*;

/**
 * Feature that handles spawning a temporary cart when a player right-clicks a rail.
 */
public class TemporaryCartFeature extends AbstractFeature {
    private final TemporaryCartManager temporaryCartManager;

    public TemporaryCartFeature(EnhancementsPlugin plugin) {
        super(plugin);

        temporaryCartManager = new TemporaryCartManager(plugin);
    }

    @Override
    public void onEnable() {
        this.registerEvents();
    }

    private void registerEvents() {
        this.registerEvent(new RailRightClickListener(this.getTemporaryCartManager()));
        this.registerEvent(new MinecartLeaveListener(this.getTemporaryCartManager()));
        this.registerEvent(new RemoveCoolDownListener(this.getTemporaryCartManager()));
        this.registerEvent(new RemoveMinecartOnLeaveListener(this.getTemporaryCartManager()));
        this.registerEvent(new MinecartDestroyListener(this.getTemporaryCartManager()));
    }

    @Override
    public String getName() {
        return "TemporaryCart";
    }

    public TemporaryCartManager getTemporaryCartManager() {
        return temporaryCartManager;
    }
}
