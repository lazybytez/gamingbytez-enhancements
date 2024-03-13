package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.GlowItemFrame;

public class MythicAltar implements AltarInterface {
    private final GlowItemFrame center;
    private final GlowItemFrame north;
    private final GlowItemFrame south;
    private final GlowItemFrame east;
    private final GlowItemFrame west;

    public MythicAltar(
            GlowItemFrame center,
            GlowItemFrame north,
            GlowItemFrame south,
            GlowItemFrame east,
            GlowItemFrame west
    ) {
        this.center = center;
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
    }

    @Override
    public Location getLocation() {
        return this.getCenter().getLocation();
    }

    @Override
    public GlowItemFrame getCenter() {
        return this.center;
    }

    @Override
    public GlowItemFrame getNorth() {
        return this.north;
    }

    @Override
    public GlowItemFrame getSouth() {
        return this.south;
    }

    @Override
    public GlowItemFrame getEast() {
        return this.east;
    }

    @Override
    public GlowItemFrame getWest() {
        return this.west;
    }
}
