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

    private final MythicAltarFeature mythicAltarFeature;
    private final Plugin plugin;
    private final Random random;

    // Blacklisted entity types that cannot be caught
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

    public SafariNetCatchEntityListener(MythicAltarFeature mythicAltarFeature, Plugin plugin) {
        this.mythicAltarFeature = mythicAltarFeature;
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        // Check if the projectile is a snowball
        if (!(projectile instanceof Snowball snowball)) {
            return;
        }

        // Check if the snowball has an item (Safari Net)
        ItemStack item = snowball.getItem();
        SafariNetManager safariNetManager = this.mythicAltarFeature
                .getCustomItemManagerRegistry()
                .getCustomItemManager(SafariNetManager.class);

        if (!safariNetManager.isCustomItem(item)) {
            return;
        }

        // Check if Safari Net already has an entity
        if (safariNetManager.hasEntity(item)) {
            return;
        }

        // Check if the snowball hit an entity
        Entity hitEntity = event.getHitEntity();
        if (!(hitEntity instanceof LivingEntity livingEntity)) {
            return;
        }

        Location entityLocation = livingEntity.getLocation();

        // Check if entity type is blacklisted - show immediate failure
        boolean isBlacklisted = BLACKLISTED_ENTITIES.contains(livingEntity.getType());

        if (isBlacklisted) {
            // Blacklisted entities: show immediate dark gray effect without capture attempt
            showBlacklistedParticleEffect(entityLocation);

            // Play failure sound
            entityLocation.getWorld().playSound(entityLocation, Sound.ENTITY_ITEM_BREAK, 1.0f, 0.5f);

            // Safari Net is consumed (don't drop anything)
        } else {
            // 25% chance to catch non-blacklisted entities
            boolean success = random.nextDouble() < 0.25;

            // Store entity metadata before removing
            safariNetManager.storeEntity(item, livingEntity);

            // Remove the entity from world during capture attempt
            livingEntity.remove();

            // Drop a visual clone (empty Safari Net) that cannot be picked up during animation
            ItemStack visualClone = safariNetManager.createCustomItem();
            Item visualItem = entityLocation.getWorld().dropItem(entityLocation, visualClone);
            visualItem.setVelocity(visualItem.getVelocity().multiply(0));
            visualItem.setPickupDelay(Integer.MAX_VALUE); // Make it unpickupable
            visualItem.setCustomNameVisible(false); // Make it look normal

            // Show capture animation (2x red, then white/gray)
            showCaptureAnimation(entityLocation, success, visualItem, () -> {
                // Remove the visual clone
                if (visualItem.isValid()) {
                    visualItem.remove();
                }

                if (success) {
                    // Success: Play success sound and drop the Safari Net with captured entity
                    entityLocation.getWorld().playSound(entityLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

                    // Replace with the real Safari Net containing the entity
                    Item droppedItem = entityLocation.getWorld().dropItem(entityLocation, item);
                    droppedItem.setVelocity(droppedItem.getVelocity().multiply(0));
                    droppedItem.setPickupDelay(20);
                } else {
                    // Failure: Play failure sound, respawn entity as clone, consume Safari Net
                    entityLocation.getWorld().playSound(entityLocation, Sound.ENTITY_ITEM_BREAK, 1.0f, 0.5f);

                    // Respawn the entity as an exact clone
                    safariNetManager.spawnEntityFromNet(item, entityLocation);

                    // Safari Net is consumed (visual clone already removed, don't drop anything else)
                }
            });
        }

        // Remove any Safari Net items that might have been dropped by the snowball
        // But delay this until after the animation could complete to avoid removing the visual clone
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            entityLocation.getNearbyEntitiesByType(Item.class, 2.0).forEach(itemEntity -> {
                ItemStack droppedItemStack = itemEntity.getItemStack();
                // Remove empty Safari Nets (ones without captured entity)
                // Skip items with MAX_VALUE pickup delay (those are visual clones being animated)
                if (safariNetManager.isCustomItem(droppedItemStack)
                    && !safariNetManager.hasEntity(droppedItemStack)
                    && itemEntity.getPickupDelay() != Integer.MAX_VALUE) {
                    itemEntity.remove();
                }
            });
        }, 80L); // Delay cleanup until after animation completes (60 ticks + buffer)

        // Remove the snowball projectile
        snowball.remove();
    }

    /**
     * Show immediate dark gray effect for blacklisted entities.
     */
    private void showBlacklistedParticleEffect(Location location) {
        Color darkGray = Color.fromRGB(64, 64, 64);

        // Show enhanced dark gray effect
        showEnhancedFinalPulse(location, darkGray);

        // Add a few failure firework particles
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
     * Animation: RED (1.5 seconds) → RED (1.5 seconds) → WHITE (success) or DARK GRAY (failure)
     * The callback is executed after the animation completes.
     */
    private void showCaptureAnimation(Location location, boolean success, Item visualItem, Runnable callback) {
        Location itemLocation = visualItem.getLocation();

        // Tick 1: RED (wiggle) with pokeball sound
        showParticlePulse(itemLocation, Color.RED, 0L, false);
        itemLocation.getWorld().playSound(itemLocation, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.8f, 1.2f);

        // Tick 2: RED (wiggle) - after ~1.5 seconds (30 ticks) with pokeball sound
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (visualItem.isValid()) {
                Location loc = visualItem.getLocation();
                showParticlePulse(loc, Color.RED, 0L, false);
                loc.getWorld().playSound(loc, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.8f, 1.4f);
            }
        }, 30L);

        // Tick 3: WHITE (success) or DARK GRAY (failure) - after ~3 seconds (60 ticks)
        Color finalColor = success ? Color.WHITE : Color.fromRGB(64, 64, 64);
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            Location finalLocation = visualItem.isValid() ? visualItem.getLocation() : itemLocation;

            // Show enhanced final effect (larger and more visible)
            showEnhancedFinalPulse(finalLocation, finalColor);

            // Add firework particles on final tick
            finalLocation.getWorld().spawnParticle(
                    Particle.FIREWORK,
                    finalLocation.clone().add(0, 0.5, 0),
                    success ? 40 : 10,  // More particles
                    0.5, 0.5, 0.5,
                    0.15
            );

            // Execute callback after animation completes
            callback.run();
        }, 60L);
    }

    /**
     * Show a single particle pulse at the given location.
     */
    private void showParticlePulse(Location location, Color color, long delay, boolean enhanced) {
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            float particleSize = enhanced ? 2.0f : 1.5f;
            int particleCount = enhanced ? 50 : 30;
            Particle.DustOptions dustOptions = new Particle.DustOptions(color, particleSize);

            // Spawn particles in a sphere pattern
            for (int i = 0; i < particleCount; i++) {
                double angle1 = random.nextDouble() * Math.PI * 2;
                double angle2 = random.nextDouble() * Math.PI * 2;
                double radius = 0.5 + random.nextDouble() * 0.5;

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
     */
    private void showEnhancedFinalPulse(Location location, Color color) {
        Particle.DustOptions dustOptions = new Particle.DustOptions(color, 2.5f);

        // First wave - immediate
        spawnParticleSphere(location, dustOptions, 60, 0.7);

        // Second wave - after 5 ticks
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            spawnParticleSphere(location, dustOptions, 50, 0.9);
        }, 5L);

        // Third wave - after 10 ticks
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            spawnParticleSphere(location, dustOptions, 40, 1.1);
        }, 10L);
    }

    /**
     * Spawn a sphere of particles at the given location.
     */
    private void spawnParticleSphere(Location location, Particle.DustOptions dustOptions, int count, double maxRadius) {
        for (int i = 0; i < count; i++) {
            double angle1 = random.nextDouble() * Math.PI * 2;
            double angle2 = random.nextDouble() * Math.PI * 2;
            double radius = 0.3 + random.nextDouble() * maxRadius;

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
