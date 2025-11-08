package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.AbstractCustomItemManager;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class EssenceOfSpawnerManager extends AbstractCustomItemManager {

    public static final String PDC_KEY_ESSENCE_OF_SPAWNER = "gamingbytez-essence-of-spawner";

    public EssenceOfSpawnerManager(Plugin plugin) {
        super(plugin, Material.GRAY_DYE, EssenceOfSpawnerManager.PDC_KEY_ESSENCE_OF_SPAWNER);
    }

    @Override
    protected ItemMeta configureItemMeta(ItemMeta itemMeta) {
        itemMeta.customName(text("Essence of Spawner", NamedTextColor.GOLD, TextDecoration.BOLD));
        itemMeta.lore(List.of(text("A powder emitting a strong lively aura.", NamedTextColor.GRAY)));
        itemMeta.setEnchantmentGlintOverride(true);

        return itemMeta;
    }
}
