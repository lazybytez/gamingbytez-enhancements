package de.lazybytez.gamingbytezenhancements.feature.minecartportal.model;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple model that represents a Minecart Portal with all its attributes.
 * <p>
 * A portal has a name, a portal location and a destination.
 * The portal location is where a player sitting in a minecart triggers the portal.
 * The destination location is where the player will be teleported to.
 * <p>
 * Note that MinecartPortals are immutable to prevent concurrency issues.
 */
public class MinecartPortal implements ConfigurationSerializable {
    /**
     * Name of the portal
     */
    private String name;

    /**
     * Location where the portal can be triggered.
     */
    private Location portal;

    /**
     * Location where the player (+ his cart) will be teleported to.
     */
    private Location destination;

    public MinecartPortal() {
    }

    public MinecartPortal(String name, Location portal, Location destination) {
        this.name = name;
        this.portal = portal;
        this.destination = destination;
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
     * Serialize function to store the object in a config file.
     *
     * @return the serialized MinecartPortal object
     */
    @Override
    public @NotNull Map<String, Object> serialize() {
        HashMap<String, Object> serialized = new HashMap<>();

        serialized.put("name", this.name);
        serialized.put("portal", this.portal);
        serialized.put("destination", this.destination);

        return serialized;
    }

    public String getName() {
        return name;
    }

    public Location getPortal() {
        return portal;
    }

    public Location getDestination() {
        return destination;
    }
}
