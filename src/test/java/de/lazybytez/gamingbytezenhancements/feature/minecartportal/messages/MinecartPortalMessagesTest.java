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
package de.lazybytez.gamingbytezenhancements.feature.minecartportal.messages;

import de.lazybytez.gamingbytezenhancements.feature.minecartportal.model.MinecartPortal;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePalette;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MinecartPortalMessagesTest {
    @Test
    void buildsTheLengthMessageFromTheMaximumLengthConstant() {
        String expected = "Portal names must not exceed " + MinecartPortal.MAX_NAME_LENGTH + " characters.";

        assertEquals(expected, plain(MinecartPortalMessages.nameTooLong()));
    }

    @Test
    void keepsTheExistingWordingOfEveryPortalSpecificMessage() {
        assertEquals("A portal named \"north\" already exists.", plain(MinecartPortalMessages.alreadyExists("north")));
        assertEquals("Added portal \"north\".", plain(MinecartPortalMessages.added("north")));
        assertEquals("Could not find a portal named \"north\".", plain(MinecartPortalMessages.notFound("north")));
        assertEquals("Failed to delete portal \"north\".", plain(MinecartPortalMessages.deleteFailed("north")));
        assertEquals("Deleted portal \"north\".", plain(MinecartPortalMessages.deleted("north")));
        assertEquals("Failed to update \"north\".", plain(MinecartPortalMessages.updateFailed("north")));
        assertEquals("Updated entry point for \"north\".", plain(MinecartPortalMessages.entryUpdated("north")));
        assertEquals("Updated exit point for \"north\".", plain(MinecartPortalMessages.exitUpdated("north")));
    }

    @Test
    void keepsTheExistingWordingOfEveryConstantMessage() {
        assertEquals("Portal names must be alphanumeric.", plain(MinecartPortalMessages.nameNotAlphanumeric()));
        assertEquals("An entry point must be placed on a detector rail.",
                plain(MinecartPortalMessages.entryNeedsDetectorRail()));
        assertEquals("An exit point must be placed on a normal rail.",
                plain(MinecartPortalMessages.exitNeedsRail()));
        assertEquals("This command can only be used by a player on a rail.",
                plain(MinecartPortalMessages.playerOnly()));
        assertEquals("No Minecart Portals are registered.", plain(MinecartPortalMessages.noneRegistered()));
        assertEquals("Reloading portals from storage...", plain(MinecartPortalMessages.reloadStarted()));
        assertEquals("Reloaded portals from storage.", plain(MinecartPortalMessages.reloadSucceeded()));
        assertEquals("Failed to reload portals from storage.", plain(MinecartPortalMessages.reloadFailed()));
        assertEquals("Please remove the Minecart Portal first before breaking this block!",
                plain(MinecartPortalMessages.blockedDestruction()));
    }

    @Test
    void warnsThatAFailedSaveLeftTheChangeInMemoryOnly() {
        String message = plain(MinecartPortalMessages.saveFailed());

        assertTrue(message.contains("memory"));
        assertTrue(message.contains("disk"));
    }

    @Test
    void rendersThePortalNameAsAHighlightedSubject() {
        List<Component> highlighted = subjects(MinecartPortalMessages.added("north"));

        assertEquals(List.of("north"), highlighted.stream().map(MinecartPortalMessagesTest::plain).toList());
        assertEquals(MessagePalette.SUBJECT, highlighted.getFirst().color());
    }

    @Test
    void leavesTheSurroundingWordingUncoloured() {
        Component message = MinecartPortalMessages.notFound("north");

        assertNull(message.color());
        assertEquals(1, subjects(message).size());
    }

    private static List<Component> subjects(Component message) {
        List<Component> found = new ArrayList<>();

        for (Component child : message.children()) {
            if (MessagePalette.SUBJECT.equals(child.color())) {
                found.add(child);
            }
        }

        return found;
    }

    private static String plain(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
}
