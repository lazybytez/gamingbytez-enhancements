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

    protected final Plugin plugin;

    protected final Material material;

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

    protected NamespacedKey getPdcKey() {
        if (this.namespacedKey == null) {
            this.namespacedKey = new NamespacedKey(this.plugin, this.pdcKey);
        }

        return this.namespacedKey;
    }
}
