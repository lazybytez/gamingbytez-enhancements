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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.excavationcharge;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastGeometry;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastLevel;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastShape;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastVector;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * An Excavation Charge standing in the world as an end crystal.
 * <p>
 * The item form and the placed form are two faces of one custom item, so this class sits beside
 * {@link ExcavationChargeManager} and shares its shape and level key names. It is the only place
 * that knows how a placed charge stores its state, which keeps a listener, the detonator and the
 * gravity task from each carrying their own copy of that knowledge.
 * <p>
 * Every read applies the fallback a charge written by an earlier build depends on, so a charge
 * placed before a change keeps behaving the way its owner placed it.
 */
public final class PlacedExcavationCharge {

    /**
     * Marks an end crystal as a placed Excavation Charge, stored as {@link PersistentDataType#BOOLEAN}.
     * An end crystal without this marker belongs to vanilla or to another plugin.
     */
    public static final String PDC_KEY_PLACED_EXCAVATION_CHARGE = "gamingbytez-excavation-charge-placed";

    /**
     * The blast direction of a placed Excavation Charge, stored as the {@link BlockFace} name in a
     * {@link PersistentDataType#STRING}. Always one of the six cardinal faces.
     */
    public static final String PDC_KEY_FACING = "gamingbytez-excavation-charge-facing";

    private static final BlockFace DEFAULT_FACING = BlockFace.NORTH;
    private static final Set<BlockFace> CARDINAL_FACES = Set.of(
            BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
            BlockFace.WEST, BlockFace.UP, BlockFace.DOWN);

    private final EnderCrystal crystal;
    private final Keys keys;

    private PlacedExcavationCharge(EnderCrystal crystal, Keys keys) {
        this.crystal = crystal;
        this.keys = keys;
    }

    /**
     * The namespaced keys a placed Excavation Charge stores its state under.
     * <p>
     * A key runs a validation pass in its constructor, so a caller sweeping every crystal around it
     * every few ticks builds this once and hands the same instance to every read.
     *
     * @param placedKey The key marking an end crystal as a placed Excavation Charge
     * @param shapeKey  The key holding the blast shape, shared with the item form
     * @param levelKey  The key holding the blast level, shared with the item form
     * @param facingKey The key holding the cardinal blast direction
     */
    public record Keys(
            NamespacedKey placedKey,
            NamespacedKey shapeKey,
            NamespacedKey levelKey,
            NamespacedKey facingKey
    ) {
        /**
         * Builds the key set inside the given plugin's namespace.
         *
         * @param plugin The plugin owning the namespace
         * @return The keys a placed Excavation Charge is read and written through
         */
        public static Keys of(Plugin plugin) {
            Objects.requireNonNull(plugin, "plugin must not be null");

            return new Keys(
                    new NamespacedKey(plugin, PlacedExcavationCharge.PDC_KEY_PLACED_EXCAVATION_CHARGE),
                    new NamespacedKey(plugin, ExcavationChargeManager.PDC_KEY_SHAPE),
                    new NamespacedKey(plugin, ExcavationChargeManager.PDC_KEY_LEVEL),
                    new NamespacedKey(plugin, PlacedExcavationCharge.PDC_KEY_FACING)
            );
        }
    }

    /**
     * Wraps the given end crystal when it is an Excavation Charge placed by this plugin.
     * <p>
     * A vanilla end crystal and one owned by another plugin are both an ordinary absent result
     * rather than a failure, because a caller reaching this method is sweeping every crystal in
     * range and cannot know in advance which of them are charges.
     *
     * @param keys    The keys a placed charge stores its state under
     * @param crystal The end crystal to inspect
     * @return The wrapped charge, or empty when the crystal carries no placed charge marker
     */
    public static Optional<PlacedExcavationCharge> of(Keys keys, EnderCrystal crystal) {
        Objects.requireNonNull(keys, "keys must not be null");
        Objects.requireNonNull(crystal, "crystal must not be null");

        boolean placed = crystal.getPersistentDataContainer()
                .getOrDefault(keys.placedKey(), PersistentDataType.BOOLEAN, false);

        if (!placed) {
            return Optional.empty();
        }

        return Optional.of(new PlacedExcavationCharge(crystal, keys));
    }

    /**
     * Writes the marker and the full state of a freshly placed Excavation Charge onto its entity.
     * <p>
     * The four values are stamped together because a crystal carrying the marker without the rest
     * would read back as a charge every consumer then has to guess the state of.
     *
     * @param keys    The keys a placed charge stores its state under
     * @param crystal The end crystal the charge was spawned as
     * @param shape   The blast shape carried by the item
     * @param level   The blast level carried by the item
     * @param facing  The cardinal blast direction to store
     */
    public static void stamp(
            Keys keys,
            EnderCrystal crystal,
            BlastShape shape,
            int level,
            BlockFace facing
    ) {
        Objects.requireNonNull(keys, "keys must not be null");
        Objects.requireNonNull(crystal, "crystal must not be null");

        PersistentDataContainer container = crystal.getPersistentDataContainer();

        container.set(keys.placedKey(), PersistentDataType.BOOLEAN, true);
        container.set(keys.shapeKey(), PersistentDataType.STRING, ExcavationChargeManager.encodeShape(shape));
        container.set(keys.levelKey(), PersistentDataType.INTEGER, level);
        container.set(keys.facingKey(), PersistentDataType.STRING, facing.name());
    }

    /**
     * Reads the blast shape the charge carves with.
     *
     * @return The stored shape, defaulting the way the item form does
     */
    public BlastShape shape() {
        return BlastShape.decode(
                this.container().get(this.keys.shapeKey(), PersistentDataType.STRING));
    }

    /**
     * Reads the blast level the charge goes off at.
     *
     * @return The stored level, clamped into the valid range
     */
    public BlastLevel level() {
        return BlastLevel.of(ExcavationChargeManager.decodeLevel(
                this.container().get(this.keys.levelKey(), PersistentDataType.INTEGER)));
    }

    /**
     * Reads the cardinal direction the blast travels along.
     *
     * @return One of the six cardinal block faces
     */
    public BlockFace facing() {
        return PlacedExcavationCharge.decodeFacing(
                this.container().get(this.keys.facingKey(), PersistentDataType.STRING));
    }

    /**
     * Builds the volume the charge would carve from the state it carries.
     *
     * @return The geometry described by the charge's shape, level and facing
     */
    public BlastGeometry geometry() {
        BlockFace facing = this.facing();

        return this.shape().geometry(
                this.level().getDimensions(),
                new BlastVector(facing.getModX(), facing.getModY(), facing.getModZ()));
    }

    private PersistentDataContainer container() {
        return this.crystal.getPersistentDataContainer();
    }

    /**
     * Decodes the blast direction stored on a placed Excavation Charge.
     * <p>
     * Anything that is not one of the six cardinal faces reads back as {@link BlockFace#NORTH}. A
     * charge placed before the facing was written carries none at all, and a diagonal face would
     * make the tunnel geometry reject the direction, so the value is narrowed here rather than
     * passed on.
     *
     * @param rawFacing The raw face name read from the entity, may be null
     * @return One of the six cardinal block faces
     */
    static BlockFace decodeFacing(String rawFacing) {
        if (rawFacing == null) {
            return PlacedExcavationCharge.DEFAULT_FACING;
        }

        BlockFace facing;
        try {
            facing = BlockFace.valueOf(rawFacing);
        } catch (IllegalArgumentException e) {
            return PlacedExcavationCharge.DEFAULT_FACING;
        }

        if (!PlacedExcavationCharge.CARDINAL_FACES.contains(facing)) {
            return PlacedExcavationCharge.DEFAULT_FACING;
        }

        return facing;
    }
}
