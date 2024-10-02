package de.lazybytez.gamingbytezenhancements.feature.minecartportal.model;

import org.bukkit.Location;

/**
 * Simple model that represents a Minecart Portal with all its attributes.
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

    public void setName(String name) {
        this.name = name;
    }

    public Location getPortal() {
        return portal;
    }

    public void setPortal(Location portal) {
        this.portal = portal;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }
}
