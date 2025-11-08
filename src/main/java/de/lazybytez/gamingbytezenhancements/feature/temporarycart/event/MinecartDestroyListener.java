package de.lazybytez.gamingbytezenhancements.feature.temporarycart.event;

import de.lazybytez.gamingbytezenhancements.feature.temporarycart.TemporaryCartManager;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

/**
 * Listener that handles removing a temporary cart it is destroyed.
 */
public class MinecartDestroyListener implements Listener {
    private final TemporaryCartManager temporaryCartManager;

    public MinecartDestroyListener(TemporaryCartManager temporaryCartManager) {
        this.temporaryCartManager = temporaryCartManager;
    }

    @EventHandler
    public void onMineCartDestroy(VehicleDestroyEvent event) {
        Vehicle vehicle = event.getVehicle();

        if (!(vehicle instanceof Minecart minecart)) {
            return;
        }

        if (!this.temporaryCartManager.isTemporaryCart(minecart)) {
            return;
        }

        event.setCancelled(true);
        this.temporaryCartManager.removeTemporaryCart(null, minecart, false);
    }
}
