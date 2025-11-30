package de.lazybytez.gamingbytezenhancements.feature.minecartportal.model;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MinecartPortalTest {
    private World mockWorld;
    private Location portalLocation;
    private Location destinationLocation;

    @BeforeEach
    void setUp() {
        this.mockWorld = mock(World.class);
        this.portalLocation = new Location(this.mockWorld, 100, 64, 100);
        this.destinationLocation = new Location(this.mockWorld, 200, 64, 200);
    }

    @Test
    void constructor_setsFieldsCorrectly() {
        MinecartPortal portal = new MinecartPortal("test-portal", this.portalLocation, this.destinationLocation);

        assertEquals("test-portal", portal.getName());
        assertEquals(this.portalLocation, portal.getPortal());
        assertEquals(this.destinationLocation, portal.getDestination());
    }

    @Test
    void serialize_returnsMapWithAllFields() {
        MinecartPortal portal = new MinecartPortal("test-portal", this.portalLocation, this.destinationLocation);

        Map<String, Object> serialized = portal.serialize();

        assertEquals(3, serialized.size());
        assertEquals("test-portal", serialized.get("name"));
        assertEquals(this.portalLocation, serialized.get("portal"));
        assertEquals(this.destinationLocation, serialized.get("destination"));
    }

    @Test
    void deserialize_reconstructsPortalFromMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "deserialized-portal");
        data.put("portal", this.portalLocation);
        data.put("destination", this.destinationLocation);

        MinecartPortal portal = MinecartPortal.deserialize(data);

        assertEquals("deserialized-portal", portal.getName());
        assertEquals(this.portalLocation, portal.getPortal());
        assertEquals(this.destinationLocation, portal.getDestination());
    }

    @Test
    void serializeAndDeserialize_maintainsDataIntegrity() {
        MinecartPortal original = new MinecartPortal("round-trip", this.portalLocation, this.destinationLocation);

        Map<String, Object> serialized = original.serialize();
        MinecartPortal deserialized = MinecartPortal.deserialize(serialized);

        assertEquals(original.getName(), deserialized.getName());
        assertEquals(original.getPortal(), deserialized.getPortal());
        assertEquals(original.getDestination(), deserialized.getDestination());
    }

    @Test
    void defaultConstructor_createsEmptyPortal() {
        MinecartPortal portal = new MinecartPortal();

        assertNotNull(portal);
    }
}
