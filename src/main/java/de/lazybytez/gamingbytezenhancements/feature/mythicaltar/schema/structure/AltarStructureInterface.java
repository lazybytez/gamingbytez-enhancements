package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.schema.structure;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
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
     * <p>
     * Note: Implementations should ensure that the EntityType provided is either ITEM_FRAME or GLOW_ITEM_FRAME.
     *
     * @return Structure of the pedestals.
     */
    Map<Vector, EntityType> getPedestalStructure();

    /**
     * Returns the class of the altar.
     *
     * @return Class of the altar.
     */
    Class<? extends AltarInterface> getAltarClass();
}
