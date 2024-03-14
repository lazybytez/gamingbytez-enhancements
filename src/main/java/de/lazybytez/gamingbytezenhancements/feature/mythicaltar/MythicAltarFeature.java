package de.lazybytez.gamingbytezenhancements.feature.mythicaltar;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.AbstractFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.MythicAltar;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.AltarCraftingListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.CompletableRecipeRegistry;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.CompletableRecipeRegistryInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.schema.validator.SimpleAltarSchemaValidator;

/**
 * Feature that provides a new crafting altar to do some special stuff.
 */
public class MythicAltarFeature extends AbstractFeature {
    private CompletableRecipeRegistryInterface recipeRegistry;

    public MythicAltarFeature(EnhancementsPlugin plugin) {
        super(plugin);

        this.recipeRegistry = new CompletableRecipeRegistry();
    }

    @Override
    public void onEnable() {
        this.registerRecipes();
        this.registerEvents();
    }

    private void registerRecipes() {
        this.recipeRegistry.registerRecipes(MythicAltar.getDefaultRecipes());
    }

    private void registerEvents() {
        this.registerEvent(new AltarCraftingListener(new SimpleAltarSchemaValidator(), this.recipeRegistry));
    }

    @Override
    public String getName() {
        return "MythicAltar";
    }
}
