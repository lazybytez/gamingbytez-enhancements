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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class MythicAltarFeatureTest {
    private static final Component EXPECTED_PREFIX = Component.text("[", NamedTextColor.DARK_GRAY)
            .append(Component.text("MythicAltar", NamedTextColor.GOLD))
            .append(Component.text("] ", NamedTextColor.DARK_GRAY));

    @Mock
    private EnhancementsPlugin plugin;

    @Test
    void getMessenger_rendersThePrefixOfTheFeature() {
        MythicAltarFeature feature = new MythicAltarFeature(this.plugin);

        Component body = Component.text("Body");

        assertEquals(EXPECTED_PREFIX.append(body), feature.getMessenger().prefixed(body));
    }

    @Test
    void getMessenger_returnsTheSameInstanceOnEveryCall() {
        MythicAltarFeature feature = new MythicAltarFeature(this.plugin);

        assertNotNull(feature.getMessenger());
        assertEquals(feature.getMessenger(), feature.getMessenger());
    }
}
