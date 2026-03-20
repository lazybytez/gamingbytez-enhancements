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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.particles;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.plugin.Plugin;

/**
 * This interface represents an AltarParticleActionWrapper.
 * An AltarParticleActionWrapper is used to wrap the action that should be performed when a particle effect is completed on an altar.
 * The action is defined by the onRecipeComplete method, which is called when the particle effect is completed.
 */
public interface AltarParticleActionWrapper {
    /**
     * This method is called when the particle effect is completed on an altar.
     *
     * @param plugin The plugin instance.
     * @param altar  The altar on which the particle effect was executed.
     * @param event  The event that triggered the particle effect.
     */
    void onRecipeComplete(Plugin plugin, AltarInterface altar, PlayerItemFrameChangeEvent event);
}