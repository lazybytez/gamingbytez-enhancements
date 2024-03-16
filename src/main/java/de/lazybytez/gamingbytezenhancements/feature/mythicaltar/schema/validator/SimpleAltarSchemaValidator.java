package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.schema.validator;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.schema.structure.AltarStructureInterface;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Map;

/**
 * This class represents the validator for the altar schema.
 * <p>
 * It is a simple implementation of the AltarSchemaValidatorInterface that loops through the
 * altar and pedestal structure and checks if the blocks are valid.
 * Locations are handled using simple addition.
 */
public class SimpleAltarSchemaValidator implements AltarSchemaValidatorInterface {
    @Override
    public boolean validate(AltarStructureInterface altarStructure, Location location, World world) {
        // First validate the pedestal as these most likely are fewer blocks to check.
        // If the pedestal is not valid, the altar is not valid anyway.
        return this.validateStructure(altarStructure, location, world)
                && this.validatePedestal(altarStructure, location, world);
    }

    @Override
    public boolean validateStructure(AltarStructureInterface altarStructure, Location location, World world) {
        return validateStructureSchematic(altarStructure.getAltarStructure(), location, world);
    }

    @Override
    public boolean validatePedestal(AltarStructureInterface pedestalStructure, Location location, World world) {
        return validatePedestalSchematic(pedestalStructure.getPedestalStructure(), location, world);
    }

    /**
     * Validates the given altar block structure.
     * <p>
     * Validates the given altar block structure by checking if the blocks are at the correct location.
     *
     * @param schema   The altar structure to validate.
     * @param location The location of the altar. This is the location of the center block.
     * @param world    The world in which the altar is located.
     * @return True if the structure is valid, false otherwise.
     */
    private boolean validateStructureSchematic(Map<Vector, Material> schema, Location location, World world) {
        for (Vector schemaVector : schema.keySet()) {
            Location relativeLocation = location.clone().add(schemaVector);
            Block block = world.getBlockAt(relativeLocation);

            if (block.getType() != schema.get(schemaVector)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validates the given pedestal structure.
     * <p>
     * Validates the given pedestal structure by checking if the item frames are at the correct location.
     * <p>
     * Unfortunately, we can't check for the item frame directly (as they are entities),
     * so we need to check for entities at the location.
     *
     * @param schema   The pedestal structure to validate
     * @param location The location of the altar. This is the location of the center block.
     * @param world    The world in which the altar is located.
     * @return True if the structure is valid, false otherwise.
     */
    private boolean validatePedestalSchematic(Map<Vector, EntityType> schema, Location location, World world) {
        for (Vector schemaVector : schema.keySet()) {
            Location relativeLocation = location.clone().add(schemaVector);

            int relX = relativeLocation.getBlockX();
            int relY = relativeLocation.getBlockY();
            int relZ = relativeLocation.getBlockZ();

            // ItemFrame are entities, so we need to check for entities at the location
            // Not as efficient as checking for blocks, but it's the only way to check for item frames.
            Collection<Entity> entities = world.getNearbyEntities(relativeLocation, 1, 1, 1);

            boolean found = false;
            for (Entity entity : entities) {
                Location entityLocation = entity.getLocation();

                if (entity.getType().equals(schema.get(schemaVector))
                        && entityLocation.getBlockX() == relX
                        && entityLocation.getBlockY() == relY
                        && entityLocation.getBlockZ() == relZ) {
                    found = true;

                    break;
                }
            }

            if (!found) {
                Bukkit.broadcastMessage("Pedestal not found at " + relativeLocation + " for " + schema.get(schemaVector) + " at " + schemaVector);
                return false;
            }
        }

        return true;
    }
}
