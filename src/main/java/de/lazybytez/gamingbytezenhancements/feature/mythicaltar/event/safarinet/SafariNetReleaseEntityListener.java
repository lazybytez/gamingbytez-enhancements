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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.safarinet;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.safarinet.SafariNetManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener for releasing entities from Safari Nets.
 */
public class SafariNetReleaseEntityListener implements Listener {

    private final MythicAltarFeature mythicAltarFeature;

    public SafariNetReleaseEntityListener(MythicAltarFeature mythicAltarFeature) {
        this.mythicAltarFeature = mythicAltarFeature;
    }

    /**
     * Handle player right-click interaction with Safari Nets to release entities.
     * <p>
     * Validates the item is a Safari Net containing an entity, spawns the entity,
     * and plays visual/sound effects.
     *
     * @param event The player interact event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (!this.isRightClickAction(event.getAction())) {
            return;
        }

        if (item == null) {
            return;
        }

        SafariNetManager safariNetManager = this.mythicAltarFeature
                .getCustomItemManagerRegistry()
                .getCustomItemManager(SafariNetManager.class);

        if (!safariNetManager.isCustomItem(item)) {
            return;
        }

        if (!safariNetManager.hasEntity(item)) {
            return;
        }

        event.setCancelled(true);

        Location spawnLocation = this.calculateSpawnLocation(player);
        org.bukkit.entity.EntityType entityType = safariNetManager.getEntityType(item);
        Entity spawnedEntity = safariNetManager.spawnEntityFromNet(item, spawnLocation);

        if (spawnedEntity == null) {
            this.handleReleaseFailure(player, entityType, spawnLocation);
            return;
        }

        this.handleReleaseSuccess(player, entityType, spawnLocation);
        this.showReleaseEffects(spawnLocation, player);

        safariNetManager.clearEntity(item);
        this.consumeSafariNet(item, player, event);
    }

    /**
     * Check if the action is a right-click action.
     *
     * @param action The player action
     * @return True if the action is a right-click
     */
    private boolean isRightClickAction(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }

    /**
     * Calculate the spawn location for the entity.
     * <p>
     * Spawns the entity 2 blocks in front of the player at ground level.
     *
     * @param player The player releasing the entity
     * @return The calculated spawn location
     */
    private Location calculateSpawnLocation(Player player) {
        Location spawnLocation = player.getEyeLocation().add(player.getLocation().getDirection().multiply(2));
        spawnLocation.setY(player.getLocation().getY());
        return spawnLocation;
    }

    /**
     * Handle successful entity release with logging.
     *
     * @param player        The player who released the entity
     * @param entityType    The type of entity released
     * @param spawnLocation The location where the entity was spawned
     */
    private void handleReleaseSuccess(Player player, org.bukkit.entity.EntityType entityType, Location spawnLocation) {
        this.mythicAltarFeature.getPlugin().getLogger().info("The player "
                + player.getName()
                + " released a "
                + entityType
                + " from Safari Net at "
                + spawnLocation);
    }

    /**
     * Handle failed entity release with logging and player notification.
     *
     * @param player        The player who attempted to release the entity
     * @param entityType    The type of entity that failed to spawn
     * @param spawnLocation The location where spawning was attempted
     */
    private void handleReleaseFailure(Player player, org.bukkit.entity.EntityType entityType, Location spawnLocation) {
        this.mythicAltarFeature.getPlugin().getLogger().warning("The player "
                + player.getName()
                + " failed to release a "
                + entityType
                + " from Safari Net at "
                + spawnLocation);

        player.sendMessage(Component.text("Failed to release entity from Safari Net!", NamedTextColor.RED));
    }

    /**
     * Show visual and sound effects for entity release.
     *
     * @param spawnLocation The location to show effects at
     * @param player        The player who released the entity
     */
    private void showReleaseEffects(Location spawnLocation, Player player) {
        player.getWorld().playSound(spawnLocation, Sound.ENTITY_CHICKEN_EGG, 1.0f, 0.8f);
        player.getWorld().spawnParticle(
                Particle.FIREWORK,
                spawnLocation.clone().add(0, 1, 0),
                20,
                0.5, 0.5, 0.5,
                0.1
        );
    }

    /**
     * Consume one Safari Net from the player's inventory.
     *
     * @param item   The Safari Net item
     * @param player The player whose inventory to update
     * @param event  The player interact event
     */
    private void consumeSafariNet(ItemStack item, Player player, PlayerInteractEvent event) {
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItem(event.getHand(), new ItemStack(Material.AIR));
        }
    }
}
