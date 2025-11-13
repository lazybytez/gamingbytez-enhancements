package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.safarinet;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.safarinet.SafariNetManager;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Random;
import java.util.Set;

/**
 * Listener for catching entities with Safari Nets.
 */
public class SafariNetCatchEntityListener implements Listener {

    private static final Set<EntityType> BLACKLISTED_ENTITIES = Set.of(
            EntityType.PLAYER,
            EntityType.ARMOR_STAND,
            EntityType.ITEM,
            EntityType.ITEM_FRAME,
            EntityType.GLOW_ITEM_FRAME,
            EntityType.PAINTING,
            EntityType.ENDER_DRAGON,
            EntityType.WITHER,
            EntityType.GIANT
    );

    private static final double CAPTURE_SUCCESS_RATE = 0.25;

    private final MythicAltarFeature mythicAltarFeature;
    private final Plugin plugin;
    private final Random random;

    public SafariNetCatchEntityListener(MythicAltarFeature mythicAltarFeature, Plugin plugin) {
        this.mythicAltarFeature = mythicAltarFeature;
        this.plugin = plugin;
        this.random = new Random();
    }

    /**
     * Handle Safari Net projectile hits on entities.
     * <p>
     * Validates the projectile is a Safari Net, checks if the hit entity is capturable,
     * and executes the capture animation with success/failure logic.
     *
     * @param event The projectile hit event
     */
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        if (!(projectile instanceof Snowball snowball)) {
            return;
        }

        ItemStack item = snowball.getItem();
        SafariNetManager safariNetManager = this.mythicAltarFeature
                .getCustomItemManagerRegistry()
                .getCustomItemManager(SafariNetManager.class);

        if (!safariNetManager.isCustomItem(item)) {
            return;
        }

        if (safariNetManager.hasEntity(item)) {
            return;
        }

        String playerName = this.extractPlayerName(snowball);
        Entity hitEntity = event.getHitEntity();

        if (!(hitEntity instanceof LivingEntity livingEntity)) {
            this.logMissedThrow(playerName, snowball.getLocation());
            return;
        }

        Location entityLocation = livingEntity.getLocation();
        this.logThrow(playerName, livingEntity.getType(), entityLocation);

        if (BLACKLISTED_ENTITIES.contains(livingEntity.getType())) {
            this.handleBlacklistedEntity(playerName, livingEntity, entityLocation);
        } else {
            this.handleCaptureAttempt(playerName, livingEntity, entityLocation, item, safariNetManager);
        }

        this.scheduleEmptyNetCleanup(entityLocation, safariNetManager);
        snowball.remove();
    }

    /**
     * Extract player name from snowball shooter.
     *
     * @param snowball The snowball projectile
     * @return The player name or "Unknown"
     */
    private String extractPlayerName(Snowball snowball) {
        return snowball.getShooter() instanceof org.bukkit.entity.Player player
                ? player.getName()
                : "Unknown";
    }

    /**
     * Log a missed Safari Net throw.
     *
     * @param playerName The player name
     * @param location   The location where the net was thrown
     */
    private void logMissedThrow(String playerName, Location location) {
        this.plugin.getLogger().info("The player "
                + playerName
                + " threw a Safari Net at "
                + location
                + " but did not hit an entity");
    }

    /**
     * Log a Safari Net throw at an entity.
     *
     * @param playerName The player name
     * @param entityType The entity type hit
     * @param location   The entity location
     */
    private void logThrow(String playerName, EntityType entityType, Location location) {
        this.plugin.getLogger().info("The player "
                + playerName
                + " threw a Safari Net at a "
                + entityType
                + " at "
                + location);
    }

    /**
     * Handle blacklisted entity hit with visual feedback.
     *
     * @param playerName    The player name
     * @param livingEntity  The blacklisted entity
     * @param entityLocation The entity location
     */
    private void handleBlacklistedEntity(String playerName, LivingEntity livingEntity, Location entityLocation) {
        this.plugin.getLogger().info("The player "
                + playerName
                + " attempted to capture a blacklisted entity "
                + livingEntity.getType()
                + " at "
                + entityLocation);

        this.showBlacklistedParticleEffect(entityLocation);
        entityLocation.getWorld().playSound(entityLocation, Sound.ENTITY_ITEM_BREAK, 1.0f, 0.5f);
    }

    /**
     * Handle capture attempt with animation and success/failure logic.
     *
     * @param playerName       The player name
     * @param livingEntity     The entity to capture
     * @param entityLocation   The entity location
     * @param item             The Safari Net item
     * @param safariNetManager The Safari Net manager
     */
    private void handleCaptureAttempt(String playerName, LivingEntity livingEntity, Location entityLocation,
                                      ItemStack item, SafariNetManager safariNetManager) {
        boolean success = this.random.nextDouble() < CAPTURE_SUCCESS_RATE;

        safariNetManager.storeEntity(item, livingEntity);
        livingEntity.remove();

        ItemStack visualClone = safariNetManager.createCustomItem();
        Item visualItem = entityLocation.getWorld().dropItem(entityLocation, visualClone);
        visualItem.setVelocity(visualItem.getVelocity().multiply(0));
        visualItem.setPickupDelay(Integer.MAX_VALUE);
        visualItem.setCustomNameVisible(false);

        this.showCaptureAnimation(entityLocation, success, visualItem, () -> {
            if (visualItem.isValid()) {
                visualItem.remove();
            }

            if (success) {
                this.handleCaptureSuccess(playerName, livingEntity, entityLocation, item);
            } else {
                this.handleCaptureFailure(playerName, livingEntity, entityLocation, item, safariNetManager);
            }
        });
    }

    /**
     * Handle successful capture.
     *
     * @param playerName     The player name
     * @param livingEntity   The captured entity
     * @param entityLocation The entity location
     * @param item           The Safari Net item
     */
    private void handleCaptureSuccess(String playerName, LivingEntity livingEntity, Location entityLocation, ItemStack item) {
        this.plugin.getLogger().info("The player "
                + playerName
                + " successfully captured a "
                + livingEntity.getType()
                + " at "
                + entityLocation);

        entityLocation.getWorld().playSound(entityLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

        Item droppedItem = entityLocation.getWorld().dropItem(entityLocation, item);
        droppedItem.setVelocity(droppedItem.getVelocity().multiply(0));
        droppedItem.setPickupDelay(20);
    }

    /**
     * Handle failed capture by respawning the entity.
     *
     * @param playerName       The player name
     * @param livingEntity     The entity that escaped
     * @param entityLocation   The entity location
     * @param item             The Safari Net item
     * @param safariNetManager The Safari Net manager
     */
    private void handleCaptureFailure(String playerName, LivingEntity livingEntity, Location entityLocation,
                                      ItemStack item, SafariNetManager safariNetManager) {
        this.plugin.getLogger().info("The player "
                + playerName
                + " failed to capture a "
                + livingEntity.getType()
                + " at "
                + entityLocation
                + " and the entity has been respawned");

        entityLocation.getWorld().playSound(entityLocation, Sound.ENTITY_ITEM_BREAK, 1.0f, 0.5f);
        safariNetManager.spawnEntityFromNet(item, entityLocation);
    }

    /**
     * Schedule cleanup of empty Safari Nets dropped during capture.
     *
     * @param entityLocation   The location to search for nets
     * @param safariNetManager The Safari Net manager
     */
    private void scheduleEmptyNetCleanup(Location entityLocation, SafariNetManager safariNetManager) {
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            entityLocation.getNearbyEntitiesByType(Item.class, 2.0).forEach(itemEntity -> {
                ItemStack droppedItemStack = itemEntity.getItemStack();
                if (safariNetManager.isCustomItem(droppedItemStack)
                        && !safariNetManager.hasEntity(droppedItemStack)
                        && itemEntity.getPickupDelay() != Integer.MAX_VALUE) {
                    itemEntity.remove();
                }
            });
        }, 80L);
    }

    /**
     * Show immediate dark gray effect for blacklisted entities.
     *
     * @param location The location to show effects at
     */
    private void showBlacklistedParticleEffect(Location location) {
        this.showEnhancedFinalPulse(location, Color.fromRGB(64, 64, 64));

        location.getWorld().spawnParticle(
                Particle.FIREWORK,
                location.clone().add(0, 0.5, 0),
                10,
                0.3, 0.3, 0.3,
                0.1
        );
    }

    /**
     * Show capture animation with callback.
     * <p>
     * Animation sequence: RED pulse → RED pulse → WHITE/GRAY pulse (success/failure).
     *
     * @param location   The location to show animation at
     * @param success    Whether the capture was successful
     * @param visualItem The visual item to animate
     * @param callback   Callback to run after animation completes
     */
    private void showCaptureAnimation(Location location, boolean success, Item visualItem, Runnable callback) {
        Location itemLocation = visualItem.getLocation();

        this.showParticlePulse(itemLocation, Color.RED, 0L, false);
        itemLocation.getWorld().playSound(itemLocation, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.2f);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (visualItem.isValid()) {
                Location loc = visualItem.getLocation();
                this.showParticlePulse(loc, Color.RED, 0L, false);
                loc.getWorld().playSound(loc, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.4f);
            }
        }, 30L);

        Color finalColor = success ? Color.WHITE : Color.fromRGB(64, 64, 64);
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            Location finalLocation = visualItem.isValid() ? visualItem.getLocation() : itemLocation;

            this.showEnhancedFinalPulse(finalLocation, finalColor);

            finalLocation.getWorld().spawnParticle(
                    Particle.FIREWORK,
                    finalLocation.clone().add(0, 0.5, 0),
                    success ? 40 : 10,
                    0.5, 0.5, 0.5,
                    0.15
            );

            callback.run();
        }, 60L);
    }

    /**
     * Show a single particle pulse at the given location.
     *
     * @param location The location to spawn particles at
     * @param color    The color of the particles
     * @param delay    The delay in ticks before showing the pulse
     * @param enhanced Whether to use enhanced particle count and size
     */
    private void showParticlePulse(Location location, Color color, long delay, boolean enhanced) {
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            float particleSize = enhanced ? 2.0f : 1.5f;
            int particleCount = enhanced ? 50 : 30;
            Particle.DustOptions dustOptions = new Particle.DustOptions(color, particleSize);

            for (int i = 0; i < particleCount; i++) {
                double angle1 = this.random.nextDouble() * Math.PI * 2;
                double angle2 = this.random.nextDouble() * Math.PI * 2;
                double radius = 0.5 + this.random.nextDouble() * 0.5;

                double x = radius * Math.sin(angle1) * Math.cos(angle2);
                double y = radius * Math.sin(angle1) * Math.sin(angle2);
                double z = radius * Math.cos(angle1);

                Location particleLocation = location.clone().add(x, y + 0.5, z);

                location.getWorld().spawnParticle(
                        Particle.DUST,
                        particleLocation,
                        1,
                        0, 0, 0,
                        0,
                        dustOptions
                );
            }
        }, delay);
    }

    /**
     * Show an enhanced final pulse with multiple waves for better visibility.
     *
     * @param location The location to show the pulse at
     * @param color    The color of the pulse
     */
    private void showEnhancedFinalPulse(Location location, Color color) {
        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 2.5f);

        this.spawnParticleSphere(location, dustOptions, 60, 0.7);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            this.spawnParticleSphere(location, dustOptions, 50, 0.9);
        }, 5L);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            this.spawnParticleSphere(location, dustOptions, 40, 1.1);
        }, 10L);
    }

    /**
     * Spawn a sphere of particles at the given location.
     *
     * @param location    The center location
     * @param dustOptions The particle dust options
     * @param count       The number of particles to spawn
     * @param maxRadius   The maximum radius of the sphere
     */
    private void spawnParticleSphere(Location location, Particle.DustOptions dustOptions, int count, double maxRadius) {
        for (int i = 0; i < count; i++) {
            double angle1 = this.random.nextDouble() * Math.PI * 2;
            double angle2 = this.random.nextDouble() * Math.PI * 2;
            double radius = 0.3 + this.random.nextDouble() * maxRadius;

            double x = radius * Math.sin(angle1) * Math.cos(angle2);
            double y = radius * Math.sin(angle1) * Math.sin(angle2);
            double z = radius * Math.cos(angle1);

            Location particleLocation = location.clone().add(x, y + 0.5, z);

            location.getWorld().spawnParticle(
                    Particle.DUST,
                    particleLocation,
                    1,
                    0, 0, 0,
                    0,
                    dustOptions
            );
        }
    }
}
