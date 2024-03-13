package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.GlowItemFrame;

/**
 * This interface represents an Altar.
 * <p>
 * An Altar consists of five pedestals, each representing a different direction (center, north, south, east, west).
 */
public interface AltarInterface {
    /**
     * Returns the location of the altar.
     * <p>
     * The location is always the diamond block below the item frame of the center pedestal.
     *
     * @return Location of the altar.
     */
    Location getLocation();

    /**
     * Returns the item frame in the center pedestal of the altar.
     * <p>
     * This item frame is the most important one, as it represents the center of the altar.
     * A player must place last item in this item frame to complete a recipe.
     *
     * @return ItemFrame in the center.
     */
    GlowItemFrame getCenter();

    /**
     * Returns the item frame in the north pedestal of the altar.
     *
     * @return ItemFrame in the north.
     */
    GlowItemFrame getNorth();

    /**
     * Returns the item frame in the south pedestal of the altar.
     *
     * @return ItemFrame in the south.
     */
    GlowItemFrame getSouth();

    /**
     * Returns the item frame in the east pedestal of the altar.
     *
     * @return ItemFrame in the east.
     */
    GlowItemFrame getEast();

    /**
     * Returns the item frame in the west pedestal of the altar.
     *
     * @return ItemFrame in the west.
     */
    GlowItemFrame getWest();
}
