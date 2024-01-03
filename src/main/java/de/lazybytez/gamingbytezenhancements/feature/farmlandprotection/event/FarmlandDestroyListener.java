package de.lazybytez.gamingbytezenhancements.feature.farmlandprotection.event;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class FarmlandDestroyListener implements Listener {
    @EventHandler
    public void onPlayerFarmlandDestroy(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.PHYSICAL)) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) {
            return;
        }

        if (!clickedBlock.getType().equals(Material.FARMLAND)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityFarmlandDestroy(EntityInteractEvent event) {
        if (!event.getBlock().getType().equals(Material.FARMLAND)) {
            return;
        }

        event.setCancelled(true);
    }
}
