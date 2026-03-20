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
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class ExperienceGemManager extends AbstractCustomItemManager {

    public static final String PDC_KEY_EXPERIENCE_GEM = "gamingbytez-experience-gem";

    public ExperienceGemManager(Plugin plugin) {
        super(plugin, Material.DIAMOND, ExperienceGemManager.PDC_KEY_EXPERIENCE_GEM);
    }

    @Override
    protected ItemMeta configureItemMeta(ItemMeta itemMeta) {
        itemMeta.customName(text("Experience Gem", NamedTextColor.GOLD, TextDecoration.BOLD));
        itemMeta.lore(List.of(text("A gem holding the life energy of centuries.", NamedTextColor.GRAY)));
        itemMeta.setEnchantmentGlintOverride(true);

        return itemMeta;
    }
}
