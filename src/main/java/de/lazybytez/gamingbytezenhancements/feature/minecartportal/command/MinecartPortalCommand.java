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

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.PortalConfiguration;
import de.lazybytez.gamingbytezenhancements.lib.command.CommandHelp;
import de.lazybytez.gamingbytezenhancements.lib.command.CommandResults;
import de.lazybytez.gamingbytezenhancements.lib.command.PluginCommand;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import java.util.List;
import java.util.Objects;
import org.bukkit.plugin.Plugin;

/**
 * The Minecart Portal administration command.
 */
public final class MinecartPortalCommand implements PluginCommand {
    private static final String ADMIN_PERMISSION = "gamingbytez.minecartportals.admin";
    private static final String LABEL = "minecartportals";

    private final Messenger messenger;
    private final PortalLifecycleCommands lifecycleCommands;
    private final PortalLocationCommands locationCommands;
    private final PortalQueryCommands queryCommands;
    private final PortalStorageCommands storageCommands;

    /**
     * Assemble the command from the state it administers.
     *
     * @param plugin       the plugin owning the scheduler the subcommands use
     * @param portalConfig the configuration holding the registered portals
     * @param messenger    the messenger carrying the Minecart Portals prefix
     */
    public MinecartPortalCommand(Plugin plugin, PortalConfiguration portalConfig, Messenger messenger) {
        Objects.requireNonNull(plugin, "plugin must not be null");
        Objects.requireNonNull(portalConfig, "portalConfig must not be null");
        Objects.requireNonNull(messenger, "messenger must not be null");

        this.messenger = messenger;

        PortalArguments portalArguments = new PortalArguments(portalConfig);
        PortalAutoSave portalAutoSave = new PortalAutoSave(plugin, portalConfig, this.messenger);

        this.lifecycleCommands = new PortalLifecycleCommands(
                portalConfig,
                this.messenger,
                portalArguments,
                portalAutoSave
        );
        this.locationCommands = new PortalLocationCommands(
                portalConfig,
                this.messenger,
                portalArguments,
                portalAutoSave
        );
        this.queryCommands = new PortalQueryCommands(portalConfig, this.messenger, portalArguments);
        this.storageCommands = new PortalStorageCommands(plugin, portalConfig, this.messenger);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> createNode() {
        return Commands.literal(MinecartPortalCommand.LABEL)
                .requires(MinecartPortalCommand::canUse)
                .executes(this::sendHelp)
                .then(this.lifecycleCommands.add())
                .then(this.locationCommands.entry())
                .then(this.locationCommands.exit())
                .then(this.lifecycleCommands.delete())
                .then(this.queryCommands.list())
                .then(this.queryCommands.inspect())
                .then(this.storageCommands.reload());
    }

    @Override
    public String description() {
        return "Manage the Minecart Portals of the GamingBytez Enhancements plugin";
    }

    @Override
    public List<String> aliases() {
        return List.of("gbmcp");
    }

    private static boolean canUse(CommandSourceStack source) {
        return source.getSender().hasPermission(MinecartPortalCommand.ADMIN_PERMISSION);
    }

    private int sendHelp(CommandContext<CommandSourceStack> context) {
        CommandHelp.send(context.getSource(), this.messenger, context.getNodes().getFirst().getNode());

        return CommandResults.SUCCESS;
    }
}
