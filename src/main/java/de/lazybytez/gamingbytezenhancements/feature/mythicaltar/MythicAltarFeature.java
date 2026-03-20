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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar;

import de.lazybytez.gamingbytezenhancements.EnhancementsPlugin;
import de.lazybytez.gamingbytezenhancements.feature.AbstractFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.MythicAltar;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.AltarCraftingListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.magicxpbottle.DropEssenceOfSpawnerOnSpawnerBreakListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.magicxpbottle.UseMagicXpBottleOnClickListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.safarinet.SafariNetCatchEntityListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.safarinet.SafariNetPickupListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.safarinet.SafariNetReleaseEntityListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.CustomItemManagerRegistry;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle.EssenceOfSpawnerManager;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle.ExperienceGemManager;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle.MagicXpBottleManager;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.safarinet.SafariNetManager;
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

        // Safari Net
        this.customItemManagerRegistry.registerCustomItemManager(new SafariNetManager(this.plugin));
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
        this.registerEvent(new UseMagicXpBottleOnClickListener(this));

        // Safari Net
        this.registerEvent(new SafariNetCatchEntityListener(this, this.plugin));
        this.registerEvent(new SafariNetReleaseEntityListener(this));
        this.registerEvent(new SafariNetPickupListener(this));
    }

    public CustomItemManagerRegistry getCustomItemManagerRegistry() {
        return customItemManagerRegistry;
    }

    @Override
    public String getName() {
        return "MythicAltar";
    }
}
