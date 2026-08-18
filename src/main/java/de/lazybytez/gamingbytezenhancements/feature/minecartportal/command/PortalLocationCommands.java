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
import de.lazybytez.gamingbytezenhancements.lib.command.CommandSources;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import java.util.Objects;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The subcommands that move the two ends of a portal onto the rail below the sender.
 */
final class PortalLocationCommands {
    private final PortalConfiguration portalConfig;
    private final Messenger messenger;
    private final PortalArguments arguments;
    private final PortalAutoSave autoSave;

    PortalLocationCommands(
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
     * Build the subcommand moving the entry point of a portal.
     *
     * @return the complete entry node
     */
    LiteralArgumentBuilder<CommandSourceStack> entry() {
        return Commands.literal("entry").then(this.arguments.existingName().executes(this::setEntry));
    }

    /**
     * Build the subcommand moving the exit point of a portal.
     *
     * @return the complete exit node
     */
    LiteralArgumentBuilder<CommandSourceStack> exit() {
        return Commands.literal("exit").then(this.arguments.existingName().executes(this::setExit));
    }

    private int setEntry(CommandContext<CommandSourceStack> context) {
        RailTarget target = this.railTarget(
                context,
                Material.DETECTOR_RAIL,
                MinecartPortalMessages.entryNeedsDetectorRail()
        );

        if (target == null) {
            return CommandResults.FAILURE;
        }

        MinecartPortal moved = new MinecartPortal(
                target.portal().getName(),
                target.location(),
                target.portal().getDestination());

        return this.store(
                context.getSource().getSender(),
                moved,
                MinecartPortalMessages.entryUpdated(target.portal().getName())
        );
    }

    private int setExit(CommandContext<CommandSourceStack> context) {
        RailTarget target = this.railTarget(context, Material.RAIL, MinecartPortalMessages.exitNeedsRail());

        if (target == null) {
            return CommandResults.FAILURE;
        }

        MinecartPortal moved = new MinecartPortal(
                target.portal().getName(),
                target.portal().getPortal(),
                target.location());

        return this.store(
                context.getSource().getSender(),
                moved,
                MinecartPortalMessages.exitUpdated(target.portal().getName())
        );
    }

    /**
     * Resolve the portal and the rail position a sender may move an end to.
     *
     * The position comes from the acting player rather than from the command source, so
     * the subject the player check qualifies is the subject the stored coordinates come
     * from. Reading the source position instead would let an execute-as invocation pass
     * the player check and then store the position of whoever started the command.
     *
     * @param context   the context of the executed command
     * @param rail      the material the player has to stand on
     * @param wrongRail the wording rejecting any other material
     * @return the addressed portal with the position to store, or null when already rejected
     */
    private RailTarget railTarget(
            CommandContext<CommandSourceStack> context,
            Material rail,
            Component wrongRail
    ) {
        CommandSourceStack source = context.getSource();
        CommandSender sender = source.getSender();
        Optional<Player> executor = CommandSources.playerExecutor(source);

        if (executor.isEmpty()) {
            this.messenger.error(sender, MinecartPortalMessages.playerOnly());

            return null;
        }

        String name = this.arguments.name(context);
        MinecartPortal portal = this.portalConfig.getPortalByName(name);

        if (portal == null) {
            this.messenger.error(sender, MinecartPortalMessages.notFound(name));

            return null;
        }

        Location location = executor.get().getLocation();

        if (location.getBlock().getType() != rail) {
            this.messenger.error(sender, wrongRail);

            return null;
        }

        return new RailTarget(portal, location);
    }

    private record RailTarget(MinecartPortal portal, Location location) {
    }

    private int store(CommandSender sender, MinecartPortal portal, Component confirmation) {
        if (!this.portalConfig.updatePortal(portal)) {
            this.messenger.error(sender, MinecartPortalMessages.updateFailed(portal.getName()));

            return CommandResults.FAILURE;
        }

        this.autoSave.requestSave(sender);
        this.messenger.success(sender, confirmation);

        return CommandResults.SUCCESS;
    }
}
