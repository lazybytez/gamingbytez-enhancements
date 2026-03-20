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
package de.lazybytez.gamingbytezenhancements.feature.minecartportal;

import de.lazybytez.gamingbytezenhancements.feature.minecartportal.model.MinecartPortal;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PortalConfigurationTest {
    private PortalConfiguration configuration;
    private Plugin mockPlugin;
    private MinecartPortal testPortal;
    private Location testLocation;
    private Location testDestination;

    @BeforeEach
    void setUp() {
        this.mockPlugin = mock(Plugin.class);
        this.configuration = new PortalConfiguration(this.mockPlugin);

        World mockWorld = mock(World.class);
        this.testLocation = new Location(mockWorld, 100, 64, 100);
        this.testDestination = new Location(mockWorld, 200, 64, 200);
        this.testPortal = new MinecartPortal("test-portal", this.testLocation, this.testDestination);
    }

    @Test
    void addPortal_withNewPortal_returnsTrue() {
        boolean result = this.configuration.addPortal(this.testPortal);

        assertTrue(result);
    }

    @Test
    void addPortal_withDuplicateName_returnsFalse() {
        this.configuration.addPortal(this.testPortal);

        boolean result = this.configuration.addPortal(this.testPortal);

        assertFalse(result);
    }

    @Test
    void hasPortal_withExistingPortal_returnsTrue() {
        this.configuration.addPortal(this.testPortal);

        boolean result = this.configuration.hasPortal("test-portal");

        assertTrue(result);
    }

    @Test
    void hasPortal_withNonExistingPortal_returnsFalse() {
        boolean result = this.configuration.hasPortal("non-existing");

        assertFalse(result);
    }

    @Test
    void getPortalByName_withExistingPortal_returnsPortal() {
        this.configuration.addPortal(this.testPortal);

        MinecartPortal result = this.configuration.getPortalByName("test-portal");

        assertNotNull(result);
        assertEquals("test-portal", result.getName());
    }

    @Test
    void getPortalByName_withNonExistingPortal_returnsNull() {
        MinecartPortal result = this.configuration.getPortalByName("non-existing");

        assertNull(result);
    }

    @Test
    void getPortalAtLocation_withExactLocation_returnsPortal() {
        this.configuration.addPortal(this.testPortal);
        Location queryLocation = new Location(this.testLocation.getWorld(), 100, 64, 100);

        MinecartPortal result = this.configuration.getPortalAtLocation(queryLocation);

        assertNotNull(result);
        assertEquals("test-portal", result.getName());
    }

    @Test
    void getPortalAtLocation_withNearbyLocation_returnsPortal() {
        this.configuration.addPortal(this.testPortal);
        Location queryLocation = new Location(this.testLocation.getWorld(), 100.5, 64, 100.5);

        MinecartPortal result = this.configuration.getPortalAtLocation(queryLocation);

        assertNotNull(result);
        assertEquals("test-portal", result.getName());
    }

    @Test
    void getPortalAtLocation_withFarLocation_returnsNull() {
        this.configuration.addPortal(this.testPortal);
        Location queryLocation = new Location(this.testLocation.getWorld(), 150, 64, 150);

        MinecartPortal result = this.configuration.getPortalAtLocation(queryLocation);

        assertNull(result);
    }

    @Test
    void getPortalAtLocation_withDifferentWorld_returnsNull() {
        this.configuration.addPortal(this.testPortal);
        World otherWorld = mock(World.class);
        Location queryLocation = new Location(otherWorld, 100, 64, 100);

        MinecartPortal result = this.configuration.getPortalAtLocation(queryLocation);

        assertNull(result);
    }

    @Test
    void updatePortal_withExistingPortal_returnsTrue() {
        this.configuration.addPortal(this.testPortal);
        Location newDestination = new Location(this.testLocation.getWorld(), 300, 64, 300);
        MinecartPortal updatedPortal = new MinecartPortal("test-portal", this.testLocation, newDestination);

        boolean result = this.configuration.updatePortal(updatedPortal);

        assertTrue(result);
        assertEquals(newDestination, this.configuration.getPortalByName("test-portal").getDestination());
    }

    @Test
    void updatePortal_withNonExistingPortal_returnsFalse() {
        MinecartPortal nonExistingPortal = new MinecartPortal("non-existing", this.testLocation, this.testDestination);

        boolean result = this.configuration.updatePortal(nonExistingPortal);

        assertFalse(result);
    }

    @Test
    void deletePortal_withExistingPortal_returnsTrue() {
        this.configuration.addPortal(this.testPortal);

        boolean result = this.configuration.deletePortal(this.testPortal);

        assertTrue(result);
        assertFalse(this.configuration.hasPortal("test-portal"));
    }

    @Test
    void deletePortal_withNonExistingPortal_returnsFalse() {
        boolean result = this.configuration.deletePortal(this.testPortal);

        assertFalse(result);
    }

    @Test
    void getPortals_withNoPortals_returnsEmptyList() {
        List<MinecartPortal> portals = this.configuration.getPortals();

        assertNotNull(portals);
        assertTrue(portals.isEmpty());
    }

    @Test
    void getPortals_withMultiplePortals_returnsAllPortals() {
        MinecartPortal portal2 = new MinecartPortal("portal-2", this.testLocation, this.testDestination);
        MinecartPortal portal3 = new MinecartPortal("portal-3", this.testLocation, this.testDestination);

        this.configuration.addPortal(this.testPortal);
        this.configuration.addPortal(portal2);
        this.configuration.addPortal(portal3);

        List<MinecartPortal> portals = this.configuration.getPortals();

        assertEquals(3, portals.size());
    }

    @Test
    void getPortals_returnsImmutableCopy() {
        this.configuration.addPortal(this.testPortal);

        List<MinecartPortal> portals = this.configuration.getPortals();

        assertThrows(UnsupportedOperationException.class, () -> portals.add(
                new MinecartPortal("new", this.testLocation, this.testDestination)
        ));
    }
}
