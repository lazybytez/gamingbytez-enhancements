package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.safarinet;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.AbstractCustomItemManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

/**
 * Manager for the Safari Net custom item.
 * <p>
 * Safari Net allows players to catch and transport entities.
 */
public class SafariNetManager extends AbstractCustomItemManager {
    public static final String PDC_KEY_SAFARI_NET = "gamingbytez-safari-net";
    public static final String PDC_KEY_ENTITY_TYPE = "gamingbytez-safari-net-entity-type";
    public static final String PDC_KEY_ENTITY_DATA = "gamingbytez-safari-net-entity-data";

    private NamespacedKey entityTypePdcKey;
    private NamespacedKey entityDataPdcKey;

    public SafariNetManager(Plugin plugin) {
        super(plugin, Material.SNOWBALL, SafariNetManager.PDC_KEY_SAFARI_NET);
    }

    /**
     * Check if the Safari Net contains an entity.
     *
     * @param safariNet The Safari Net item to check
     * @return True if the Safari Net contains an entity
     */
    public boolean hasEntity(ItemStack safariNet) {
        return safariNet.getPersistentDataContainer().has(
                this.getEntityTypePdcKey(),
                PersistentDataType.STRING
        );
    }

    /**
     * Get the entity type stored in the Safari Net.
     *
     * @param safariNet The Safari Net item
     * @return The entity type, or null if none is stored
     */
    public EntityType getEntityType(ItemStack safariNet) {
        String entityTypeName = safariNet.getPersistentDataContainer().get(
                this.getEntityTypePdcKey(),
                PersistentDataType.STRING
        );

        if (entityTypeName == null) {
            return null;
        }

        try {
            return EntityType.valueOf(entityTypeName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Spawn an entity from the Safari Net at the given location.
     * <p>
     * Uses Paper's EntitySnapshot API to restore complete entity state including
     * name, health, age, color, size, and all other NBT data.
     *
     * @param safariNet The Safari Net containing the entity
     * @param location  The location to spawn the entity at
     * @return The spawned entity, or null if spawning failed
     */
    @SuppressWarnings("UnstableApiUsage")
    public Entity spawnEntityFromNet(ItemStack safariNet, org.bukkit.Location location) {
        String encodedData = this.getEntityData(safariNet);
        EntityType entityType = this.getEntityType(safariNet);

        if (encodedData == null || entityType == null) {
            return null;
        }

        try {
            String nbtString = new String(Base64.getDecoder().decode(encodedData));
            EntitySnapshot snapshot = Bukkit.getServer().getEntityFactory().createEntitySnapshot(nbtString);

            return snapshot.createEntity(location);
        } catch (IllegalArgumentException e) {
            this.plugin.getLogger().info("Safari Net contains old or invalid format data. Spawning fresh entity of type: " + entityType);
            return location.getWorld().spawnEntity(location, entityType);
        } catch (Exception e) {
            this.plugin.getLogger().warning("Failed to spawn entity from Safari Net: " + e.getMessage());
            return null;
        }
    }

    /**
     * Store an entity in the Safari Net.
     * <p>
     * Uses Paper's EntitySnapshot API to preserve complete entity state dynamically.
     *
     * @param safariNet The Safari Net item to store the entity in
     * @param entity    The entity to store
     */
    @SuppressWarnings("UnstableApiUsage")
    public void storeEntity(ItemStack safariNet, Entity entity) {
        EntityType entityType = entity.getType();

        try {
            EntitySnapshot snapshot = entity.createSnapshot();
            if (snapshot == null) {
                this.plugin.getLogger().warning("Failed to create snapshot for entity: " + entityType);
                return;
            }

            String nbtString = snapshot.getAsString();
            String encodedData = Base64.getEncoder().encodeToString(nbtString.getBytes());

            safariNet.editPersistentDataContainer(pdc -> {
                pdc.set(this.getEntityTypePdcKey(), PersistentDataType.STRING, entityType.name());
                pdc.set(this.getEntityDataPdcKey(), PersistentDataType.STRING, encodedData);
            });

            this.updateItemDisplay(safariNet, entityType, entity);
        } catch (Exception e) {
            this.plugin.getLogger().warning("Failed to store entity in Safari Net: " + e.getMessage());
        }
    }

    /**
     * Clear the entity from the Safari Net.
     *
     * @param safariNet The Safari Net to clear
     */
    public void clearEntity(ItemStack safariNet) {
        safariNet.editPersistentDataContainer(pdc -> {
            pdc.remove(this.getEntityTypePdcKey());
            pdc.remove(this.getEntityDataPdcKey());
        });

        this.updateItemDisplay(safariNet, null, null);
    }

    @Override
    protected ItemMeta configureItemMeta(ItemMeta itemMeta) {
        itemMeta.displayName(this.computeDisplayName(null, null));
        itemMeta.lore(this.computeLore(null, null));
        itemMeta.setEnchantmentGlintOverride(true);
        itemMeta.setMaxStackSize(1);

        return itemMeta;
    }

    @Override
    public boolean isCustomItem(ItemStack item) {
        if (item == null || item.getType() != Material.SNOWBALL) {
            return false;
        }

        return item.getPersistentDataContainer().getOrDefault(
                this.getPdcKey(),
                PersistentDataType.BOOLEAN,
                false
        );
    }

    /**
     * Update the display name and lore of the Safari Net item.
     *
     * @param safariNet  The Safari Net item to update
     * @param entityType The entity type stored in the net
     * @param entity     The entity instance (may be null)
     */
    private void updateItemDisplay(ItemStack safariNet, EntityType entityType, Entity entity) {
        ItemMeta itemMeta = safariNet.getItemMeta();
        itemMeta.displayName(this.computeDisplayName(entityType, entity));
        itemMeta.lore(this.computeLore(entityType, entity));
        safariNet.setItemMeta(itemMeta);
    }

    /**
     * Compute the display name for the Safari Net.
     *
     * @param entityType The entity type stored in the net
     * @param entity     The entity instance (may be null)
     * @return The computed display name component
     */
    private Component computeDisplayName(EntityType entityType, Entity entity) {
        if (entityType == null) {
            return text("Safari Net", NamedTextColor.AQUA, TextDecoration.BOLD);
        }

        String entityName = this.extractEntityName(entityType, entity);

        return text("Safari Net ", NamedTextColor.AQUA, TextDecoration.BOLD)
                .append(text("(", NamedTextColor.GRAY))
                .append(text(entityName, NamedTextColor.YELLOW))
                .append(text(")", NamedTextColor.GRAY));
    }

    /**
     * Compute the lore for the Safari Net.
     *
     * @param entityType The entity type stored in the net
     * @param entity     The entity instance (may be null)
     * @return The computed lore components
     */
    private List<Component> computeLore(EntityType entityType, Entity entity) {
        List<Component> lore = new ArrayList<>();

        if (entityType == null) {
            lore.add(text("A mystical net that can capture creatures.", NamedTextColor.GRAY));
            lore.add(text(""));
            lore.add(text("Throw to catch entities!", NamedTextColor.GOLD));
            lore.add(text("25% success rate", NamedTextColor.GRAY));
            return lore;
        }

        String entityName = this.extractEntityName(entityType, entity);

        lore.add(text("Contains: ", NamedTextColor.GRAY).append(text(entityName, NamedTextColor.YELLOW)));
        lore.add(text(""));
        lore.add(text("Right-click to release!", NamedTextColor.GOLD));

        return lore;
    }

    /**
     * Extract the display name for an entity.
     * <p>
     * Uses the entity's custom name if available and is a TextComponent,
     * otherwise formats the entity type name.
     *
     * @param entityType The entity type
     * @param entity     The entity instance (may be null)
     * @return The entity's display name
     */
    private String extractEntityName(EntityType entityType, Entity entity) {
        if (entity != null) {
            Component customName = entity.customName();
            if (customName instanceof TextComponent textComponent) {
                return textComponent.content();
            }
        }

        return this.formatEntityName(entityType);
    }

    /**
     * Format entity type name to be more readable.
     * <p>
     * Converts "ZOMBIE_PIGMAN" to "Zombie Pigman".
     *
     * @param entityType The entity type to format
     * @return The formatted entity name
     */
    private String formatEntityName(EntityType entityType) {
        String name = entityType.name().replace("_", " ");
        String[] words = name.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(word.substring(0, 1).toUpperCase())
                    .append(word.substring(1).toLowerCase());
        }

        return result.toString();
    }

    /**
     * Get the entity data stored in the Safari Net.
     *
     * @param safariNet The Safari Net item
     * @return The encoded entity data, or null if none is stored
     */
    private String getEntityData(ItemStack safariNet) {
        return safariNet.getPersistentDataContainer().get(
                this.getEntityDataPdcKey(),
                PersistentDataType.STRING
        );
    }

    private NamespacedKey getEntityTypePdcKey() {
        if (this.entityTypePdcKey == null) {
            this.entityTypePdcKey = new NamespacedKey(this.plugin, SafariNetManager.PDC_KEY_ENTITY_TYPE);
        }

        return this.entityTypePdcKey;
    }

    private NamespacedKey getEntityDataPdcKey() {
        if (this.entityDataPdcKey == null) {
            this.entityDataPdcKey = new NamespacedKey(this.plugin, SafariNetManager.PDC_KEY_ENTITY_DATA);
        }

        return this.entityDataPdcKey;
    }
}
