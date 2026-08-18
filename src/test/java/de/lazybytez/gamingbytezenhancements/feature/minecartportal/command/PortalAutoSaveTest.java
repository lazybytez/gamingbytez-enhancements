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
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePalette;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePrefix;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class PortalAutoSaveTest {
    private static final MessagePrefix PREFIX = MessagePrefix.of("MinecartPortals", MessagePalette.HEADING);

    private final Plugin plugin = mock(Plugin.class);
    private final Server server = mock(Server.class);
    private final BukkitScheduler scheduler = mock(BukkitScheduler.class);
    private final PortalConfiguration portalConfig = mock(PortalConfiguration.class);
    private final Messenger messenger = new Messenger(PREFIX);
    private final CommandSender sender = mock(CommandSender.class);
    private final PortalAutoSave autoSave = new PortalAutoSave(this.plugin, this.portalConfig, this.messenger);

    @Test
    void sendsNothingWhenTheSaveSucceeds() {
        this.autoSave.requestSave(this.sender);

        this.saveCallback().accept(true);

        verifyNoInteractions(this.sender);
        verifyNoInteractions(this.server);
    }

    @Test
    void sendsExactlyOneWarningWhenTheSaveFails() {
        this.givenScheduler();

        this.autoSave.requestSave(this.sender);
        this.saveCallback().accept(false);
        this.scheduledTask().run();

        verify(this.sender).sendMessage(this.messenger.prefixed(
                MinecartPortalMessages.saveFailed().color(MessagePalette.EMPHASIS)));
        verifyNoMoreInteractions(this.sender);
    }

    @Test
    void dispatchesTheFailureWarningThroughTheScheduler() {
        this.givenScheduler();

        this.autoSave.requestSave(this.sender);
        this.saveCallback().accept(false);

        verify(this.scheduler, times(1)).runTask(eq(this.plugin), any(Runnable.class));
        verifyNoInteractions(this.sender);
    }

    private void givenScheduler() {
        when(this.plugin.getServer()).thenReturn(this.server);
        when(this.server.getScheduler()).thenReturn(this.scheduler);
    }

    @SuppressWarnings("unchecked")
    private Consumer<Boolean> saveCallback() {
        ArgumentCaptor<Consumer<Boolean>> callback = ArgumentCaptor.forClass(Consumer.class);
        verify(this.portalConfig).saveAsync(callback.capture());

        return callback.getValue();
    }

    private Runnable scheduledTask() {
        ArgumentCaptor<Runnable> task = ArgumentCaptor.forClass(Runnable.class);
        verify(this.scheduler).runTask(eq(this.plugin), task.capture());

        return task.getValue();
    }
}
