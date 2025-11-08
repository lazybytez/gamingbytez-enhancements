package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.MythicAltar;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.CompletableRecipeInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.CompletableRecipeRegistryInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.schema.structure.MythicAltarStructure;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.schema.validator.AltarSchemaValidatorInterface;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

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
    public static final long ALTAR_LOCK_TIMEOUT = 1000L * 60L * 5L; // 5 minutes
    private final Plugin plugin;
    private final Logger logger;
    private final AltarSchemaValidatorInterface validator;
    private final CompletableRecipeRegistryInterface recipeRegistry;
    private final Map<UUID, Long> altarLock = new ConcurrentHashMap<>();

    // Right now we only support this one
    private final MythicAltarStructure altarStructure = new MythicAltarStructure();

    public AltarCraftingListener(
            Plugin plugin,
            AltarSchemaValidatorInterface validator,
            CompletableRecipeRegistryInterface recipeRegistry
    ) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.validator = validator;
        this.recipeRegistry = recipeRegistry;
    }

    /**
     * Listens for item frame changes and checks if the altar structure is valid and triggers recipe handling.
     *
     * @param event The event that was triggered.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemFrameInteract(PlayerItemFrameChangeEvent event) {
        ItemFrame centerFrame = event.getItemFrame();

        Location centerFrameLocation = centerFrame.getLocation();
        Location centerBlockLocation = centerFrameLocation.clone().subtract(0, 1, 0);

        if (this.altarLock.containsKey(centerFrame.getUniqueId())) {
            long lastTrigger = this.altarLock.get(centerFrame.getUniqueId());
            if (System.currentTimeMillis() - lastTrigger < ALTAR_LOCK_TIMEOUT) {
                this.logger.info("Player " + event.getPlayer().getName() + " tried to trigger the altar at " + centerBlockLocation + " too soon.");
                event.getPlayer().sendMessage(Component.textOfChildren(
                        MythicAltarFeature.CHAT_MESSAGE_PREFIX,
                        Component.text("This altar is currently in use.", NamedTextColor.RED)
                ));
                event.setCancelled(true);

                return;
            }
        }

        if (!event.getAction().equals(PlayerItemFrameChangeEvent.ItemFrameChangeAction.PLACE)) {
            return;
        }

        if (!this.validator.validate(this.altarStructure, centerBlockLocation, centerBlockLocation.getWorld())) {
            return;
        }

        AltarInterface altar = new MythicAltar(centerBlockLocation);

        // Center completes a recipe and triggers recipe validation and crafting.
        ItemFrame centerPedestal = altar.getPedestal(PedestalLocation.CENTER);
        if (centerPedestal == null) {
            return;
        }

        // Set item already before event is fully handled.
        // At this point, we will clear the item frame after the recipe is completed anyway.
        centerPedestal.setItem(event.getItemStack());

        this.logger.info("Player " + event.getPlayer().getName() + " triggered a recipe on the altar at " + centerBlockLocation + ".");

        this.altarLock.put(centerFrame.getUniqueId(), System.currentTimeMillis());

        // Closure that allows action to release the lock after the recipe is completed.
        Runnable removeLock = () -> this.altarLock.remove(centerFrame.getUniqueId());

        handleTriggeredAltar(event, altar, removeLock);
    }

    /**
     * Handles the triggered altar and starts the crafting process.
     *
     * @param event      The event that triggered the altar.
     * @param altar      The altar that was triggered.
     * @param removeLock A runnable that can be used to remove the lock from the altar.
     */
    private void handleTriggeredAltar(PlayerItemFrameChangeEvent event, AltarInterface altar, Runnable removeLock) {
        Location centerBlockLocation = altar.getLocation();

        CompletableRecipeInterface recipe = this.recipeRegistry.findMatchingRecipe(altar);
        if (recipe == null) {
            event.getPlayer().sendMessage(Component.textOfChildren(
                    MythicAltarFeature.CHAT_MESSAGE_PREFIX,
                    Component.text("No recipe found for the items on the altar.", NamedTextColor.RED)
            ));
            this.logger.info("No recipe found for the items on the altar at " + centerBlockLocation + ".");
            removeLock.run();

            return;
        }

        this.logger.info("Recipe " + recipe.getClass().getSimpleName() + " found for the items on the altar at " + centerBlockLocation + ", executing recipe.");
        recipe.onRecipeComplete(this.plugin, altar, event, removeLock);
        this.logger.info("Recipe " + recipe.getClass().getSimpleName() + " executed for the items on the altar at " + centerBlockLocation + ".");

        cleanupAltar(recipe, altar);
    }

    /**
     * Cleans up the altar after a recipe has been completed.
     *
     * @param recipe The recipe that was completed.
     * @param altar  The altar that was used.
     */
    private void cleanupAltar(CompletableRecipeInterface recipe, AltarInterface altar) {
        // Delay cleanup of 1 tick to ensure the event is fully handled.
        // Within a single tick, the risk that a player interacts with the altar again is very low.
        // Most recipes take ~5 seconds to complete, so the player will not be able to interact with the altar
        // before this scheduled task is executed.
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            Location centerBlockLocation = altar.getLocation();

            if (recipe.autoCleanupAltar()) {
                this.logger.info("Cleaning up altar at " + centerBlockLocation + "...");
                altar.getPedestals().forEach((location, pedestal) -> {
                    pedestal.setItem(null);
                    // We always play cloud particles, when the altar is cleaned up.
                    pedestal.getWorld().spawnParticle(Particle.CLOUD, pedestal.getLocation(), 50);
                });
                this.logger.info("Altar at " + centerBlockLocation + " cleaned up.");
            }
        }, 1L);
    }
}
