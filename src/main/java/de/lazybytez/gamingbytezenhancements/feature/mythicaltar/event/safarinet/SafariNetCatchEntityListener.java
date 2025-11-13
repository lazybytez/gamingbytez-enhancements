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

        String playerName = snowball.getShooter() instanceof org.bukkit.entity.Player player
                ? player.getName()
                : "Unknown";

        Entity hitEntity = event.getHitEntity();
        if (!(hitEntity instanceof LivingEntity livingEntity)) {
            plugin.getLogger().info("The player "
                    + playerName
                    + " threw a Safari Net at "
                    + snowball.getLocation()
                    + " but did not hit an entity");
            return;
        }

        Location entityLocation = livingEntity.getLocation();
        boolean isBlacklisted = BLACKLISTED_ENTITIES.contains(livingEntity.getType());

        plugin.getLogger().info("The player "
                + playerName
                + " threw a Safari Net at a "
                + livingEntity.getType()
                + " at "
                + entityLocation);

        if (isBlacklisted) {
            plugin.getLogger().info("The player "
                    + playerName
                    + " attempted to capture a blacklisted entity "
                    + livingEntity.getType()
                    + " at "
                    + entityLocation);

            showBlacklistedParticleEffect(entityLocation);
            entityLocation.getWorld().playSound(entityLocation, Sound.ENTITY_ITEM_BREAK, 1.0f, 0.5f);
        } else {
            boolean success = random.nextDouble() < 0.25;

            safariNetManager.storeEntity(item, livingEntity);
            livingEntity.remove();

            ItemStack visualClone = safariNetManager.createCustomItem();
            Item visualItem = entityLocation.getWorld().dropItem(entityLocation, visualClone);
            visualItem.setVelocity(visualItem.getVelocity().multiply(0));
            visualItem.setPickupDelay(Integer.MAX_VALUE);
            visualItem.setCustomNameVisible(false);

            showCaptureAnimation(entityLocation, success, visualItem, () -> {
                if (visualItem.isValid()) {
                    visualItem.remove();
                }

                if (success) {
                    plugin.getLogger().info("The player "
                            + playerName
                            + " successfully captured a "
                            + livingEntity.getType()
                            + " at "
                            + entityLocation);

                    entityLocation.getWorld().playSound(entityLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

                    Item droppedItem = entityLocation.getWorld().dropItem(entityLocation, item);
                    droppedItem.setVelocity(droppedItem.getVelocity().multiply(0));
                    droppedItem.setPickupDelay(20);
                } else {
                    plugin.getLogger().info("The player "
                            + playerName
                            + " failed to capture a "
                            + livingEntity.getType()
                            + " at "
                            + entityLocation
                            + " and the entity has been respawned");

                    entityLocation.getWorld().playSound(entityLocation, Sound.ENTITY_ITEM_BREAK, 1.0f, 0.5f);
                    safariNetManager.spawnEntityFromNet(item, entityLocation);
                }
            });
        }

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

        snowball.remove();
    }

    /**
     * Show immediate dark gray effect for blacklisted entities.
     */
    private void showBlacklistedParticleEffect(Location location) {
        Color darkGray = Color.fromRGB(64, 64, 64);
        showEnhancedFinalPulse(location, darkGray);

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
     * Animation: RED (1.5s) → RED (1.5s) → WHITE/GRAY (success/failure).
     */
    private void showCaptureAnimation(Location location, boolean success, Item visualItem, Runnable callback) {
        Location itemLocation = visualItem.getLocation();

        showParticlePulse(itemLocation, Color.RED, 0L, false);
        itemLocation.getWorld().playSound(itemLocation, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.8f, 1.2f);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (visualItem.isValid()) {
                Location loc = visualItem.getLocation();
                showParticlePulse(loc, Color.RED, 0L, false);
                loc.getWorld().playSound(loc, Sound.BLOCK_STONE_BUTTON_CLICK_ON, 0.8f, 1.4f);
            }
        }, 30L);

        Color finalColor = success ? Color.WHITE : Color.fromRGB(64, 64, 64);
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            Location finalLocation = visualItem.isValid() ? visualItem.getLocation() : itemLocation;

            showEnhancedFinalPulse(finalLocation, finalColor);

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
     */
    private void showParticlePulse(Location location, Color color, long delay, boolean enhanced) {
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            float particleSize = enhanced ? 2.0f : 1.5f;
            int particleCount = enhanced ? 50 : 30;
            Particle.DustOptions dustOptions = new Particle.DustOptions(color, particleSize);

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

        spawnParticleSphere(location, dustOptions, 60, 0.7);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            spawnParticleSphere(location, dustOptions, 50, 0.9);
        }, 5L);

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
