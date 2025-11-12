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
        if (hitEntity == null || !(hitEntity instanceof LivingEntity livingEntity)) {
            return;
        }

        // Check if entity type is blacklisted
        if (BLACKLISTED_ENTITIES.contains(livingEntity.getType())) {
            return;
        }

        // 50% chance to catch
        boolean success = random.nextBoolean();

        Location entityLocation = livingEntity.getLocation();

        // Remove the entity if caught successfully
        if (success) {
            // Store entity in the Safari Net (with all metadata)
            safariNetManager.storeEntity(item, livingEntity);

            // Remove the entity
            livingEntity.remove();

            // Play success sound
            entityLocation.getWorld().playSound(entityLocation, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        } else {
            // Play failure sound
            entityLocation.getWorld().playSound(entityLocation, Sound.ENTITY_ITEM_BREAK, 1.0f, 0.5f);
        }

        // Drop the Safari Net as an item at the entity location
        // First remove any existing Safari Net items nearby (from the snowball)
        entityLocation.getNearbyEntitiesByType(Item.class, 1.0).forEach(itemEntity -> {
            if (safariNetManager.isCustomItem(itemEntity.getItemStack())) {
                itemEntity.remove();
            }
        });

        Item droppedItem = entityLocation.getWorld().dropItem(entityLocation, item);
        droppedItem.setVelocity(droppedItem.getVelocity().multiply(0));
        droppedItem.setPickupDelay(20);

        // Show particle effects (3 pulses)
        showParticleEffects(entityLocation, success);

        // Remove the snowball projectile
        snowball.remove();
    }

    /**
     * Show particle effects at the given location.
     * Red and white firework particles, 3 pulses.
     */
    private void showParticleEffects(Location location, boolean success) {
        Color color1 = success ? Color.WHITE : Color.RED;
        Color color2 = success ? Color.AQUA : Color.MAROON;

        for (int pulse = 0; pulse < 3; pulse++) {
            int delay = pulse * 10; // 10 ticks (0.5 seconds) between each pulse

            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                // Create firework effect
                Particle.DustOptions dustOptions1 = new Particle.DustOptions(color1, 1.5f);
                Particle.DustOptions dustOptions2 = new Particle.DustOptions(color2, 1.5f);

                // Spawn particles in a sphere pattern
                for (int i = 0; i < 30; i++) {
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
                            i % 2 == 0 ? dustOptions1 : dustOptions2
                    );
                }

                // Add some sparkle particles
                location.getWorld().spawnParticle(
                        Particle.FIREWORK,
                        location.clone().add(0, 0.5, 0),
                        10,
                        0.3, 0.3, 0.3,
                        0.1
                );

            }, delay);
        }
    }
}
