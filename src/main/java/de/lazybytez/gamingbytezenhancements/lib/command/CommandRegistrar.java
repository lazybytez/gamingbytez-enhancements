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
package de.lazybytez.gamingbytezenhancements.lib.command;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.util.List;
import java.util.Objects;
import org.bukkit.plugin.Plugin;

/**
 * The single place the plugin talks to the command lifecycle.
 *
 * A feature hands over its {@link PluginCommand} instances and writes no
 * lifecycle boilerplate, which is why no other class may reference
 * {@link LifecycleEvents} directly.
 */
public final class CommandRegistrar {
    private final Plugin plugin;

    /**
     * Bind a registrar to the plugin whose lifecycle manager it uses.
     *
     * @param plugin the plugin owning the commands
     */
    public CommandRegistrar(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null");
    }

    /**
     * Register the given commands once the server asks for command registration.
     *
     * The tree of each command is built inside the lifecycle handler, so a
     * server reload rebuilds it from the current feature state.
     *
     * @param commands the commands to register
     */
    public void register(PluginCommand... commands) {
        Objects.requireNonNull(commands, "commands must not be null");

        List<PluginCommand> pending = List.of(commands);

        this.plugin.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS,
                event -> registerAll(event.registrar(), pending)
        );
    }

    /**
     * Hand every command to the registrar the server supplied.
     *
     * This is separate from {@link #register} so it can be exercised without
     * touching {@link LifecycleEvents}, whose constants resolve a server-provided
     * service and are therefore unreachable outside a running server.
     *
     * @param registrar the registrar for the current registration pass
     * @param commands  the commands to hand over
     */
    static void registerAll(Commands registrar, List<PluginCommand> commands) {
        for (PluginCommand command : commands) {
            registrar.register(command.createNode().build(), command.description(), command.aliases());
        }
    }
}
