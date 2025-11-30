package de.lazybytez.gamingbytezenhancements.feature.antimobgriefing;

import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GriefProtectionRegistryTest {
    @Test
    void griefingDisabledEntityTypes_isNotEmpty() {
        assertFalse(GriefProtectionRegistry.GRIEFING_DISABLED_ENTITY_TYPES.isEmpty());
    }

    @Test
    void griefingDisabledEntityTypes_isImmutable() {
        assertThrows(UnsupportedOperationException.class, () ->
                GriefProtectionRegistry.GRIEFING_DISABLED_ENTITY_TYPES.add(EntityType.ZOMBIE)
        );
    }

    @Test
    void griefingDisabledProjectilesWithShootersCheck_isNotEmpty() {
        assertFalse(GriefProtectionRegistry.GRIEFING_DISABLED_PROJECTILES_WITH_SHOOTERS_CHECK.isEmpty());
    }

    @Test
    void griefingDisabledProjectilesWithShootersCheck_isImmutable() {
        assertThrows(UnsupportedOperationException.class, () ->
                GriefProtectionRegistry.GRIEFING_DISABLED_PROJECTILES_WITH_SHOOTERS_CHECK.add(EntityType.FIREBALL)
        );
    }

    @Test
    void griefingDisabledProjectileShooters_isNotEmpty() {
        assertFalse(GriefProtectionRegistry.GRIEFING_DISABLED_PROJECTILE_SHOOTERS.isEmpty());
    }

    @Test
    void protectedEntities_isNotEmpty() {
        assertFalse(GriefProtectionRegistry.PROTECTED_ENTITIES.isEmpty());
    }

    @Test
    void protectedEntities_isImmutable() {
        assertThrows(UnsupportedOperationException.class, () ->
                GriefProtectionRegistry.PROTECTED_ENTITIES.add(EntityType.PIG)
        );
    }
}
