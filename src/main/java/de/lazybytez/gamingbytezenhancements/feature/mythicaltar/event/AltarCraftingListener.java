package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.MythicAltar;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.CompletableRecipeInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.CompletableRecipeRegistryInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.schema.structure.MythicAltarStructure;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.schema.validator.AltarSchemaValidatorInterface;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Listener for the crafting of items on the altar.
 * <p>
 * This listener is handling the magic behind the functionality of the altar.
 * It listens for item frame changes and checks if the altar structure is valid.
 * If it is, it will create a new altar and start the crafting process.
 * <p>
 * Right now, it only supports the crafting of the Mythic Altar.
 * In the future, we may support multiple altars. If we get to that point, we should refactor this class
 * to use factories to create the altars and registries for the structures.
 */
public class AltarCraftingListener implements Listener {
    private final AltarSchemaValidatorInterface validator;
    private final CompletableRecipeRegistryInterface recipeRegistry;

    private final MythicAltarStructure altarStructure = new MythicAltarStructure();

    public AltarCraftingListener(AltarSchemaValidatorInterface validator, CompletableRecipeRegistryInterface recipeRegistry) {
        this.validator = validator;
        this.recipeRegistry = recipeRegistry;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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

        MythicAltar altar = new MythicAltar(centerBlockLocation);

        // Center completes a recipe and triggers recipe validation and crafting.
        ItemFrame centerPedestal = altar.getPedestal(PedestalLocation.CENTER);
        if (centerPedestal == null) {
            return;
        }

        // Force item to be in frame before recipe validation
        // Cancel event as we fulfilled it here manually.
        // TODO: Can we find a better solution to not have AIR in the center frame?
        event.setCancelled(true);
        centerPedestal.setItem(event.getItemStack());

        CompletableRecipeInterface recipe = this.recipeRegistry.findMatchingRecipe(altar);
        if (recipe == null) {
            event.getPlayer().sendMessage("No matching recipe found for the altar.");

            return;
        }

        recipe.onRecipeComplete(altar, event);
    }
}
