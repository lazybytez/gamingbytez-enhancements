package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar;

import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;

import java.util.Map;

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
     * Returns the ItemFrame located at the given pedestal.
     *
     * @param location The location of the pedestal.
     * @return The ItemFrame at the given pedestal.
     */
    ItemFrame getPedestal(PedestalLocation location);

    /**
     * Returns a map of all pedestals of the altar.
     *
     * @return Map of all pedestals.
     */
    Map<PedestalLocation, ItemFrame> getPedestals();
}
