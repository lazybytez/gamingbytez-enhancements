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
import de.lazybytez.gamingbytezenhancements.lib.message.LocationFormat;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePalette;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;

/**
 * The subcommands that read the portal registry without changing it.
 */
final class PortalQueryCommands {
    private static final String INSPECT = "inspect";

    private final PortalConfiguration portalConfig;
    private final Messenger messenger;
    private final PortalArguments arguments;

    PortalQueryCommands(PortalConfiguration portalConfig, Messenger messenger, PortalArguments arguments) {
        this.portalConfig = Objects.requireNonNull(portalConfig, "portalConfig must not be null");
        this.messenger = Objects.requireNonNull(messenger, "messenger must not be null");
        this.arguments = Objects.requireNonNull(arguments, "arguments must not be null");
    }

    /**
     * Build the subcommand listing every registered portal.
     *
     * @return the complete list node
     */
    LiteralArgumentBuilder<CommandSourceStack> list() {
        return Commands.literal("list").executes(this::listPortals);
    }

    /**
     * Build the subcommand showing both ends of a single portal.
     *
     * @return the complete inspect node
     */
    LiteralArgumentBuilder<CommandSourceStack> inspect() {
        return Commands.literal(PortalQueryCommands.INSPECT)
                .then(this.arguments.existingName().executes(this::inspectPortal));
    }

    private int listPortals(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        List<MinecartPortal> portals = this.portalConfig.getPortals();

        if (portals.isEmpty()) {
            this.messenger.info(sender, MinecartPortalMessages.noneRegistered());

            return CommandResults.SUCCESS;
        }

        this.messenger.heading(sender, "Registered Minecart Portals (" + portals.size() + ")");

        String label = context.getNodes().getFirst().getNode().getName();

        for (MinecartPortal portal : portals) {
            this.messenger.bullet(sender, this.listEntry(portal, label));
        }

        return CommandResults.SUCCESS;
    }

    private int inspectPortal(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        String name = this.arguments.name(context);
        MinecartPortal portal = this.portalConfig.getPortalByName(name);

        if (portal == null) {
            this.messenger.error(sender, MinecartPortalMessages.notFound(name));

            return CommandResults.FAILURE;
        }

        this.messenger.heading(sender, Component.text("Minecart Portal ")
                .append(Component.text(name, MessagePalette.SUBJECT)));
        this.messenger.field(sender, "Entry", LocationFormat.format(portal.getPortal()));
        this.messenger.field(sender, "Exit", LocationFormat.format(portal.getDestination()));

        return CommandResults.SUCCESS;
    }

    /**
     * Render one listing entry, offering the inspect command and the entry location.
     *
     * The label comes from the node the sender invoked, so clicking an entry of a
     * listing produced through an alias suggests that alias rather than the canonical
     * name.
     *
     * @param portal the portal the entry stands for
     * @param label  the label the sender invoked the command with
     * @return the entry text carrying its click and hover behaviour
     */
    private Component listEntry(MinecartPortal portal, String label) {
        return Component.text(portal.getName())
                .clickEvent(ClickEvent.suggestCommand(PortalQueryCommands.inspectCommand(label, portal.getName())))
                .hoverEvent(HoverEvent.showText(LocationFormat.format(portal.getPortal())));
    }

    private static String inspectCommand(String label, String name) {
        return "/" + label + " " + PortalQueryCommands.INSPECT + " " + name;
    }
}
