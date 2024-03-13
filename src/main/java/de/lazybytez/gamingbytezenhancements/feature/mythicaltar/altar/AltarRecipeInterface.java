package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar;

import org.bukkit.inventory.ItemStack;

/**
 * This interface represents an AltarRecipe.
 * <p>
 * An AltarRecipe consists of five ItemStacks,
 * each representing a different pedestal (center, north, south, east, west).
 */
public interface AltarRecipeInterface {

    /**
     * Returns the ItemStack in the center pedestal of the altar.
     *
     * @return ItemStack in the center.
     */
    ItemStack getCenter();

    /**
     * Returns the ItemStack in the north pedestal of the altar.
     *
     * @return ItemStack in the north.
     */
    ItemStack getNorth();

    /**
     * Returns the ItemStack in the south pedestal of the altar.
     *
     * @return ItemStack in the south.
     */
    ItemStack getSouth();

    /**
     * Returns the ItemStack in the east pedestal of the altar.
     *
     * @return ItemStack in the east.
     */
    ItemStack getEast();

    /**
     * Returns the ItemStack in the west pedestal of the altar.
     *
     * @return ItemStack in the west.
     */
    ItemStack getWest();
}