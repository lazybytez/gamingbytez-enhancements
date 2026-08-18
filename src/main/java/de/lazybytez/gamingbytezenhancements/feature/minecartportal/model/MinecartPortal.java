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
package de.lazybytez.gamingbytezenhancements.feature.minecartportal.model;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Simple model that represents a Minecart Portal with all its attributes.
 * <p>
 * A portal has a name, a portal location and a destination.
 * The portal location is where a player sitting in a minecart triggers the portal.
 * The destination location is where the player will be teleported to.
 * <p>
 * Instances are immutable: all fields are final, the constructor clones the
 * {@link Location} arguments it receives, and the accessors clone the stored
 * locations before returning them. No caller can observe or mutate the internal
 * state, which makes instances safe to share across threads, e.g. when held in a
 * {@link java.util.concurrent.CopyOnWriteArrayList}.
 */
public final class MinecartPortal implements ConfigurationSerializable {
    /**
     * The longest name a portal may carry.
     */
    public static final int MAX_NAME_LENGTH = 16;

    private static final Pattern NAME_PATTERN = Pattern.compile("[A-Za-z0-9]+");

    /**
     * The characters Brigadier's single-word argument reads, which is what decides whether
     * a stored name can be named in a command at all.
     */
    private static final Pattern COMMAND_ARGUMENT_PATTERN = Pattern.compile("[0-9A-Za-z_.+\\-]+");

    /**
     * Name of the portal
     */
    private final String name;

    /**
     * Location where the portal can be triggered.
     */
    private final Location portal;

    /**
     * Location where the player (+ his cart) will be teleported to.
     */
    private final Location destination;

    /**
     * Creates a new immutable MinecartPortal.
     * <p>
     * The given locations are cloned so that later mutation of the arguments by the
     * caller cannot affect this instance. Both locations may be {@code null} to
     * represent a portal that has not been fully configured yet.
     *
     * @param name        the name of the portal
     * @param portal      the location where the portal can be triggered, or {@code null}
     * @param destination the location the portal teleports to, or {@code null}
     */
    public MinecartPortal(String name, @Nullable Location portal, @Nullable Location destination) {
        this.name = name;
        this.portal = MinecartPortal.cloneLocation(portal);
        this.destination = MinecartPortal.cloneLocation(destination);
    }

    /**
     * Deserialize a MinecartPortal object from the storage (config file).
     *
     * @param data the data to deserialize
     * @return the deserialized MinecartPortal instance
     */
    public static MinecartPortal deserialize(Map<String, Object> data) {
        String name = (String) data.get("name");
        // In theory this cast is unsafe, however, the configuration should never be touched manually anyway
        Location portal = (Location) data.get("portal");
        Location destination = (Location) data.get("destination");

        return new MinecartPortal(name, portal, destination);
    }

    /**
     * Check whether a name exceeds the length a portal may carry.
     *
     * @param name the name a sender typed
     * @return whether the name is too long to register
     */
    public static boolean isNameTooLong(String name) {
        return name.length() > MinecartPortal.MAX_NAME_LENGTH;
    }

    /**
     * Check a name against the accepted character set.
     *
     * @param name the name a sender typed
     * @return whether the name consists of letters and digits only
     */
    public static boolean hasValidNameCharacters(String name) {
        return MinecartPortal.NAME_PATTERN.matcher(name).matches();
    }

    /**
     * Check whether a sender can name this portal in a command argument.
     * <p>
     * The accepted set is the one Brigadier's single-word argument reads, which is wider
     * than the set {@code add} enforces. A name outside it cannot be addressed at all,
     * because the argument stops reading at the first character it does not accept.
     *
     * @param name the name to check
     * @return whether a sender can address a portal carrying this name
     */
    public static boolean isAddressableName(String name) {
        return MinecartPortal.COMMAND_ARGUMENT_PATTERN.matcher(name).matches();
    }

    /**
     * Serialize function to store the object in a config file.
     *
     * @return the serialized MinecartPortal object
     */
    @Override
    public @NotNull Map<String, Object> serialize() {
        HashMap<String, Object> serialized = new HashMap<>();

        serialized.put("name", this.name);
        serialized.put("portal", MinecartPortal.cloneLocation(this.portal));
        serialized.put("destination", MinecartPortal.cloneLocation(this.destination));

        return serialized;
    }

    /**
     * Returns the name of the portal.
     *
     * @return the portal name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns a clone of the location where the portal can be triggered.
     *
     * @return a clone of the portal location, or {@code null} if it has not been set
     */
    public @Nullable Location getPortal() {
        return MinecartPortal.cloneLocation(this.portal);
    }

    /**
     * Returns a clone of the location the portal teleports to.
     *
     * @return a clone of the destination location, or {@code null} if it has not been set
     */
    public @Nullable Location getDestination() {
        return MinecartPortal.cloneLocation(this.destination);
    }

    /**
     * Null-safe clone helper used to defensively copy locations on the way in and out
     * of this class.
     *
     * @param location the location to clone, may be {@code null}
     * @return a clone of the given location, or {@code null} if the argument was {@code null}
     */
    private static @Nullable Location cloneLocation(@Nullable Location location) {
        if (location == null) {
            return null;
        }

        return location.clone();
    }
}
