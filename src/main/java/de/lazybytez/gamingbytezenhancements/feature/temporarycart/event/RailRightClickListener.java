package de.lazybytez.gamingbytezenhancements.feature.temporarycart.event;

import de.lazybytez.gamingbytezenhancements.feature.temporarycart.TemporaryCartManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Rail;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Event that handles spawning a temporary cart when a player right-clicks a rail.
 */
public class RailRightClickListener implements Listener {
    private final TemporaryCartManager temporaryCartManager;

    private final List<Material> minecartTypes = new ArrayList<>(List.of(
            Material.MINECART,
            Material.CHEST_MINECART,
            Material.FURNACE_MINECART,
            Material.HOPPER_MINECART,
            Material.TNT_MINECART
    ));

    public RailRightClickListener(TemporaryCartManager temporaryCartManager) {
        this.temporaryCartManager = temporaryCartManager;
    }

    @EventHandler
    public void onRailRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null || !(block.getBlockData() instanceof Rail)) {
            return;
        }

        Player p = event.getPlayer();
        ItemStack item = event.getItem();
        if (item != null && this.minecartTypes.contains(item.getType())) {
            return;
        }

        event.setCancelled(true);
        this.temporaryCartManager.spawnTemporaryCart(p, block.getLocation());
    }

}
