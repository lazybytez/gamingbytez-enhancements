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
import net.kyori.adventure.text.Component;

/**
 * The wording of every player facing Minecart Portal message.
 *
 * A message carries no line colour of its own, so the messenger colours it by
 * the role of the line it opens. Only the portal name is coloured here, which
 * keeps the named subject readable inside an otherwise uniform line.
 */
public final class MinecartPortalMessages {
    private MinecartPortalMessages() {
    }

    /**
     * Reject a name that exceeds the length limit.
     *
     * @return the rejection wording
     */
    public static Component nameTooLong() {
        return Component.text("Portal names must not exceed " + MinecartPortal.MAX_NAME_LENGTH
                + " characters.");
    }

    /**
     * Reject a name that carries unsupported characters.
     *
     * @return the rejection wording
     */
    public static Component nameNotAlphanumeric() {
        return Component.text("Portal names must be alphanumeric.");
    }

    /**
     * Reject a name that is already taken.
     *
     * @param name the requested portal name
     * @return the rejection wording
     */
    public static Component alreadyExists(String name) {
        return MinecartPortalMessages.about("A portal named \"", name, "\" already exists.");
    }

    /**
     * Confirm a newly registered portal.
     *
     * @param name the portal name
     * @return the confirmation wording
     */
    public static Component added(String name) {
        return MinecartPortalMessages.about("Added portal \"", name, "\".");
    }

    /**
     * Report that no portal carries the requested name.
     *
     * @param name the requested portal name
     * @return the failure wording
     */
    public static Component notFound(String name) {
        return MinecartPortalMessages.about("Could not find a portal named \"", name, "\".");
    }

    /**
     * Report a deletion that did not take effect.
     *
     * @param name the portal name
     * @return the failure wording
     */
    public static Component deleteFailed(String name) {
        return MinecartPortalMessages.about("Failed to delete portal \"", name, "\".");
    }

    /**
     * Confirm a deleted portal.
     *
     * @param name the portal name
     * @return the confirmation wording
     */
    public static Component deleted(String name) {
        return MinecartPortalMessages.about("Deleted portal \"", name, "\".");
    }

    /**
     * Reject an entry point that does not sit on a detector rail.
     *
     * @return the rejection wording
     */
    public static Component entryNeedsDetectorRail() {
        return Component.text("An entry point must be placed on a detector rail.");
    }

    /**
     * Reject an exit point that does not sit on a normal rail.
     *
     * @return the rejection wording
     */
    public static Component exitNeedsRail() {
        return Component.text("An exit point must be placed on a normal rail.");
    }

    /**
     * Report an update that did not take effect.
     *
     * @param name the portal name
     * @return the failure wording
     */
    public static Component updateFailed(String name) {
        return MinecartPortalMessages.about("Failed to update \"", name, "\".");
    }

    /**
     * Confirm a moved entry point.
     *
     * @param name the portal name
     * @return the confirmation wording
     */
    public static Component entryUpdated(String name) {
        return MinecartPortalMessages.about("Updated entry point for \"", name, "\".");
    }

    /**
     * Confirm a moved exit point.
     *
     * @param name the portal name
     * @return the confirmation wording
     */
    public static Component exitUpdated(String name) {
        return MinecartPortalMessages.about("Updated exit point for \"", name, "\".");
    }

    /**
     * Reject a sender that is not a player standing on a rail.
     *
     * @return the rejection wording
     */
    public static Component playerOnly() {
        return Component.text("This command can only be used by a player on a rail.");
    }

    /**
     * Report that no portal is registered at all.
     *
     * @return the empty listing wording
     */
    public static Component noneRegistered() {
        return Component.text("No Minecart Portals are registered.");
    }

    /**
     * Announce a reload that has just been started.
     *
     * @return the progress wording
     */
    public static Component reloadStarted() {
        return Component.text("Reloading portals from storage...");
    }

    /**
     * Confirm a finished reload.
     *
     * @return the confirmation wording
     */
    public static Component reloadSucceeded() {
        return Component.text("Reloaded portals from storage.");
    }

    /**
     * Report a reload that did not complete.
     *
     * @return the failure wording
     */
    public static Component reloadFailed() {
        return Component.text("Failed to reload portals from storage.");
    }

    /**
     * Explain why a block belonging to a portal cannot be broken.
     *
     * This wording is also used by the destruction listener in the sibling
     * {@code listener} package, so it is the one factory method exposed publicly.
     *
     * @return the rejection wording
     */
    public static Component blockedDestruction() {
        return Component.text("Please remove the Minecart Portal first before breaking this block!");
    }

    /**
     * Warn that a change survives in memory only.
     *
     * @return the warning wording
     */
    public static Component saveFailed() {
        return Component.text("The change is only kept in memory, writing it to disk failed.");
    }

    private static Component about(String prefix, String name, String suffix) {
        return Component.textOfChildren(
                Component.text(prefix),
                Component.text(name, MessagePalette.SUBJECT),
                Component.text(suffix)
        );
    }
}
