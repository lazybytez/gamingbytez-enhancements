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
package de.lazybytez.gamingbytezenhancements.lib.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class MessengerTest {
    private static final MessagePrefix PREFIX = MessagePrefix.of("TestFeature", NamedTextColor.LIGHT_PURPLE);

    private final Messenger messenger = new Messenger(PREFIX);

    @Test
    void rendersHeadingInHeadingColor() {
        CommandSender sender = mock(CommandSender.class);

        this.messenger.heading(sender, "Portals");

        verify(sender).sendMessage(prefixed(Component.text("Portals", MessagePalette.HEADING)));
    }

    @Test
    void rendersWarningInEmphasisColor() {
        CommandSender sender = mock(CommandSender.class);

        this.messenger.warning(sender, "Save failed");

        verify(sender).sendMessage(prefixed(Component.text("Save failed", MessagePalette.EMPHASIS)));
    }

    @Test
    void rendersInfoSuccessAndErrorInTheirSemanticColors() {
        CommandSender sender = mock(CommandSender.class);

        this.messenger.info(sender, "Info");
        this.messenger.success(sender, "Success");
        this.messenger.error(sender, "Error");

        verify(sender).sendMessage(prefixed(Component.text("Info", MessagePalette.BODY)));
        verify(sender).sendMessage(prefixed(Component.text("Success", MessagePalette.SUCCESS)));
        verify(sender).sendMessage(prefixed(Component.text("Error", MessagePalette.ERROR)));
    }

    @Test
    void keepsColorsAlreadySetOnAComponentBody() {
        CommandSender sender = mock(CommandSender.class);
        Component body = Component.text("Portal", MessagePalette.SUBJECT);

        this.messenger.success(sender, body);

        verify(sender).sendMessage(prefixed(body));
    }

    @Test
    void servesCommandSendersAndPlayersThroughTheSameApi() {
        CommandSender sender = mock(CommandSender.class);
        Player player = mock(Player.class);

        this.messenger.info(sender, "Shared");
        this.messenger.info(player, "Shared");

        Component expected = prefixed(Component.text("Shared", MessagePalette.BODY));

        verify(sender).sendMessage(expected);
        verify(player).sendMessage(expected);
    }

    @Test
    void rendersDetailIndentedAndWithoutPrefix() {
        CommandSender sender = mock(CommandSender.class);

        this.messenger.detail(sender, Component.text("Continuation"));

        verify(sender).sendMessage(Component.text("  ")
                .append(Component.text("Continuation", MessagePalette.BODY)));
    }

    @Test
    void rendersBulletIndentedWithADecorationGlyphAndWithoutPrefix() {
        CommandSender sender = mock(CommandSender.class);

        this.messenger.bullet(sender, Component.text("Entry"));

        verify(sender).sendMessage(Component.text("  ")
                .append(Component.text("• ", MessagePalette.DECORATION))
                .append(Component.text("Entry", MessagePalette.BODY)));
    }

    @Test
    void rendersFieldIndentedWithABodyLabelAndAValueAndWithoutPrefix() {
        CommandSender sender = mock(CommandSender.class);

        this.messenger.field(sender, "Destination", Component.text("spawn"));

        verify(sender).sendMessage(Component.text("  ")
                .append(Component.text("Destination", MessagePalette.BODY))
                .append(Component.text(": ", MessagePalette.DECORATION))
                .append(Component.text("spawn", MessagePalette.VALUE)));
    }

    @Test
    void prefixedReturnsTheComponentWithoutSendingIt() {
        CommandSender sender = mock(CommandSender.class);
        Component body = Component.text("Broadcast", MessagePalette.EMPHASIS);

        Component result = this.messenger.prefixed(body);

        assertEquals(PREFIX.component().append(body), result);
        verifyNoInteractions(sender);
    }

    private static Component prefixed(Component body) {
        return PREFIX.component().append(body);
    }
}
