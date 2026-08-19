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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.excavationcharge;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastLevel;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastShape;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.excavationcharge.ExcavationChargeManager;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Map;

/**
 * Listener that picks a placed Excavation Charge back up.
 * <p>
 * Only an end crystal carrying the marker written by {@link PlaceExcavationChargeListener} is handled,
 * so vanilla end crystals and entities owned by other plugins are left untouched.
 */
public class CollectExcavationChargeListener implements Listener {

    private final MythicAltarFeature mythicAltarFeature;

    public CollectExcavationChargeListener(MythicAltarFeature mythicAltarFeature) {
        this.mythicAltarFeature = Objects.requireNonNull(
                mythicAltarFeature, "mythicAltarFeature must not be null");
    }

    /**
     * Return a placed Excavation Charge to the player who right clicks it.
     * <p>
     * The returned item carries the shape and level the entity held, so a charge can be repositioned
     * without losing the state it was placed with.
     *
     * @param event The player interact entity event
     */
    @EventHandler(ignoreCancelled = true)
    public void onCollectExcavationCharge(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (!(event.getRightClicked() instanceof EnderCrystal crystal)) {
            return;
        }

        Plugin plugin = this.mythicAltarFeature.getPlugin();
        PersistentDataContainer container = crystal.getPersistentDataContainer();

        if (!container.getOrDefault(PlaceExcavationChargeListener.placedKey(plugin), PersistentDataType.BOOLEAN, false)) {
            return;
        }

        event.setCancelled(true);

        ItemStack excavationCharge = this.buildExcavationCharge(container, plugin);
        Location crystalLocation = crystal.getLocation();
        crystal.remove();

        this.giveExcavationCharge(event.getPlayer(), excavationCharge, crystalLocation);
    }

    /**
     * Build the item returned to the player from the state stored on the entity.
     *
     * @param container The persistent data container of the placed charge
     * @param plugin    The plugin owning the namespace
     * @return The Excavation Charge item carrying the entity's shape and level
     */
    private ItemStack buildExcavationCharge(PersistentDataContainer container, Plugin plugin) {
        ExcavationChargeManager excavationChargeManager = this.mythicAltarFeature
                .getCustomItemManagerRegistry()
                .getCustomItemManager(ExcavationChargeManager.class);

        ItemStack excavationCharge = excavationChargeManager.createCustomItem();

        excavationChargeManager.setShape(excavationCharge, BlastShape.decode(
                container.get(PlaceExcavationChargeListener.shapeKey(plugin), PersistentDataType.STRING)));
        excavationChargeManager.setLevel(excavationCharge, container.getOrDefault(
                PlaceExcavationChargeListener.levelKey(plugin), PersistentDataType.INTEGER, BlastLevel.MIN_LEVEL));

        return excavationCharge;
    }

    /**
     * Hand the charge to the player, dropping it where the entity stood when the inventory is full.
     *
     * @param player          The player collecting the charge
     * @param excavationCharge      The Excavation Charge item to hand over
     * @param crystalLocation The location the charge was picked up from
     */
    private void giveExcavationCharge(Player player, ItemStack excavationCharge, Location crystalLocation) {
        Map<Integer, ItemStack> rejected = player.getInventory().addItem(excavationCharge);

        for (ItemStack leftover : rejected.values()) {
            crystalLocation.getWorld().dropItemNaturally(crystalLocation, leftover);
        }

        player.playSound(crystalLocation, Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.2f);
    }
}
