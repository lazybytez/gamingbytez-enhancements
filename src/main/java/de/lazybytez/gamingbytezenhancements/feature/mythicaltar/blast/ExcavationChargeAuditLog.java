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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast;

import de.lazybytez.gamingbytezenhancements.lib.gameplay.blast.BlastShape;
import java.util.List;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Writes the audit trail of the Excavation Charge to the server log.
 * <p>
 * Every line answers a question an operator asks after the fact: who placed or collected a charge
 * where, who or what set it off, which blast a chained charge belongs to, and which region a
 * detonation removed. The removed region is logged as the corner to corner bounding box of the
 * blocks actually carved, so the affected area can be judged, and rolled back with an external
 * tool, without replaying the blast.
 * <p>
 * All lines share the {@code Excavation Charge:} prefix, so the whole trail of an incident is one
 * grep away.
 */
public final class ExcavationChargeAuditLog {

    private static final String PREFIX = "Excavation Charge: ";

    private final Plugin plugin;

    /**
     * Creates the audit log writing through the given plugin's logger.
     *
     * @param plugin The plugin whose logger carries the audit lines
     */
    public ExcavationChargeAuditLog(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null");
    }

    /**
     * Records that a player placed a charge.
     *
     * @param player   The player who placed the charge
     * @param shape    The blast shape the charge carries
     * @param level    The blast level the charge carries
     * @param facing   The cardinal blast direction the charge was placed with
     * @param location The location the charge stands at
     */
    public void placed(Player player, BlastShape shape, int level, BlockFace facing, Location location) {
        this.write("%s placed a level %d %s charge at %s facing %s".formatted(
                player.getName(),
                level,
                shape.getDisplayName(),
                ExcavationChargeAuditLog.describe(location),
                facing.name()));
    }

    /**
     * Records that a player collected a placed charge back into their inventory.
     *
     * @param player   The player who collected the charge
     * @param location The location the charge was picked up from
     */
    public void collected(Player player, Location location) {
        this.write("%s collected the charge at %s".formatted(
                player.getName(), ExcavationChargeAuditLog.describe(location)));
    }

    /**
     * Records that a player set a placed charge off.
     *
     * @param player   The player who ignited the charge
     * @param location The location of the ignited charge
     */
    public void ignitedByPlayer(Player player, Location location) {
        this.write("%s ignited the charge at %s".formatted(
                player.getName(), ExcavationChargeAuditLog.describe(location)));
    }

    /**
     * Records that something other than a player's hit set a placed charge off.
     *
     * @param cause    A short description of the trigger, such as a damage cause name
     * @param location The location of the ignited charge
     */
    public void ignitedBy(String cause, Location location) {
        this.write("%s ignited the charge at %s".formatted(
                cause, ExcavationChargeAuditLog.describe(location)));
    }

    /**
     * Records that a rising redstone signal set a placed charge off.
     *
     * @param signalLocation The location of the component whose signal rose
     * @param chargeLocation The location of the ignited charge
     */
    public void ignitedByRedstone(Location signalLocation, Location chargeLocation) {
        this.write("a redstone signal at %s ignited the charge at %s".formatted(
                ExcavationChargeAuditLog.describe(signalLocation),
                ExcavationChargeAuditLog.describe(chargeLocation)));
    }

    /**
     * Records that a detonation woke a neighbouring charge into the cascade.
     *
     * @param detonationPoint The location of the blast that woke the charge
     * @param chargeLocation  The location of the woken charge
     */
    public void chainIgnited(Location detonationPoint, Location chargeLocation) {
        this.write("the blast at %s chain ignited the charge at %s".formatted(
                ExcavationChargeAuditLog.describe(detonationPoint),
                ExcavationChargeAuditLog.describe(chargeLocation)));
    }

    /**
     * Records a detonation together with the region its blast removes.
     *
     * @param shape           The blast shape of the detonated charge
     * @param level           The blast level of the detonated charge
     * @param detonationPoint The location the charge went off at
     * @param carved          The blocks the blast removes, after listener vetoes
     */
    public void detonated(BlastShape shape, BlastLevel level, Location detonationPoint, List<Block> carved) {
        this.write("a level %d %s charge detonated at %s, carving %d blocks %s".formatted(
                level.getLevel(),
                shape.getDisplayName(),
                ExcavationChargeAuditLog.describe(detonationPoint),
                carved.size(),
                ExcavationChargeAuditLog.describeBounds(carved)));
    }

    private void write(String line) {
        this.plugin.getLogger().info(ExcavationChargeAuditLog.PREFIX + line);
    }

    /**
     * Renders the corner to corner bounding box of the carved blocks.
     *
     * @param carved The blocks the blast removes
     * @return The bounds as a from/to pair, or a fixed phrase when nothing is carved
     */
    private static String describeBounds(List<Block> carved) {
        if (carved.isEmpty()) {
            return "in an empty region";
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (Block block : carved) {
            minX = Math.min(minX, block.getX());
            minY = Math.min(minY, block.getY());
            minZ = Math.min(minZ, block.getZ());
            maxX = Math.max(maxX, block.getX());
            maxY = Math.max(maxY, block.getY());
            maxZ = Math.max(maxZ, block.getZ());
        }

        return "between (%d, %d, %d) and (%d, %d, %d)".formatted(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static String describe(Location location) {
        return "%s (%d, %d, %d)".formatted(
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ());
    }
}
