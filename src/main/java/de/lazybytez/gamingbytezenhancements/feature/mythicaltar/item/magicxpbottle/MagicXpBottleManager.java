package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.AbstractCustomItemManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class MagicXpBottleManager extends AbstractCustomItemManager {

    public static final String PDC_KEY_EXPERIENCE_GEM = "gamingbytez-magic-xp-bottle";

    public MagicXpBottleManager(Plugin plugin) {
        super(plugin, Material.OMINOUS_BOTTLE, MagicXpBottleManager.PDC_KEY_EXPERIENCE_GEM);
    }

    @Override
    protected ItemMeta configureItemMeta(ItemMeta itemMeta) {
        itemMeta.customName(text("Magic XP Bottle", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD));

        itemMeta.lore(this.computeLore(0));
        itemMeta.setEnchantmentGlintOverride(true);

        return itemMeta;
    }

    /**
     * Compute the lore for the given level.
     */
    private List<Component> computeLore(int level) {
        return List.of(
                text("A magic bottle that somehow manages to store huge amounts of experience.", NamedTextColor.GOLD),
                text(""),
                text("Current Level: ", NamedTextColor.GRAY).append(text(level, TextColor.color(255, 170, 0))),
                text(""),
                text("Right click to consume up to 5 levels.", NamedTextColor.GRAY),
                text("Recharge the bottle at the Mythic Altar.")
        );
    }
}
