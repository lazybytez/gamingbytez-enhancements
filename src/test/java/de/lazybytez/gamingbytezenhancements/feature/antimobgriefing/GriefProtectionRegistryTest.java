/*
 * Gaming Bytez Enhancements - Gameplay enhancements used on our SMP servers.
 * Copyright (C) 2026 Lazy Bytez (Pascal Zarrad, Elias Knodel) and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
