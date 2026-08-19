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
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastBlockFilter;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastBudget;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastScheduler;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.ExcavationChargeGravity;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.AltarCraftingListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.excavationcharge.CollectExcavationChargeListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.excavationcharge.CycleExcavationChargeShapeListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.excavationcharge.DetonateExcavationChargeListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.excavationcharge.PlaceExcavationChargeListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.excavationcharge.RedstoneIgniteExcavationChargeListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.magicxpbottle.DropEssenceOfSpawnerOnSpawnerBreakListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.magicxpbottle.UseMagicXpBottleOnClickListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.safarinet.SafariNetCatchEntityListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.safarinet.SafariNetPickupListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.safarinet.SafariNetReleaseEntityListener;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.CustomItemManagerRegistry;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.excavationcharge.ExcavationChargeManager;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle.EssenceOfSpawnerManager;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle.ExperienceGemManager;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle.MagicXpBottleManager;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.safarinet.SafariNetManager;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.CompletableRecipeRegistry;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.CompletableRecipeRegistryInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.schema.validator.SimpleAltarSchemaValidator;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePrefix;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import net.kyori.adventure.text.format.NamedTextColor;

/**
 * Feature that provides a new crafting altar to do some special stuff.
 */
public class MythicAltarFeature extends AbstractFeature {
    private static final String FEATURE_NAME = "MythicAltar";
    private static final NamedTextColor BRAND_COLOR = NamedTextColor.GOLD;

    private final CompletableRecipeRegistryInterface recipeRegistry;
    private final CustomItemManagerRegistry customItemManagerRegistry;

    /**
     * The messenger carrying the Mythic Altar prefix, shared by every
     * player facing component this feature sends.
     */
    private final Messenger messenger;

    private BlastScheduler blastScheduler;
    private ExcavationChargeGravity chargeGravity;

    public MythicAltarFeature(EnhancementsPlugin plugin) {
        super(plugin);

        this.recipeRegistry = new CompletableRecipeRegistry();
        this.customItemManagerRegistry = new CustomItemManagerRegistry();
        this.messenger = new Messenger(MessagePrefix.of(
                MythicAltarFeature.FEATURE_NAME, MythicAltarFeature.BRAND_COLOR));
    }

    @Override
    public void onEnable() {
        this.blastScheduler = new BlastScheduler(this.plugin, BlastBlockFilter.production(), new BlastBudget());
        this.chargeGravity = new ExcavationChargeGravity(this.plugin);
        this.chargeGravity.start();

        this.registerRecipes();
        this.registerCustomItemManagers();
        this.registerEvents();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Carves every Excavation Charge blast still in flight synchronously, so a server stop never
     * leaves a half carved crater in the world.
     */
    @Override
    public void onDisable() {
        if (this.chargeGravity != null) {
            this.chargeGravity.stop();
        }

        if (this.blastScheduler == null) {
            return;
        }

        this.blastScheduler.shutdown();
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

        // Excavation Charge
        this.customItemManagerRegistry.registerCustomItemManager(new ExcavationChargeManager(this.plugin));
    }

    private void registerEvents() {
        // Base Feature
        this.registerEvent(new AltarCraftingListener(
                this.plugin,
                new SimpleAltarSchemaValidator(),
                this.recipeRegistry,
                this.messenger
        ));

        // Magic XP Bottle
        this.registerEvent(new DropEssenceOfSpawnerOnSpawnerBreakListener(this));
        this.registerEvent(new UseMagicXpBottleOnClickListener(this, this.messenger));

        // Safari Net
        this.registerEvent(new SafariNetCatchEntityListener(this, this.plugin));
        this.registerEvent(new SafariNetReleaseEntityListener(this, this.messenger));
        this.registerEvent(new SafariNetPickupListener(this));

        // Excavation Charge
        DetonateExcavationChargeListener detonation =
                new DetonateExcavationChargeListener(this, this.blastScheduler, this.messenger);
        this.registerEvent(new CycleExcavationChargeShapeListener(this, this.messenger));
        this.registerEvent(new PlaceExcavationChargeListener(this));
        this.registerEvent(new CollectExcavationChargeListener(this));
        this.registerEvent(detonation);
        this.registerEvent(new RedstoneIgniteExcavationChargeListener(detonation));
    }

    public CustomItemManagerRegistry getCustomItemManagerRegistry() {
        return customItemManagerRegistry;
    }

    /**
     * Get the messenger every player facing part of this feature sends through.
     *
     * @return the messenger bound to the Mythic Altar prefix
     */
    public Messenger getMessenger() {
        return this.messenger;
    }

    @Override
    public String getName() {
        return "MythicAltar";
    }
}
