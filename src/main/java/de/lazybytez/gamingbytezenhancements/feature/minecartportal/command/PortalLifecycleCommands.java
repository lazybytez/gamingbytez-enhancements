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
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.model.MinecartPortal;
import de.lazybytez.gamingbytezenhancements.lib.command.CommandResults;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import java.util.Objects;
import org.bukkit.command.CommandSender;

/**
 * The subcommands that bring a portal into existence and take it out again.
 */
final class PortalLifecycleCommands {
    private final PortalConfiguration portalConfig;
    private final Messenger messenger;
    private final PortalArguments arguments;
    private final PortalAutoSave autoSave;

    PortalLifecycleCommands(
            PortalConfiguration portalConfig,
            Messenger messenger,
            PortalArguments arguments,
            PortalAutoSave autoSave
    ) {
        this.portalConfig = Objects.requireNonNull(portalConfig, "portalConfig must not be null");
        this.messenger = Objects.requireNonNull(messenger, "messenger must not be null");
        this.arguments = Objects.requireNonNull(arguments, "arguments must not be null");
        this.autoSave = Objects.requireNonNull(autoSave, "autoSave must not be null");
    }

    /**
     * Build the subcommand registering a portal that has no ends yet.
     *
     * @return the complete add node
     */
    LiteralArgumentBuilder<CommandSourceStack> add() {
        return Commands.literal("add").then(this.arguments.newName().executes(this::addPortal));
    }

    /**
     * Build the subcommand removing a registered portal.
     *
     * @return the complete delete node
     */
    LiteralArgumentBuilder<CommandSourceStack> delete() {
        return Commands.literal("delete").then(this.arguments.existingName().executes(this::deletePortal));
    }

    private int addPortal(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        String name = this.arguments.name(context);

        if (MinecartPortal.isNameTooLong(name)) {
            this.messenger.error(sender, MinecartPortalMessages.nameTooLong());

            return CommandResults.FAILURE;
        }

        if (!MinecartPortal.hasValidNameCharacters(name)) {
            this.messenger.error(sender, MinecartPortalMessages.nameNotAlphanumeric());

            return CommandResults.FAILURE;
        }

        if (!this.portalConfig.addPortal(new MinecartPortal(name, null, null))) {
            this.messenger.error(sender, MinecartPortalMessages.alreadyExists(name));

            return CommandResults.FAILURE;
        }

        this.autoSave.requestSave(sender);
        this.messenger.success(sender, MinecartPortalMessages.added(name));

        return CommandResults.SUCCESS;
    }

    private int deletePortal(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        String name = this.arguments.name(context);
        MinecartPortal portal = this.portalConfig.getPortalByName(name);

        if (portal == null) {
            this.messenger.error(sender, MinecartPortalMessages.notFound(name));

            return CommandResults.FAILURE;
        }

        if (!this.portalConfig.deletePortal(portal)) {
            this.messenger.error(sender, MinecartPortalMessages.deleteFailed(name));

            return CommandResults.FAILURE;
        }

        this.autoSave.requestSave(sender);
        this.messenger.success(sender, MinecartPortalMessages.deleted(name));

        return CommandResults.SUCCESS;
    }
}
