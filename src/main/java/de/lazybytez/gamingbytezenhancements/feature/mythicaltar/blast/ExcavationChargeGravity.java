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

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.excavationcharge.PlaceExcavationChargeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.scheduler.BukkitTask;

/**
 * Lets placed Excavation Charges fall when nothing supports them.
 * <p>
 * A vanilla end crystal ignores gravity: its tick applies no motion at all, so the entity gravity
 * flag does nothing and the fall has to be applied by hand. One repeating task watches every
 * placed charge in the loaded worlds, which covers a charge placed over a hole, a support block a
 * player mines away, and the ground a neighbouring blast carves out from under a charge.
 */
public final class ExcavationChargeGravity {

    private static final long CHECK_PERIOD_TICKS = 2L;

    /**
     * The velocity gained per check, tuned to read like a heavy object rather than a feather.
     */
    private static final double GRAVITY_PER_CHECK = 0.16;

    private static final double MAX_FALL_SPEED = 1.6;

    private final EnhancementsPlugin plugin;
    private final Map<UUID, Double> fallSpeeds;

    private BukkitTask task;

    /**
     * Creates the gravity watcher for the given plugin.
     *
     * @param plugin The plugin owning the repeating task.
     */
    public ExcavationChargeGravity(EnhancementsPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin must not be null");
        this.fallSpeeds = new HashMap<>();
    }

    /**
     * Starts watching the placed charges.
     */
    public void start() {
        if (this.task != null) {
            return;
        }

        this.task = this.plugin.getServer().getScheduler().runTaskTimer(
                this.plugin,
                this::tick,
                ExcavationChargeGravity.CHECK_PERIOD_TICKS,
                ExcavationChargeGravity.CHECK_PERIOD_TICKS
        );
    }

    /**
     * Stops watching. Charges mid fall simply stop where they are.
     */
    public void stop() {
        if (this.task == null) {
            return;
        }

        this.task.cancel();
        this.task = null;
        this.fallSpeeds.clear();
    }

    private void tick() {
        Set<UUID> loadedCharges = new HashSet<>();

        for (World world : this.plugin.getServer().getWorlds()) {
            for (EnderCrystal crystal : world.getEntitiesByClass(EnderCrystal.class)) {
                if (!PlaceExcavationChargeListener.isPlacedCharge(this.plugin, crystal)) {
                    continue;
                }

                loadedCharges.add(crystal.getUniqueId());
                this.applyGravity(crystal);
            }
        }

        // A charge removed mid fall never reaches land(), so its speed entry is swept here.
        this.fallSpeeds.keySet().retainAll(loadedCharges);
    }

    /**
     * Returns how many charges are currently tracked as falling.
     *
     * @return The number of tracked fall speeds.
     */
    int trackedFallingCharges() {
        return this.fallSpeeds.size();
    }

    private void applyGravity(EnderCrystal crystal) {
        Location location = crystal.getLocation();
        World world = crystal.getWorld();

        if (location.getBlockY() - 1 <= world.getMinHeight()) {
            this.fallSpeeds.remove(crystal.getUniqueId());

            return;
        }

        Block support = world.getBlockAt(location.getBlockX(), location.getBlockY() - 1, location.getBlockZ());

        if (!support.isPassable()) {
            this.land(crystal);

            return;
        }

        double speed = Math.min(
                ExcavationChargeGravity.MAX_FALL_SPEED,
                this.fallSpeeds.merge(crystal.getUniqueId(), ExcavationChargeGravity.GRAVITY_PER_CHECK, Double::sum)
        );

        Location fallen = location.clone();
        fallen.setY(this.landingHeight(world, location, speed));

        crystal.teleport(fallen);
    }

    /**
     * Returns the height the charge reaches this check, stopping on the first solid block.
     *
     * @param world    The world the charge falls in.
     * @param location The current location of the charge.
     * @param speed    The distance the charge falls this check.
     * @return The new y coordinate, clamped onto the first support in the way.
     */
    private double landingHeight(World world, Location location, double speed) {
        double target = location.getY() - speed;
        int x = location.getBlockX();
        int z = location.getBlockZ();

        for (int y = location.getBlockY() - 1; y >= (int) Math.floor(target) && y > world.getMinHeight(); y--) {
            if (!world.getBlockAt(x, y, z).isPassable()) {
                return y + 1.0;
            }
        }

        return Math.max(target, world.getMinHeight() + 1.0);
    }

    private void land(EnderCrystal crystal) {
        if (this.fallSpeeds.remove(crystal.getUniqueId()) == null) {
            return;
        }

        Location location = crystal.getLocation();
        location.setY(location.getBlockY());
        crystal.teleport(location);

        crystal.getWorld().playSound(location, "block.deepslate.fall", 1.5f, 0.7f);
    }
}
