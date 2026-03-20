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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This abstract class represents an Altar.
 * <p>
 * An Altar consists of some number of pedestals, each representing a different spot to place an item for
 * a recipe.
 * This abstract altar expects the center pedestal to be the most important one,
 * as it represents the center of the altar. However, center does not force it to be in the middle of the altar.
 * It is just used as a relative position for the other pedestals.
 * <p>
 * Having this abstract class allows for easier extension of the altar, as we can just use
 * the constructor and compute the other pedestals based on the center pedestal.
 */
public abstract class AbstractAltar implements AltarInterface {
    /**
     * The location of the altar. This is the diamond block
     * below the item frame of the center pedestal.
     */
    protected final Location location;
    /**
     * The entity type of the pedestal inputs.
     */
    protected final EntityType pedestalEntityType;

    /**
     * Cache of the pedestals of the altar.
     * Realised using UUIDs to avoid memory leaks.
     */
    private final Map<PedestalLocation, UUID> pedestalCache = new HashMap<>();

    /**
     * Create a new Altar.
     *
     * @param location The location of the altar. This is the diamond block
     *                 below the item frame of the center pedestal.
     */
    public AbstractAltar(
            @NotNull Location location,
            @NotNull EntityType pedestalEntityType
    ) {
        this.location = location;
        this.pedestalEntityType = pedestalEntityType;
    }

    /**
     * Returns the location of the altars pedestals.
     *
     * @return Location of the altar pedestals.
     */
    public abstract Map<PedestalLocation, Vector> getPedestalLocations();

    public ItemFrame getPedestal(PedestalLocation pedestalLocation) {
        if (pedestalCache.containsKey(pedestalLocation)) {
            Entity entity = this.location.getWorld().getEntity(pedestalCache.get(pedestalLocation));
            if (entity instanceof ItemFrame) {
                return (ItemFrame) entity;
            }

            // If not an ItemFrame, we try again to find it below.
        }

        Vector relativePedestalLocation = this.getPedestalLocations().get(pedestalLocation);
        Location pedestalEntityLocation = this.location.clone().add(relativePedestalLocation);

        ItemFrame pedestal = this.getPedestalAtLocation(pedestalEntityLocation);

        this.pedestalCache.put(pedestalLocation, pedestal.getUniqueId());

        return pedestal;
    }

    @Override
    public Map<PedestalLocation, ItemFrame> getPedestals() {
        return this.getPedestalLocations().keySet().stream()
                .collect(
                        HashMap::new,
                        (map, pedestalLocation) -> map.put(pedestalLocation, this.getPedestal(pedestalLocation)),
                        HashMap::putAll
                );
    }

    /**
     * Returns the ItemFrame located at the given pedestal.
     *
     * @param location The location of the pedestal.
     * @return The ItemFrame at the given pedestal.
     */
    protected ItemFrame getPedestalAtLocation(Location location) {
        ItemFrame result = null;
        Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, 1, 1, 1);
        if (nearbyEntities.isEmpty()) {
            return null;
        }

        for (Entity entity : nearbyEntities) {
            if (entity.getType().equals(this.pedestalEntityType)
                    && entity instanceof ItemFrame
                    && entity.getLocation().getBlockX() == location.getBlockX()
                    && entity.getLocation().getBlockY() == location.getBlockY()
                    && entity.getLocation().getBlockZ() == location.getBlockZ()) {
                result = (ItemFrame) entity;
                break;
            }
        }

        return result;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }
}
