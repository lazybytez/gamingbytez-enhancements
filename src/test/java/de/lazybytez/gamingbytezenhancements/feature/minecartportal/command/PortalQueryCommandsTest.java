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
import de.lazybytez.gamingbytezenhancements.feature.minecartportal.model.MinecartPortal;
import de.lazybytez.gamingbytezenhancements.lib.command.CommandResults;
import de.lazybytez.gamingbytezenhancements.lib.message.LocationFormat;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePalette;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePrefix;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class PortalQueryCommandsTest {
    private static final MessagePrefix PREFIX = MessagePrefix.of("MinecartPortals", NamedTextColor.LIGHT_PURPLE);
    private static final String LABEL = "minecartportals";

    private final PortalConfiguration portalConfig = mock(PortalConfiguration.class);
    private final Messenger messenger = new Messenger(PortalQueryCommandsTest.PREFIX);
    private final CommandSender sender = mock(CommandSender.class);
    private final CommandSourceStack source = mock(CommandSourceStack.class);
    private final World world = mock(World.class);
    private final PortalQueryCommands commands = new PortalQueryCommands(
            this.portalConfig,
            this.messenger,
            new PortalArguments(this.portalConfig));

    @Test
    void listReportsTheEmptyCaseWithoutEmittingBullets() throws Exception {
        when(this.portalConfig.getPortals()).thenReturn(List.of());

        int result = this.execute("minecartportals list");

        assertEquals(CommandResults.SUCCESS, result);
        verify(this.sender).sendMessage(this.messenger.prefixed(
                MinecartPortalMessages.noneRegistered().colorIfAbsent(MessagePalette.BODY)));
        verifyNoMoreInteractions(this.sender);
    }

    @Test
    void listEmitsAHeadingWithTheCountAndOneBulletPerPortal() throws Exception {
        MinecartPortal spawn = new MinecartPortal("spawn", this.location(12.8, 64.0, -4.2), null);
        MinecartPortal nether = new MinecartPortal("nether", null, null);
        when(this.portalConfig.getPortals()).thenReturn(List.of(spawn, nether));

        int result = this.execute("minecartportals list");

        assertEquals(CommandResults.SUCCESS, result);
        List<String> lines = this.renderedLines(3);
        assertEquals("[MinecartPortals] Registered Minecart Portals (2)", lines.get(0));
        assertEquals(List.of("• spawn", "• nether"), List.of(lines.get(1), lines.get(2)));
    }

    @Test
    void listGivesEveryBulletAnInspectClickAndALocationHover() throws Exception {
        Location entry = this.location(12.8, 64.0, -4.2);
        when(this.portalConfig.getPortals()).thenReturn(List.of(new MinecartPortal("spawn", entry, null)));

        this.execute("minecartportals list");

        Component bullet = this.sentMessages(2).get(1);
        Component marked = PortalQueryCommandsTest.componentWithClickEvent(bullet);
        assertEquals(ClickEvent.suggestCommand("/minecartportals inspect spawn"), marked.clickEvent());
        assertEquals(HoverEvent.showText(LocationFormat.format(entry)), marked.hoverEvent());
    }

    @Test
    void listSuggestsTheLabelTheSenderInvokedRatherThanTheCanonicalOne() throws Exception {
        Location entry = this.location(12.8, 64.0, -4.2);
        when(this.portalConfig.getPortals()).thenReturn(List.of(new MinecartPortal("spawn", entry, null)));
        when(this.source.getSender()).thenReturn(this.sender);

        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        dispatcher.register(Commands.literal("gbmcp").then(this.commands.list()));
        dispatcher.execute("gbmcp list", this.source);

        Component marked = PortalQueryCommandsTest.componentWithClickEvent(this.sentMessages(2).get(1));
        assertEquals(ClickEvent.suggestCommand("/gbmcp inspect spawn"), marked.clickEvent());
    }

    @Test
    void inspectRendersEntryAndExitAsFieldLines() throws Exception {
        MinecartPortal spawn = new MinecartPortal(
                "spawn",
                this.location(12.8, 64.0, -4.2),
                this.location(-1.2, 70.9, 8.1));
        when(this.portalConfig.getPortalByName("spawn")).thenReturn(spawn);

        int result = this.execute("minecartportals inspect spawn");

        assertEquals(CommandResults.SUCCESS, result);
        assertEquals(List.of(
                "[MinecartPortals] Minecart Portal spawn",
                "Entry: world (12, 64, -5)",
                "Exit: world (-2, 70, 8)"), this.renderedLines(3));
    }

    @Test
    void inspectReportsAPortalThatDoesNotExist() throws Exception {
        int result = this.execute("minecartportals inspect spawn");

        assertEquals(CommandResults.FAILURE, result);
        verify(this.sender).sendMessage(this.messenger.prefixed(
                MinecartPortalMessages.notFound("spawn").colorIfAbsent(MessagePalette.ERROR)));
    }

    private int execute(String input) throws Exception {
        when(this.source.getSender()).thenReturn(this.sender);

        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        dispatcher.register(Commands.literal(PortalQueryCommandsTest.LABEL)
                .then(this.commands.list())
                .then(this.commands.inspect()));

        return dispatcher.execute(input, this.source);
    }

    private Location location(double x, double y, double z) {
        when(this.world.getKey()).thenReturn(NamespacedKey.minecraft("world"));

        return new Location(this.world, x, y, z);
    }

    private List<Component> sentMessages(int expectedCount) {
        ArgumentCaptor<Component> captor = ArgumentCaptor.forClass(Component.class);
        verify(this.sender, times(expectedCount)).sendMessage(captor.capture());

        return captor.getAllValues();
    }

    private List<String> renderedLines(int expectedCount) {
        return this.sentMessages(expectedCount).stream()
                .map(line -> PlainTextComponentSerializer.plainText().serialize(line).trim())
                .toList();
    }

    private static Component componentWithClickEvent(Component root) {
        for (Component candidate : PortalQueryCommandsTest.flatten(root)) {
            if (candidate.clickEvent() != null) {
                return candidate;
            }
        }

        throw new AssertionError("no component in the tree carries a click event");
    }

    private static List<Component> flatten(Component root) {
        List<Component> components = new ArrayList<>();
        components.add(root);

        for (Component child : root.children()) {
            components.addAll(PortalQueryCommandsTest.flatten(child));
        }

        return components;
    }
}
