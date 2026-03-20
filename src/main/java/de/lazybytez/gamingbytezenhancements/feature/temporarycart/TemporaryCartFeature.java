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
        this.registerEvent(new MinecartTeleportListener(this.getTemporaryCartManager(), this.getPlugin()));
    }

    @Override
    public String getName() {
        return "TemporaryCart";
    }

    public TemporaryCartManager getTemporaryCartManager() {
        return temporaryCartManager;
    }
}
