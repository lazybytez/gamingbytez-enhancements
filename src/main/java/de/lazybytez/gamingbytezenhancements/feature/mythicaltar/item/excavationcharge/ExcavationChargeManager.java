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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.excavationcharge;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastLevel;
import de.lazybytez.gamingbytezenhancements.lib.gameplay.blast.BlastShape;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.AbstractCustomItemManager;
import de.lazybytez.gamingbytezenhancements.lib.gameplay.item.CustomItemDefinition;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePalette;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

/**
 * Manager for the Excavation Charge custom item.
 * <p>
 * An Excavation Charge is a craftable explosive that carries its blast {@link BlastShape} and blast
 * level on the item's persistent data container, so every charge keeps its own state
 * independently of every other charge in a player's inventory.
 */
public class ExcavationChargeManager extends AbstractCustomItemManager {
    public static final String PDC_KEY_EXCAVATION_CHARGE = "gamingbytez-excavation-charge";
    public static final String PDC_KEY_SHAPE = "gamingbytez-excavation-charge-shape";
    public static final String PDC_KEY_LEVEL = "gamingbytez-excavation-charge-level";

    private NamespacedKey shapePdcKey;
    private NamespacedKey levelPdcKey;

    public ExcavationChargeManager(Plugin plugin) {
        super(plugin, Material.END_CRYSTAL, ExcavationChargeManager.PDC_KEY_EXCAVATION_CHARGE);
    }

    /**
     * Get the blast shape stored on the given Excavation Charge.
     * <p>
     * A charge that carries no shape, such as one written by an earlier version of the plugin,
     * reads back as {@link BlastShape#CUBOID}.
     *
     * @param excavationCharge the Excavation Charge item to read
     * @return the stored blast shape, or {@link BlastShape#CUBOID} if none is stored
     */
    public BlastShape getShape(ItemStack excavationCharge) {
        String rawShape = excavationCharge.getPersistentDataContainer().get(this.getShapePdcKey(), PersistentDataType.STRING);

        return BlastShape.decode(rawShape);
    }

    /**
     * Set the blast shape stored on the given Excavation Charge and refresh its lore.
     *
     * @param excavationCharge the Excavation Charge item to update
     * @param shape      the blast shape to store
     */
    public void setShape(ItemStack excavationCharge, BlastShape shape) {
        excavationCharge.editPersistentDataContainer(pdc ->
                pdc.set(this.getShapePdcKey(), PersistentDataType.STRING, ExcavationChargeManager.encodeShape(shape)));

        this.updateItemDisplay(excavationCharge, shape, this.getLevel(excavationCharge));
    }

    /**
     * Get the blast level stored on the given Excavation Charge.
     * <p>
     * A charge that carries no level, such as one written by an earlier version of the plugin,
     * reads back as level {@value BlastLevel#MIN_LEVEL}.
     *
     * @param excavationCharge the Excavation Charge item to read
     * @return the stored blast level, clamped into {@link BlastLevel#MIN_LEVEL}..{@link BlastLevel#MAX_LEVEL}
     */
    public int getLevel(ItemStack excavationCharge) {
        Integer rawLevel = excavationCharge.getPersistentDataContainer().get(this.getLevelPdcKey(), PersistentDataType.INTEGER);

        return ExcavationChargeManager.decodeLevel(rawLevel);
    }

    /**
     * Set the blast level stored on the given Excavation Charge and refresh its lore.
     * <p>
     * The level is clamped into {@link BlastLevel#MIN_LEVEL}..{@link BlastLevel#MAX_LEVEL} before
     * it is stored.
     *
     * @param excavationCharge the Excavation Charge item to update
     * @param level      the blast level to store
     */
    public void setLevel(ItemStack excavationCharge, int level) {
        int encodedLevel = ExcavationChargeManager.encodeLevel(level);

        excavationCharge.editPersistentDataContainer(pdc ->
                pdc.set(this.getLevelPdcKey(), PersistentDataType.INTEGER, encodedLevel));

        this.updateItemDisplay(excavationCharge, this.getShape(excavationCharge), encodedLevel);
    }

    @Override
    protected CustomItemDefinition createItemDefinition() {
        return CustomItemDefinition.builder()
                .name(this.computeDisplayName())
                .lore(this.computeLore(BlastShape.CUBOID, BlastLevel.MIN_LEVEL))
                .enchantmentGlintOverride(true)
                .maxStackSize(1)
                .build();
    }

    @Override
    public boolean isCustomItem(ItemStack item) {
        if (item == null || item.getType() != Material.END_CRYSTAL) {
            return false;
        }

        return item.getPersistentDataContainer().getOrDefault(
                this.getPdcKey(),
                PersistentDataType.BOOLEAN,
                false
        );
    }

    /**
     * Refresh the lore of the given Excavation Charge to reflect its current shape and level.
     * <p>
     * Only the lore is described, so a charge already in a player's inventory keeps the name, the
     * glint and the stack limit it was created with.
     *
     * @param excavationCharge the Excavation Charge item to update
     * @param shape      the blast shape to render
     * @param level      the blast level to render
     */
    private void updateItemDisplay(ItemStack excavationCharge, BlastShape shape, int level) {
        CustomItemDefinition.builder()
                .lore(this.computeLore(shape, level))
                .build()
                .applyTo(excavationCharge);
    }

    /**
     * Compute the display name for the Excavation Charge.
     *
     * @return the computed display name component
     */
    private Component computeDisplayName() {
        return text("Excavation Charge", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD);
    }

    /**
     * Compute the lore for the Excavation Charge, reading its size and centre damage from
     * {@link BlastLevel} rather than hardcoding them.
     *
     * @param shape the blast shape to render
     * @param level the blast level to render
     * @return the computed lore components
     */
    private List<Component> computeLore(BlastShape shape, int level) {
        BlastLevel blastLevel = BlastLevel.of(level);
        List<Component> lore = new ArrayList<>();

        lore.add(text("Shape: ", MessagePalette.BODY).append(text(shape.getDisplayName(), MessagePalette.VALUE)));
        lore.add(text("Level: ", MessagePalette.BODY).append(text(level, MessagePalette.VALUE)));
        lore.add(text(""));
        lore.add(text("Size: ", MessagePalette.BODY).append(text(blastLevel.getSize() + " blocks", MessagePalette.VALUE)));
        lore.add(text("Damage: ", MessagePalette.BODY).append(text(blastLevel.getCentreDamage(), MessagePalette.VALUE)));
        lore.add(text("Chains to every charge inside the blast.", MessagePalette.BODY));
        lore.add(text(""));
        lore.add(text("Sneak + right click to change the shape.", MessagePalette.EMPHASIS));
        lore.add(text("Place it, then hit or power it to set it off.", MessagePalette.EMPHASIS));

        return lore;
    }

    /**
     * Encode a {@link BlastShape} into the string stored in the persistent data container.
     *
     * @param shape the blast shape to encode
     * @return the encoded shape name
     */
    static String encodeShape(BlastShape shape) {
        return shape.name();
    }

    /**
     * Decode a stored level into a valid blast level, defaulting to {@link BlastLevel#MIN_LEVEL}
     * when the value is missing and clamping any out-of-range value into
     * {@link BlastLevel#MIN_LEVEL}..{@link BlastLevel#MAX_LEVEL}.
     *
     * @param rawLevel the raw integer read from the persistent data container, may be null
     * @return the decoded, clamped blast level
     */
    static int decodeLevel(Integer rawLevel) {
        int level = rawLevel != null ? rawLevel : BlastLevel.MIN_LEVEL;

        return Math.clamp(level, BlastLevel.MIN_LEVEL, BlastLevel.MAX_LEVEL);
    }

    /**
     * Encode a level into the value stored in the persistent data container, clamping it into
     * {@link BlastLevel#MIN_LEVEL}..{@link BlastLevel#MAX_LEVEL}.
     *
     * @param level the blast level to encode
     * @return the encoded, clamped level
     */
    static int encodeLevel(int level) {
        return Math.clamp(level, BlastLevel.MIN_LEVEL, BlastLevel.MAX_LEVEL);
    }

    private NamespacedKey getShapePdcKey() {
        if (this.shapePdcKey == null) {
            this.shapePdcKey = new NamespacedKey(this.plugin, ExcavationChargeManager.PDC_KEY_SHAPE);
        }

        return this.shapePdcKey;
    }

    private NamespacedKey getLevelPdcKey() {
        if (this.levelPdcKey == null) {
            this.levelPdcKey = new NamespacedKey(this.plugin, ExcavationChargeManager.PDC_KEY_LEVEL);
        }

        return this.levelPdcKey;
    }
}
