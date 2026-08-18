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
package de.lazybytez.gamingbytezenhancements.feature.minecartportal.command;

import de.lazybytez.gamingbytezenhancements.feature.minecartportal.messages.MinecartPortalMessages;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.PortalConfiguration;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import java.util.Objects;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * Persists a portal change right after the command that made it.
 *
 * A successful write is silent, because the command already confirmed what it
 * changed. A failed write is worth an interruption, since the change then lives
 * in memory only and is lost on shutdown.
 */
final class PortalAutoSave {
    private final Plugin plugin;
    private final PortalConfiguration portalConfig;
    private final Messenger messenger;

    /**
     * Bind the auto save to the configuration it writes and the messenger it reports through.
     *
     * @param plugin       the plugin owning the scheduler used to reach the server thread
     * @param portalConfig the configuration to write
     * @param messenger    the messenger delivering a failure warning
     */
    PortalAutoSave(Plugin plugin, PortalConfiguration portalConfig, Messenger messenger) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null");
        this.portalConfig = Objects.requireNonNull(portalConfig, "portalConfig must not be null");
        this.messenger = Objects.requireNonNull(messenger, "messenger must not be null");
    }

    /**
     * Write the current portals and warn the sender if the write failed.
     *
     * @param sender the sender whose command caused the change
     */
    void requestSave(CommandSender sender) {
        this.portalConfig.saveAsync(saved -> {
            if (Boolean.TRUE.equals(saved)) {
                return;
            }

            this.warnOnServerThread(sender);
        });
    }

    private void warnOnServerThread(CommandSender sender) {
        this.plugin.getServer().getScheduler().runTask(
                this.plugin,
                () -> this.messenger.warning(sender, MinecartPortalMessages.saveFailed())
        );
    }
}
