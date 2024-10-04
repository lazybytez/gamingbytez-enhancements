package de.lazybytez.gamingbytezenhancements.feature.temporarycart;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages spawning and destroying temporary carts.
 */
public class TemporaryCartManager {

    private final EnhancementsPlugin plugin;

    private final ConcurrentHashMap<String, Long> coolDownMap = new ConcurrentHashMap<>();

    public TemporaryCartManager(EnhancementsPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Spawns a temporary cart at the given location.
     */
    public void spawnTemporaryCart(Player p, Location location) {
        if (this.isOnCoolDown(p)) {
            return;
        }

        this.setOnCoolDown(p);
        Vehicle minecart = p.getWorld().spawn(location, Minecart.class);

        this.tagMinecartAsTemporary(minecart);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (minecart.isValid() && p.isValid()) {
                minecart.addPassenger(p);
            }

        }, 2L);

        this.plugin.getLogger().info("Spawned temporary cart at " + location + " for " + p.getName());
    }

    /**
     * Sets the player on cool down.
     *
     * @param p The player to set on cool down.
     */
    private void setOnCoolDown(Player p) {
        String uuid = p.getUniqueId().toString();

        this.coolDownMap.put(uuid, System.currentTimeMillis());
    }

    /**
     * Returns whether the player is on cool down.
     *
     * @param p The player to check.
     * @return Whether the player is on cool down.
     */
    private boolean isOnCoolDown(Player p) {
        String uuid = p.getUniqueId().toString();

        if (this.coolDownMap.containsKey(uuid)) {
            long lastSpawn = this.coolDownMap.get(uuid);
            long now = System.currentTimeMillis();

            return now - lastSpawn < 1000;
        }

        return false;
    }

    /**
     * Returns whether the player is on cool down.
     *
     * @param p The player to remove from the cool down list.
     */
    public void removeCoolDown(Player p) {
        String uuid = p.getUniqueId().toString();

        if (this.coolDownMap.containsKey(uuid)) {
            this.coolDownMap.remove(p.getUniqueId().toString());
            this.plugin.getLogger().info("Removed " + p.getName() + " from temporary cart cooldown list.");
        }
    }

    /**
     * Returns whether the given minecart is a temporary cart.
     *
     * @param minecart The minecart to check.
     * @return Whether the given minecart is a temporary cart.
     */
    public boolean isTemporaryCart(Minecart minecart) {
        return minecart.getPersistentDataContainer().has(this.getTemporaryCartKey());
    }

    /**
     * Removes the temporary cart of the given player.
     * <p>
     * This method only removes temporary carts that were spawned by this plugin.
     * </p>
     *
     * @param p The player to remove the temporary cart from.
     */
    public void removeTemporaryCart(Player p, Minecart minecart, boolean synchronous) {
        if (!isTemporaryCart(minecart)) {
            return;
        }

        if (null != p && minecart.getPassengers().contains(p)) {
            p.leaveVehicle();

            this.plugin.getLogger().info("Removed temporary cart for "
                    + p.getName()
                    + " at "
                    + minecart.getLocation());
        }

        if (synchronous) {
            minecart.remove();
            return;
        }

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (minecart.isDead() || !minecart.isValid()) {
                return;
            }

            minecart.remove();
        }, 2L);
    }

    /**
     * Tag a Minecart using a boolean PDC value as a temporary cart.
     *
     * @param vehicle the vehicle to tag
     */
    private void tagMinecartAsTemporary(Vehicle vehicle) {
        vehicle.getPersistentDataContainer().set(
                this.getTemporaryCartKey(),
                PersistentDataType.BOOLEAN,
                true
        );
    }

    /**
     * Get the {@see NamespacedKey} used for temporary cart tagging.
     *
     * @return the namespaced key used for temporary cart tagging
     */
    private NamespacedKey getTemporaryCartKey() {
        return new NamespacedKey(this.plugin, "gamingbytez-temporary-cart");
    }
}
