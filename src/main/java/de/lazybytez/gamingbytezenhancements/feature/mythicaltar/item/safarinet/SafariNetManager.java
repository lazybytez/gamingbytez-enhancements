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
     */
    public boolean hasEntity(ItemStack safariNet) {
        return safariNet.getPersistentDataContainer().has(
                this.getEntityTypePdcKey(),
                PersistentDataType.STRING
        );
    }

    /**
     * Get the entity type stored in the Safari Net.
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
     * Get the entity data stored in the Safari Net.
     */
    public String getEntityData(ItemStack safariNet) {
        return safariNet.getPersistentDataContainer().get(
                this.getEntityDataPdcKey(),
                PersistentDataType.STRING
        );
    }

    /**
     * Spawn an entity from the Safari Net at the given location.
     * Restores the complete entity state using Paper's EntitySnapshot API.
     *
     * @param safariNet The Safari Net containing the entity
     * @param location  The location to spawn the entity at
     * @return The spawned entity, or null if spawning failed
     */
    @SuppressWarnings("UnstableApiUsage")
    public Entity spawnEntityFromNet(ItemStack safariNet, org.bukkit.Location location) {
        String encodedData = getEntityData(safariNet);
        EntityType entityType = getEntityType(safariNet);

        if (encodedData == null || entityType == null) {
            return null;
        }

        try {
            String nbtString = new String(Base64.getDecoder().decode(encodedData));
            EntitySnapshot snapshot = Bukkit.getServer().getEntityFactory().createEntitySnapshot(nbtString);

            return snapshot.createEntity(location);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().info("Safari Net contains old or invalid format data. Spawning fresh entity of type: " + entityType);
            return location.getWorld().spawnEntity(location, entityType);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to spawn entity from Safari Net: " + e.getMessage());
            return null;
        }
    }

    /**
     * Store an entity in the Safari Net.
     * Uses Paper's EntitySnapshot API to preserve the complete entity state dynamically.
     */
    @SuppressWarnings("UnstableApiUsage")
    public void storeEntity(ItemStack safariNet, Entity entity) {
        EntityType entityType = entity.getType();

        try {
            EntitySnapshot snapshot = entity.createSnapshot();
            if (snapshot == null) {
                plugin.getLogger().warning("Failed to create snapshot for entity: " + entityType);
                return;
            }

            String nbtString = snapshot.getAsString();
            String encodedData = Base64.getEncoder().encodeToString(nbtString.getBytes());

            safariNet.editPersistentDataContainer(pdc -> {
                pdc.set(this.getEntityTypePdcKey(), PersistentDataType.STRING, entityType.name());
                pdc.set(this.getEntityDataPdcKey(), PersistentDataType.STRING, encodedData);
            });

            ItemMeta itemMeta = safariNet.getItemMeta();
            itemMeta.displayName(this.computeDisplayName(entityType, entity));
            itemMeta.lore(this.computeLore(entityType, entity));
            safariNet.setItemMeta(itemMeta);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to store entity in Safari Net: " + e.getMessage());
        }
    }

    /**
     * Clear the entity from the Safari Net.
     */
    public void clearEntity(ItemStack safariNet) {
        safariNet.editPersistentDataContainer(pdc -> {
            pdc.remove(this.getEntityTypePdcKey());
            pdc.remove(this.getEntityDataPdcKey());
        });

        ItemMeta itemMeta = safariNet.getItemMeta();
        itemMeta.displayName(this.computeDisplayName(null, null));
        itemMeta.lore(this.computeLore(null, null));
        safariNet.setItemMeta(itemMeta);
    }

    @Override
    protected ItemMeta configureItemMeta(ItemMeta itemMeta) {
        itemMeta.displayName(this.computeDisplayName(null, null));
        itemMeta.lore(this.computeLore(null, null));
        itemMeta.setEnchantmentGlintOverride(true);
        itemMeta.setMaxStackSize(1);

        return itemMeta;
    }

    /**
     * Compute the display name for the Safari Net.
     */
    private Component computeDisplayName(EntityType entityType, Entity entity) {
        if (entityType == null) {
            return text("Safari Net", NamedTextColor.AQUA, TextDecoration.BOLD);
        }

        String entityName;
        if (entity != null) {
            Component customName = entity.customName();
            if (customName instanceof TextComponent textComponent) {
                entityName = textComponent.content();
            } else {
                entityName = formatEntityName(entityType);
            }
        } else {
            entityName = formatEntityName(entityType);
        }

        return text("Safari Net ", NamedTextColor.AQUA, TextDecoration.BOLD)
                .append(text("(", NamedTextColor.GRAY))
                .append(text(entityName, NamedTextColor.YELLOW))
                .append(text(")", NamedTextColor.GRAY));
    }

    /**
     * Compute the lore for the Safari Net.
     */
    private List<Component> computeLore(EntityType entityType, Entity entity) {
        List<Component> lore = new ArrayList<>();

        if (entityType == null) {
            lore.add(text("A mystical net that can capture creatures.", NamedTextColor.GRAY));
            lore.add(text(""));
            lore.add(text("Throw to catch entities!", NamedTextColor.GOLD));
            lore.add(text("25% success rate", NamedTextColor.GRAY));
        } else {
            String entityName;
            if (entity != null) {
                Component customName = entity.customName();
                if (customName instanceof TextComponent textComponent) {
                    entityName = textComponent.content();
                } else {
                    entityName = formatEntityName(entityType);
                }
            } else {
                entityName = formatEntityName(entityType);
            }

            lore.add(text("Contains: ", NamedTextColor.GRAY).append(text(entityName, NamedTextColor.YELLOW)));
            lore.add(text(""));
            lore.add(text("Right-click to release!", NamedTextColor.GOLD));
        }

        return lore;
    }

    /**
     * Format entity type name to be more readable.
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
