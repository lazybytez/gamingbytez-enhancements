package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

/**
 * Abstract class that all custom items used within the Altar feature extend.
 * <p>
 * This abstraction handles primarily the persistent data container flag used to identify
 * our specific items.
 */
public abstract class AbstractCustomItemManager {

    private final Plugin plugin;

    private final Material material;
    private final String pdcKey;

    private NamespacedKey namespacedKey;

    public AbstractCustomItemManager(Plugin plugin, Material material, String pdcKey) {
        this.plugin = plugin;
        this.material = material;
        this.pdcKey = pdcKey;
    }

    /**
     * Create an instance of the custom item defined in this service.
     */
    public ItemStack createCustomItem() {
        ItemStack item = ItemStack.of(this.material);

        item.setItemMeta(this.configureItemMeta(item.getItemMeta()));

        item.editPersistentDataContainer(pdc -> {
            pdc.set(this.getPdcKey(), PersistentDataType.BOOLEAN, true);
        });

        return item;
    }

    /**
     * Customize item name, lore, enchanting glint, ...
     */
    abstract protected ItemMeta configureItemMeta(ItemMeta itemMeta);

    /**
     * Check whether the given item is a custom item.
     */
    public boolean isCustomItem(ItemStack item) {
        ItemStack template = this.createCustomItem();

        return item.isSimilar(template)
                && item.getPersistentDataContainer().getOrDefault(
                this.getPdcKey(),
                PersistentDataType.BOOLEAN,
                false
        );
    }

    public NamespacedKey getPdcKey() {
        if (this.namespacedKey == null) {
            this.namespacedKey = new NamespacedKey(this.plugin, this.pdcKey);

            return this.namespacedKey;
        }

        return this.namespacedKey;
    }
}
