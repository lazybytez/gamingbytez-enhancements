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
