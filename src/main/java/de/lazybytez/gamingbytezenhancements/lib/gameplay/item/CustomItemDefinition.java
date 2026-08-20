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
package de.lazybytez.gamingbytezenhancements.lib.gameplay.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

/**
 * The presentation of a custom item, described once and written onto any stack that should wear it.
 * <p>
 * Every property is optional and an unset one writes no component at all, because an absent
 * component and a component holding a default are two different items in game: empty lore renders
 * an empty line and a glint override of {@code false} suppresses the glint an enchantment would
 * otherwise give. A builder carries that three-state meaning where a record cannot, since a record
 * would have to expose a canonical constructor whose four null arguments are the only way to say
 * "leave this alone".
 * <p>
 * The definition is immutable, so one instance describes a whole item kind and can be shared by
 * every stack of it.
 */
public final class CustomItemDefinition {

    private final Component name;

    private final List<Component> lore;

    private final Boolean enchantmentGlintOverride;

    private final Integer maxStackSize;

    private CustomItemDefinition(Builder builder) {
        this.name = builder.name;
        this.lore = builder.lore;
        this.enchantmentGlintOverride = builder.enchantmentGlintOverride;
        this.maxStackSize = builder.maxStackSize;
    }

    /**
     * Opens a definition that describes nothing yet.
     *
     * @return a builder holding every property unset
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Writes the described presentation onto the given stack.
     * <p>
     * Only the properties this definition carries are written, so a stack keeps whatever the
     * definition stays silent about.
     *
     * @param itemStack the stack to write the components onto
     * @throws NullPointerException when the stack is null
     */
    public void applyTo(ItemStack itemStack) {
        Objects.requireNonNull(itemStack, "itemStack must not be null");

        if (this.name != null) {
            itemStack.setData(DataComponentTypes.CUSTOM_NAME, this.name);
        }

        if (this.lore != null) {
            itemStack.setData(DataComponentTypes.LORE, ItemLore.lore(this.lore));
        }

        if (this.enchantmentGlintOverride != null) {
            itemStack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, this.enchantmentGlintOverride);
        }

        if (this.maxStackSize != null) {
            itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, this.maxStackSize);
        }
    }

    /**
     * Collects the presentation properties a custom item wears.
     * <p>
     * A property left untouched stays unset, which is what keeps the definition from writing a
     * component the caller never asked for.
     */
    public static final class Builder {

        private Component name;

        private List<Component> lore;

        private Boolean enchantmentGlintOverride;

        private Integer maxStackSize;

        private Builder() {
        }

        /**
         * Sets the name the item is displayed under.
         *
         * @param name the display name
         * @return this builder
         * @throws NullPointerException when the name is null
         */
        public Builder name(Component name) {
            this.name = Objects.requireNonNull(name, "name must not be null");

            return this;
        }

        /**
         * Sets the lore lines, in the order they are read.
         *
         * @param lore the lore lines
         * @return this builder
         * @throws NullPointerException when the lines are null or contain a null line
         */
        public Builder lore(List<Component> lore) {
            Objects.requireNonNull(lore, "lore must not be null");
            this.lore = List.copyOf(lore);

            return this;
        }

        /**
         * Forces the enchantment glint on or off.
         *
         * @param enchantmentGlintOverride whether the item glints
         * @return this builder
         */
        public Builder enchantmentGlintOverride(boolean enchantmentGlintOverride) {
            this.enchantmentGlintOverride = enchantmentGlintOverride;

            return this;
        }

        /**
         * Caps how many of the item share a slot.
         *
         * @param maxStackSize the largest stack the item forms
         * @return this builder
         */
        public Builder maxStackSize(int maxStackSize) {
            this.maxStackSize = maxStackSize;

            return this;
        }

        /**
         * Freezes the collected properties into a definition.
         *
         * @return the immutable definition
         */
        public CustomItemDefinition build() {
            return new CustomItemDefinition(this);
        }
    }
}
