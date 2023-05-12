package de.lazybytez.gamingbytezenhancements.feature.temporarycart.event;

import de.lazybytez.gamingbytezenhancements.feature.temporarycart.TemporaryCartManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;

/**
 * Listener that handles removal of the temporary cart when a player leaves it.
 */
public class MinecartLeaveListener implements Listener {
    private final TemporaryCartManager temporaryCartManager;

    public MinecartLeaveListener(TemporaryCartManager temporaryCartManager) {
        this.temporaryCartManager = temporaryCartManager;
    }

    @EventHandler
    public void onMineCartLeave(VehicleExitEvent event) {
        Vehicle vehicle = event.getVehicle();
        if (!(vehicle instanceof Minecart)) {
            return;
        }

        Entity exitedEntity = event.getExited();
        if (!(exitedEntity instanceof Player)) {
            return;
        }

        this.temporaryCartManager.removeTemporaryCart(
                (Player) exitedEntity,
                (Minecart) vehicle
        );
    }
}
