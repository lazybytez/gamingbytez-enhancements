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
import com.mojang.brigadier.CommandDispatcher;
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.PortalConfiguration;
import de.lazybytez.gamingbytezenhancements.lib.command.CommandResults;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePalette;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePrefix;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class PortalStorageCommandsTest {
    private static final MessagePrefix PREFIX = MessagePrefix.of("MinecartPortals", NamedTextColor.LIGHT_PURPLE);

    private final Plugin plugin = mock(Plugin.class);
    private final Server server = mock(Server.class);
    private final BukkitScheduler scheduler = mock(BukkitScheduler.class);
    private final PortalConfiguration portalConfig = mock(PortalConfiguration.class);
    private final Messenger messenger = new Messenger(PortalStorageCommandsTest.PREFIX);
    private final CommandSender sender = mock(CommandSender.class);
    private final CommandSourceStack source = mock(CommandSourceStack.class);
    private final PortalStorageCommands commands =
            new PortalStorageCommands(this.plugin, this.portalConfig, this.messenger);

    @Test
    void reloadAnnouncesTheReloadAndReadsTheStorageAsynchronously() throws Exception {
        int result = this.execute();

        assertEquals(CommandResults.SUCCESS, result);
        verify(this.sender).sendMessage(this.line(MinecartPortalMessages.reloadStarted(), MessagePalette.BODY));
        verify(this.portalConfig).loadAsync(any());
        verifyNoMoreInteractions(this.sender);
    }

    @Test
    void reloadDeliversItsSuccessOnTheServerThread() throws Exception {
        this.execute();
        this.loadCallback().accept(true);

        verify(this.sender, times(1)).sendMessage(any(Component.class));
        this.scheduledTask().run();

        verify(this.sender).sendMessage(this.line(MinecartPortalMessages.reloadSucceeded(), MessagePalette.SUCCESS));
    }

    @Test
    void reloadDeliversItsFailureOnTheServerThread() throws Exception {
        this.execute();
        this.loadCallback().accept(false);
        this.scheduledTask().run();

        verify(this.sender).sendMessage(this.line(MinecartPortalMessages.reloadFailed(), MessagePalette.ERROR));
    }

    private int execute() throws Exception {
        when(this.source.getSender()).thenReturn(this.sender);
        when(this.plugin.getServer()).thenReturn(this.server);
        when(this.server.getScheduler()).thenReturn(this.scheduler);

        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        dispatcher.register(this.commands.reload());

        return dispatcher.execute("reload", this.source);
    }

    @SuppressWarnings("unchecked")
    private Consumer<Boolean> loadCallback() {
        ArgumentCaptor<Consumer<Boolean>> callback = ArgumentCaptor.forClass(Consumer.class);
        verify(this.portalConfig).loadAsync(callback.capture());

        return callback.getValue();
    }

    private Runnable scheduledTask() {
        ArgumentCaptor<Runnable> task = ArgumentCaptor.forClass(Runnable.class);
        verify(this.scheduler).runTask(eq(this.plugin), task.capture());

        return task.getValue();
    }

    private Component line(Component body, TextColor color) {
        return this.messenger.prefixed(body.colorIfAbsent(color));
    }
}
