package de.lazybytez.gamingbytezenhancements.feature.mythicaltar;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.AbstractFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.MythicAltar;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.AltarCraftingListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.magicxpbottle.DropEssenceOfSpawnerOnSpawnerBreakListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.CustomItemManagerRegistry;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle.EssenceOfSpawnerManager;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle.ExperienceGemManager;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle.MagicXpBottleManager;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.CompletableRecipeRegistry;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.CompletableRecipeRegistryInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.schema.validator.SimpleAltarSchemaValidator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Feature that provides a new crafting altar to do some special stuff.
 */
public class MythicAltarFeature extends AbstractFeature {
    public static final Component CHAT_MESSAGE_PREFIX = Component.text("[", NamedTextColor.DARK_GRAY)
            .append(Component.text("MythicAltar", NamedTextColor.GOLD))
            .append(Component.text("] ", NamedTextColor.DARK_GRAY));

    private final CompletableRecipeRegistryInterface recipeRegistry;
    private final CustomItemManagerRegistry customItemManagerRegistry;

    public MythicAltarFeature(EnhancementsPlugin plugin) {
        super(plugin);

        this.recipeRegistry = new CompletableRecipeRegistry();
        this.customItemManagerRegistry = new CustomItemManagerRegistry();
    }

    @Override
    public void onEnable() {
        this.registerRecipes();
        this.registerCustomItemManagers();
        this.registerEvents();
    }

    private void registerRecipes() {
        this.recipeRegistry.registerRecipes(MythicAltar.getDefaultRecipes(this));
    }

    private void registerCustomItemManagers() {
        // Magic XP Bottle
        this.customItemManagerRegistry.registerCustomItemManager(new EssenceOfSpawnerManager(this.plugin));
        this.customItemManagerRegistry.registerCustomItemManager(new ExperienceGemManager(this.plugin));
        this.customItemManagerRegistry.registerCustomItemManager(new MagicXpBottleManager(this.plugin));
    }

    private void registerEvents() {
        // Base Feature
        this.registerEvent(new AltarCraftingListener(
                this.plugin,
                new SimpleAltarSchemaValidator(),
                this.recipeRegistry
        ));

        // Magic XP Bottle
        this.registerEvent(new DropEssenceOfSpawnerOnSpawnerBreakListener(this));
    }

    public CustomItemManagerRegistry getCustomItemManagerRegistry() {
        return customItemManagerRegistry;
    }

    @Override
    public String getName() {
        return "MythicAltar";
    }
}
