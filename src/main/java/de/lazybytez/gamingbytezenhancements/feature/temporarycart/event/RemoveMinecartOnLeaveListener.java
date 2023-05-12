package de.lazybytez.gamingbytezenhancements.feature.temporarycart.event;

import de.lazybytez.gamingbytezenhancements.feature.temporarycart.TemporaryCartManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Event that handles removing a temporary cart when a player leaves.
 */
public class RemoveMinecartOnLeaveListener implements Listener {
    private final TemporaryCartManager temporaryCartManager;

    public RemoveMinecartOnLeaveListener(TemporaryCartManager temporaryCartManager) {
        this.temporaryCartManager = temporaryCartManager;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player p = event.getPlayer();

        if (p.isInsideVehicle()) {
            Entity vehicle = p.getVehicle();
            if (!(vehicle instanceof Minecart)) {
                return;
            }

            this.temporaryCartManager.removeTemporaryCart(p, (Minecart) vehicle, true);
        }
    }
}
