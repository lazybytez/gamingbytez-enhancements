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
