package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.safarinet;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.safarinet.SafariNetManager;
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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
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

        Location spawnLocation = player.getEyeLocation().add(player.getLocation().getDirection().multiply(2));
        spawnLocation.setY(player.getLocation().getY());

        org.bukkit.entity.EntityType entityType = safariNetManager.getEntityType(item);
        Entity spawnedEntity = safariNetManager.spawnEntityFromNet(item, spawnLocation);

        if (spawnedEntity == null) {
            mythicAltarFeature.getPlugin().getLogger().warning("The player "
                    + player.getName()
                    + " failed to release a "
                    + entityType
                    + " from Safari Net at "
                    + spawnLocation);
            player.sendMessage(net.kyori.adventure.text.Component.text("Failed to release entity from Safari Net!", net.kyori.adventure.text.format.NamedTextColor.RED));
            return;
        }

        mythicAltarFeature.getPlugin().getLogger().info("The player "
                + player.getName()
                + " released a "
                + entityType
                + " from Safari Net at "
                + spawnLocation);

        player.getWorld().playSound(spawnLocation, Sound.ENTITY_CHICKEN_EGG, 1.0f, 0.8f);
        player.getWorld().spawnParticle(
                Particle.FIREWORK,
                spawnLocation.clone().add(0, 1, 0),
                20,
                0.5, 0.5, 0.5,
                0.1
        );

        safariNetManager.clearEntity(item);

        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItem(event.getHand(), new ItemStack(Material.AIR));
        }
    }
}
