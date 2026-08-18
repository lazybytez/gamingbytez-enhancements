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

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.PortalConfiguration;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.model.MinecartPortal;
import de.lazybytez.gamingbytezenhancements.lib.command.CommandSuggestions;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * The portal name argument, owned in one place for the whole feature.
 *
 * Every factory hands out a fresh builder, because a Brigadier builder is
 * consumed by the node it is attached to and the command tree needs several
 * independent name arguments.
 */
final class PortalArguments {
    private static final String NAME = "name";

    private final PortalConfiguration portalConfig;

    /**
     * Bind the argument factories to the portals they describe.
     *
     * @param portalConfig the configuration holding the registered portals
     */
    PortalArguments(PortalConfiguration portalConfig) {
        this.portalConfig = Objects.requireNonNull(portalConfig, "portalConfig must not be null");
    }

    /**
     * Build a name argument for a portal that does not exist yet.
     *
     * It offers no suggestions, so creating a portal never proposes a name that
     * is already taken.
     *
     * @return a fresh name argument without suggestions
     */
    RequiredArgumentBuilder<CommandSourceStack, String> newName() {
        return Commands.argument(PortalArguments.NAME, StringArgumentType.word());
    }

    /**
     * Build a name argument for a portal that is expected to exist.
     *
     * @return a fresh name argument suggesting the registered portal names
     */
    RequiredArgumentBuilder<CommandSourceStack, String> existingName() {
        return Commands.argument(PortalArguments.NAME, StringArgumentType.word())
                .suggests(CommandSuggestions.fromSupplier(this::portalNames));
    }

    /**
     * Read the portal name a sender typed.
     *
     * @param context the context of the executed command
     * @return the name argument value
     */
    String name(CommandContext<CommandSourceStack> context) {
        return StringArgumentType.getString(context, PortalArguments.NAME);
    }

    private Collection<String> portalNames() {
        List<MinecartPortal> portals = this.portalConfig.getPortals();
        List<String> names = new ArrayList<>(portals.size());

        for (MinecartPortal portal : portals) {
            names.add(portal.getName());
        }

        return names;
    }
}
