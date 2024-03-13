package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.schema.structure.MythicAltarStructure;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.schema.validator.AltarSchemaValidatorInterface;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Listener for the crafting of items on the altar.
 */
public class AltarCraftingListener implements Listener {
    private final AltarSchemaValidatorInterface validator;

    private final MythicAltarStructure altarStructure = new MythicAltarStructure();

    public AltarCraftingListener(AltarSchemaValidatorInterface validator) {
        this.validator = validator;
    }

    @EventHandler
    public void onItemFrameInteract(PlayerItemFrameChangeEvent event) {
        if (!event.getAction().equals(PlayerItemFrameChangeEvent.ItemFrameChangeAction.PLACE)) {
            return;
        }

        ItemFrame centerFrame = event.getItemFrame();

        Location centerFrameLocation = centerFrame.getLocation();
        Location centerBlockLocation = centerFrameLocation.clone().subtract(0, 1, 0);

        if (!this.validator.validate(this.altarStructure, centerBlockLocation, centerBlockLocation.getWorld())) {
            return;
        }

        Bukkit.broadcastMessage("Altar is valid!");
    }
}
