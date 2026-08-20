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
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastShape;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.ExcavationChargeAuditLog;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.excavationcharge.ExcavationChargeManager;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.excavationcharge.PlacedExcavationCharge;
import java.util.Objects;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Listener that places an Excavation Charge held in hand as an end crystal in the world.
 * <p>
 * Placement is the non-sneaking right click on a block. Sneaking is reserved for
 * {@link CycleExcavationChargeShapeListener}, so this listener returns as soon as the player sneaks and
 * the two never both act on one click.
 * <p>
 * The event is cancelled because vanilla only allows an end crystal on obsidian and bedrock, and a
 * Excavation Charge is meant to sit on any block.
 * <p>
 * The blast direction is written as one of the six cardinal {@link BlockFace} constants, never a
 * diagonal and never {@link BlockFace#SELF}. A reader can therefore map it straight onto an
 * axis-aligned unit vector, which is what the tunnel geometry requires.
 */
public class PlaceExcavationChargeListener implements Listener {

    private static final List<BlockFace> COMPASS_FACES =
            List.of(BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST);
    private static final float DEGREES_PER_COMPASS_FACE = 90.0f;
    private static final float VERTICAL_PITCH_THRESHOLD = 45.0f;
    private static final float FULL_TURN_DEGREES = 360.0f;

    private final MythicAltarFeature mythicAltarFeature;
    private final ExcavationChargeAuditLog auditLog;

    public PlaceExcavationChargeListener(
            MythicAltarFeature mythicAltarFeature,
            ExcavationChargeAuditLog auditLog
    ) {
        this.mythicAltarFeature = Objects.requireNonNull(
                mythicAltarFeature, "mythicAltarFeature must not be null");
        this.auditLog = Objects.requireNonNull(auditLog, "auditLog must not be null");
    }

    /**
     * Place the Excavation Charge held in the main hand as an end crystal above the clicked block.
     * <p>
     * The charge's shape, level and the placing player's facing are copied onto the spawned
     * entity, so the item may be consumed without losing the state it carried.
     *
     * @param event The player interact event
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlaceExcavationCharge(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        if (player.isSneaking()) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        ItemStack item = event.getItem();
        if (clickedBlock == null || item == null || item.getType() != Material.END_CRYSTAL) {
            return;
        }

        ExcavationChargeManager excavationChargeManager = this.mythicAltarFeature
                .getCustomItemManagerRegistry()
                .getCustomItemManager(ExcavationChargeManager.class);

        if (!excavationChargeManager.isCustomItem(item)) {
            return;
        }

        event.setCancelled(true);

        BlastShape shape = excavationChargeManager.getShape(item);
        int level = excavationChargeManager.getLevel(item);
        BlockFace facing = PlaceExcavationChargeListener.toCardinalFace(player.getYaw(), player.getPitch());

        Location spawnLocation = this.spawnExcavationCharge(clickedBlock, shape, level, facing);
        this.consumeExcavationCharge(item, player);
        this.auditLog.placed(player, shape, level, facing, spawnLocation);
    }

    /**
     * Spawn the placed Excavation Charge as an end crystal centred above the clicked block.
     *
     * @param clickedBlock The block the player clicked
     * @param shape        The blast shape carried by the item
     * @param level        The blast level carried by the item
     * @param facing       The cardinal blast direction to store
     * @return The location the charge was spawned at
     */
    private Location spawnExcavationCharge(Block clickedBlock, BlastShape shape, int level, BlockFace facing) {
        Location spawnLocation = clickedBlock.getLocation().add(0.5, 1.0, 0.5);

        clickedBlock.getWorld().spawn(
                spawnLocation,
                EnderCrystal.class,
                CreatureSpawnEvent.SpawnReason.CUSTOM,
                crystal -> {
                    crystal.setShowingBottom(false);
                    this.writeChargeState(crystal, shape, level, facing);
                }
        );

        clickedBlock.getWorld().playSound(spawnLocation, Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 1.0f, 1.2f);

        return spawnLocation;
    }

    /**
     * Write the marker, shape, level and facing of a placed Excavation Charge onto the spawned entity.
     *
     * @param crystal The spawned end crystal
     * @param shape   The blast shape carried by the item
     * @param level   The blast level carried by the item
     * @param facing  The cardinal blast direction to store
     */
    private void writeChargeState(EnderCrystal crystal, BlastShape shape, int level, BlockFace facing) {
        PlacedExcavationCharge.stamp(
                PlacedExcavationCharge.Keys.of(this.mythicAltarFeature.getPlugin()),
                crystal,
                shape,
                level,
                facing
        );
    }

    /**
     * Remove exactly one Excavation Charge from the stack the player placed from.
     *
     * @param item   The Excavation Charge stack held in the main hand
     * @param player The player who placed the charge
     */
    private void consumeExcavationCharge(ItemStack item, Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);

            return;
        }

        player.getInventory().setItem(EquipmentSlot.HAND, ItemStack.empty());
    }

    /**
     * Normalise a player's view angles to the cardinal {@link BlockFace} the blast travels along.
     * <p>
     * A pitch steeper than {@value #VERTICAL_PITCH_THRESHOLD} degrees resolves to
     * {@link BlockFace#UP} or {@link BlockFace#DOWN}, anything flatter snaps the yaw to the nearest
     * compass face. The result is therefore always axis-aligned, which is the contract a reader
     * relies on when it turns the face into a unit vector.
     *
     * @param yaw   The player's yaw in degrees, any multiple of a full turn is tolerated
     * @param pitch The player's pitch in degrees, negative when looking upwards
     * @return One of the six cardinal block faces
     */
    static BlockFace toCardinalFace(float yaw, float pitch) {
        if (pitch <= -PlaceExcavationChargeListener.VERTICAL_PITCH_THRESHOLD) {
            return BlockFace.UP;
        }

        if (pitch >= PlaceExcavationChargeListener.VERTICAL_PITCH_THRESHOLD) {
            return BlockFace.DOWN;
        }

        float turnedYaw = yaw % PlaceExcavationChargeListener.FULL_TURN_DEGREES;
        float normalisedYaw = (turnedYaw + PlaceExcavationChargeListener.FULL_TURN_DEGREES)
                % PlaceExcavationChargeListener.FULL_TURN_DEGREES;
        int faceIndex = Math.round(normalisedYaw / PlaceExcavationChargeListener.DEGREES_PER_COMPASS_FACE)
                % PlaceExcavationChargeListener.COMPASS_FACES.size();

        return PlaceExcavationChargeListener.COMPASS_FACES.get(faceIndex);
    }
}
