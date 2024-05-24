package de.lazybytez.gamingbytezenhancements.feature.antimobgriefing;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;

import java.util.Set;

public class GriefProtectionRegistry {
    /**
     * This set contains all entities that should not be able to destroy blocks or kill/damage specific entities.
     * <p>
     * We use this central registry to avoid code duplication and to make it easier to maintain the list of entities.
     * The performance impact of this universally used set is negligible.
     */
    public static final Set<EntityType> GRIEFING_DISABLED_ENTITY_TYPES = Set.of(
            EntityType.CREEPER,
            EntityType.FIREBALL,
            EntityType.GHAST,
            EntityType.BLAZE,
            EntityType.ENDERMAN,
            EntityType.SHULKER,
            EntityType.SHULKER_BULLET,
            EntityType.SKELETON
    );

    /**
     * This set contains all projectile entities that should cause a check-up of the shooter.
     */
    public static final Set<EntityType> GRIEFING_DISABLED_PROJECTILES_WITH_SHOOTERS_CHECK = Set.of(
            EntityType.ARROW,
            EntityType.SPECTRAL_ARROW
    );

    /**
     * This set contains all entities that may shoot projectiles that destroy protected entities.
     * <p>
     * This set contains classes that can be used for instanceof checks to determine if
     * the shooter of a projectile is a member of this set. This is necessary because
     * the shooter of a projectile is a generic Entity object, and we need to determine if
     * the shooter is a specific entity type. The easiest way to do this is to use instanceof.
     */
    public static final Set<Class<? extends Entity>> GRIEFING_DISABLED_PROJECTILE_SHOOTERS = Set.of(
            Skeleton.class
    );

    /**
     * This set contains all entities that should be protected from being destroyed.
     * <p>
     * The protection is applied in all cases where the cause is one of the entities listed in the
     * {@link #GRIEFING_DISABLED_ENTITY_TYPES} or {@link #GRIEFING_DISABLED_PROJECTILE_SHOOTERS} sets.
     */
    public static final Set<EntityType> PROTECTED_ENTITIES = Set.of(
            // Common decorative entities and vehicles
            EntityType.ARMOR_STAND,
            EntityType.MINECART,
            EntityType.BOAT,

            // Hanging entities
            EntityType.ITEM_FRAME,
            EntityType.GLOW_ITEM_FRAME,
            EntityType.PAINTING,

            // Other vehicles
            EntityType.CHEST_MINECART,
            EntityType.COMMAND_BLOCK_MINECART,
            EntityType.FURNACE_MINECART,
            EntityType.HOPPER_MINECART,
            EntityType.SPAWNER_MINECART,
            EntityType.TNT_MINECART,
            EntityType.CHEST_BOAT
    );
}
