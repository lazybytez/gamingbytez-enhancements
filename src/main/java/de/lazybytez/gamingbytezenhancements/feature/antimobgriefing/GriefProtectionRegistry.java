package de.lazybytez.gamingbytezenhancements.feature.antimobgriefing;

import org.bukkit.entity.EntityType;

import java.util.Set;

public class GriefProtectionRegistry {
    public static final Set<EntityType> PROTECTED_ENTITIES = Set.of(
            EntityType.ARMOR_STAND,
            EntityType.MINECART,
            EntityType.MINECART_CHEST,
            EntityType.MINECART_COMMAND,
            EntityType.MINECART_FURNACE,
            EntityType.MINECART_HOPPER,
            EntityType.MINECART_MOB_SPAWNER,
            EntityType.MINECART_TNT,
            EntityType.BOAT,
            EntityType.CHEST_BOAT
    );

    public static final Set<EntityType> PROTECTED_HANGING_ENTITIES = Set.of(
            EntityType.ITEM_FRAME,
            EntityType.GLOW_ITEM_FRAME,
            EntityType.PAINTING
    );
}
