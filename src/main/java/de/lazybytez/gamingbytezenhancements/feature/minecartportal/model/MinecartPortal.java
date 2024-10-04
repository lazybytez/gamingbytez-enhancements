package de.lazybytez.gamingbytezenhancements.feature.minecartportal.model;

import org.bukkit.Location;

/**
 * Simple model that represents a Minecart Portal with all its attributes.
 * <p>
 * A portal has a name, a portal location and a destination.
 * The portal location is where a player sitting in a minecart triggers the portal.
 * The destination location is where the player will be teleported to.
 * <p>
 * Note that MinecartPortals are immutable to prevent concurrency issues.
 */
public class MinecartPortal {
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
