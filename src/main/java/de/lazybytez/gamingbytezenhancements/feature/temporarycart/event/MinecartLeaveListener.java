/*
 * Gaming Bytez Enhancements - Gameplay enhancements used on our SMP servers.
 * Copyright (C) 2026 Lazy Bytez (Pascal Zarrad, Elias Knodel) and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
                (Minecart) vehicle,
                false
        );
    }
}
