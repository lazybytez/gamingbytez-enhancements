package de.lazybytez.gamingbytezenhancements.feature.temporarycart.event;

import de.lazybytez.gamingbytezenhancements.feature.temporarycart.TemporaryCartManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Event that handles removing the cool down when a player disconnects.
 * <p>
 * This is needed because the cool down is stored in memory and would otherwise
 * not be removed when the player disconnects.
 * </p>
 */
public class RemoveCoolDownListener implements Listener {
    private final TemporaryCartManager temporaryCartManager;

    public RemoveCoolDownListener(TemporaryCartManager temporaryCartManager) {
        this.temporaryCartManager = temporaryCartManager;
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player p = event.getPlayer();

        this.temporaryCartManager.removeCoolDown(p);
    }
}
