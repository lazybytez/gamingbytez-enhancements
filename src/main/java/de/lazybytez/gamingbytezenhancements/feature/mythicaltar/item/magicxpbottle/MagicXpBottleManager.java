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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.AbstractCustomItemManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class MagicXpBottleManager extends AbstractCustomItemManager {
    public static final String PDC_KEY_EXPERIENCE_GEM = "gamingbytez-magic-xp-bottle";
    public static final String PDC_KEY_EXPERIENCE = "gamingbytez-magic-xp-bottle-experience";

    private NamespacedKey experiencePdcKey;

    public MagicXpBottleManager(Plugin plugin) {
        super(plugin, Material.EXPERIENCE_BOTTLE, MagicXpBottleManager.PDC_KEY_EXPERIENCE_GEM);
    }

    /**
     * Add experience to the given magic xp bottle.
     */
    public void addExperience(ItemStack magicXpBottle, int experience) {
        int currentExperience = magicXpBottle.getPersistentDataContainer().getOrDefault(
                this.getExperiencePdcKey(),
                PersistentDataType.INTEGER,
                0
        );
        int newExperience = currentExperience + experience;

        magicXpBottle.editPersistentDataContainer(pdc -> {
            pdc.set(this.getExperiencePdcKey(), PersistentDataType.INTEGER, newExperience);
        });

        ItemMeta itemMeta = magicXpBottle.getItemMeta();
        itemMeta.lore(this.computeLore(newExperience));
        magicXpBottle.setItemMeta(itemMeta);
    }

    /**
     * Remove experience from the given magic xp bottle.
     */
    public void removeExperience(ItemStack magicXpBottle, int experience) {
        int currentExperience = magicXpBottle.getPersistentDataContainer().getOrDefault(
                this.getExperiencePdcKey(),
                PersistentDataType.INTEGER,
                0
        );
        int newExperience = Math.max(currentExperience - experience, 0);

        magicXpBottle.editPersistentDataContainer(pdc -> {
            pdc.set(
                    this.getExperiencePdcKey(),
                    PersistentDataType.INTEGER,
                    newExperience
            );
        });

        ItemMeta itemMeta = magicXpBottle.getItemMeta();
        itemMeta.lore(this.computeLore(newExperience));
        magicXpBottle.setItemMeta(itemMeta);
    }

    /**
     * Get experience stored in the given magic xp bottle.
     */
    public int getExperience(ItemStack magicXpBottle) {
        return magicXpBottle.getPersistentDataContainer().getOrDefault(
                this.getExperiencePdcKey(),
                PersistentDataType.INTEGER,
                0
        );
    }

    @Override
    protected ItemMeta configureItemMeta(ItemMeta itemMeta) {
        itemMeta.customName(text("Magic XP Bottle", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD));

        itemMeta.lore(this.computeLore(0));
        itemMeta.setEnchantmentGlintOverride(true);
        itemMeta.setMaxStackSize(1);

        return itemMeta;
    }

    /**
     * Compute the lore for the given level.
     */
    private List<Component> computeLore(int experience) {
        return List.of(
                text("A magic bottle that somehow manages to store huge amounts of experience.", NamedTextColor.GOLD),
                text(""),
                text("Current Experience: ", NamedTextColor.GRAY).append(text(experience, TextColor.color(255, 170, 0))),
                text(""),
                text("Right click to consume up to 450 experience.", NamedTextColor.GRAY),
                text("Recharge the bottle at the Mythic Altar.")
        );
    }

    @Override
    public boolean isCustomItem(ItemStack item) {
        ItemStack template = this.createCustomItem();

        return item.getType().equals(template.getType())
                && item.getPersistentDataContainer().getOrDefault(
                this.getPdcKey(),
                PersistentDataType.BOOLEAN,
                false
        );
    }

    private NamespacedKey getExperiencePdcKey() {
        if (this.experiencePdcKey == null) {
            this.experiencePdcKey = new NamespacedKey(this.plugin, MagicXpBottleManager.PDC_KEY_EXPERIENCE);
        }

        return this.experiencePdcKey;
    }
}
