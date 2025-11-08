package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.magicxpbottle;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle.EssenceOfSpawnerManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Event listener to add "Essence of Spawner" as drops when a spawner is broken.
 */
public class DropEssenceOfSpawnerOnSpawnerBreakListener implements Listener {

    private final MythicAltarFeature mythicAltarFeature;

    public DropEssenceOfSpawnerOnSpawnerBreakListener(MythicAltarFeature mythicAltarFeature) {
        this.mythicAltarFeature = mythicAltarFeature;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getBlock().getType().equals(Material.SPAWNER)) {
            return;
        }

        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }

        ItemStack additionalDrop = this.mythicAltarFeature
                .getCustomItemManagerRegistry()
                .getCustomItemManager(EssenceOfSpawnerManager.class)
                .createCustomItem();

        event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), additionalDrop);

    }
}
