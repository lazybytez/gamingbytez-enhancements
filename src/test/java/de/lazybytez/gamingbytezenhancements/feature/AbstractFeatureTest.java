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
package de.lazybytez.gamingbytezenhancements.feature;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractFeatureTest {
    @Mock
    private EnhancementsPlugin plugin;

    private AbstractFeature featureWithName(String name) {
        return new AbstractFeature(this.plugin) {
            @Override
            public void onEnable() {
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

    @Test
    void getFeatureConfigKey_stripsSpacesFromName() {
        assertEquals("FarmlandProtection", this.featureWithName("Farmland Protection").getFeatureConfigKey());
    }

    @Test
    void getFeatureConfigKey_nameWithoutSpacesIsUnchanged() {
        assertEquals("MythicAltar", this.featureWithName("MythicAltar").getFeatureConfigKey());
    }

    @Test
    void isEnabled_returnsTrueWhenConfiguredTrue() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("features.TestFeature", true);
        when(this.plugin.getConfig()).thenReturn(config);

        assertTrue(this.featureWithName("TestFeature").isEnabled());
    }

    @Test
    void isEnabled_returnsFalseWhenConfiguredFalse() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("features.TestFeature", false);
        when(this.plugin.getConfig()).thenReturn(config);

        assertFalse(this.featureWithName("TestFeature").isEnabled());
    }

    @Test
    void isEnabled_defaultsTrueWhenKeyAbsent() {
        when(this.plugin.getConfig()).thenReturn(new YamlConfiguration());

        assertTrue(this.featureWithName("TestFeature").isEnabled());
    }

    @Test
    void isEnabled_cachesConfigReadResult() {
        YamlConfiguration config = spy(new YamlConfiguration());
        config.set("features.TestFeature", true);
        when(this.plugin.getConfig()).thenReturn(config);

        AbstractFeature feature = this.featureWithName("TestFeature");
        feature.isEnabled();
        feature.isEnabled();

        verify(config, times(1)).getBoolean(anyString(), anyBoolean());
    }
}
