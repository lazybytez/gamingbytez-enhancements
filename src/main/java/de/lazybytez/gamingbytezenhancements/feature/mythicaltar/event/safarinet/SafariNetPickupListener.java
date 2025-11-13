package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.safarinet;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.safarinet.SafariNetManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener for picking up Safari Nets.
 */
public class SafariNetPickupListener implements Listener {

    private final MythicAltarFeature mythicAltarFeature;

    public SafariNetPickupListener(MythicAltarFeature mythicAltarFeature) {
        this.mythicAltarFeature = mythicAltarFeature;
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack item = event.getItem().getItemStack();
        SafariNetManager safariNetManager = this.mythicAltarFeature
                .getCustomItemManagerRegistry()
                .getCustomItemManager(SafariNetManager.class);

        if (!safariNetManager.isCustomItem(item)) {
            return;
        }

        if (safariNetManager.hasEntity(item)) {
            EntityType entityType = safariNetManager.getEntityType(item);
            mythicAltarFeature.getPlugin().getLogger().info("A player picked up a Safari Net containing a "
                    + entityType
                    + " at "
                    + player.getLocation());
        } else {
            mythicAltarFeature.getPlugin().getLogger().info("A player picked up an empty Safari Net at "
                    + player.getLocation());
        }
    }
}
