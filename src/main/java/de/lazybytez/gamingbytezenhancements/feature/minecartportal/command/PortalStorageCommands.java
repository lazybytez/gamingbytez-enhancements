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
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.PortalConfiguration;
import de.lazybytez.gamingbytezenhancements.lib.command.CommandResults;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import java.util.Objects;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * The subcommand that reads the portal storage back into memory.
 */
final class PortalStorageCommands {
    private final Plugin plugin;
    private final PortalConfiguration portalConfig;
    private final Messenger messenger;

    PortalStorageCommands(Plugin plugin, PortalConfiguration portalConfig, Messenger messenger) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null");
        this.portalConfig = Objects.requireNonNull(portalConfig, "portalConfig must not be null");
        this.messenger = Objects.requireNonNull(messenger, "messenger must not be null");
    }

    /**
     * Build the subcommand reading the portal storage back into memory.
     *
     * @return the complete reload node
     */
    LiteralArgumentBuilder<CommandSourceStack> reload() {
        return Commands.literal("reload").executes(this::reloadPortals);
    }

    private int reloadPortals(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();

        this.messenger.info(sender, MinecartPortalMessages.reloadStarted());
        this.portalConfig.loadAsync(loaded -> this.reportOnServerThread(sender, Boolean.TRUE.equals(loaded)));

        return CommandResults.SUCCESS;
    }

    private void reportOnServerThread(CommandSender sender, boolean loaded) {
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.report(sender, loaded));
    }

    private void report(CommandSender sender, boolean loaded) {
        if (loaded) {
            this.messenger.success(sender, MinecartPortalMessages.reloadSucceeded());

            return;
        }

        this.messenger.error(sender, MinecartPortalMessages.reloadFailed());
    }
}
