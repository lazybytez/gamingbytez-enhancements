package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.schema.structure;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.Map;

/**
 * This interface represents the structure of an altar.
 */
public interface AltarStructureInterface {
    /**
     * Returns the structure of the altar.
     * <p>
     * The structure is a map of vectors and materials, representing the blocks of the altar.
     * The vector (key of the map) is relative to the location of the altar.
     *
     * @return Structure of the altar.
     */
    Map<Vector, Material> getAltarStructure();

    /**
     * Returns the structure of the pedestals.
     * <p>
     * The structure is a map of vectors and entity types, representing the top of the pedestals.
     * The Vector (key of the map) is relative to the location of the altars center pedestal.
     * <p>
     * The entity type should be either an item frame or a glow item frame.
     *
     * @return Structure of the pedestals.
     */
    Map<Vector, EntityType> getPedestalStructure();
}
