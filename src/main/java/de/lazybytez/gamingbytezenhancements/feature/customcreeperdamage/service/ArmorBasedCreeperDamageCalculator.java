package de.lazybytez.gamingbytezenhancements.feature.customcreeperdamage.service;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class ArmorBasedCreeperDamageCalculator {
    private final Random random = new Random();

    public double calculateDamage(
            ItemStack[] equipment,
            double armorPoints,
            double armorToughness,
            double baseDamage
    ) {
        double armorFactor = (armorPoints + armorToughness + this.calculateEnchantmentFactor(equipment)) / 5;
        if (armorFactor == 0) {
            armorFactor = 1;
        }

        double adjustedArmorFactor = (armorFactor > 0 ? armorFactor : 1) / 10;

        double luck = this.random.nextDouble(0.5, 2);
        double armorLuckFactor = luck * adjustedArmorFactor;

        double adjustedDamage = baseDamage * armorLuckFactor;

        if (adjustedDamage < 0) {
            adjustedDamage = baseDamage;
        }

        return adjustedDamage;
    }

    private double calculateEnchantmentFactor(ItemStack[] equipment) {
        double enchantmentFactor = 0;
        for (ItemStack item : equipment) {
            if (item == null) {
                continue;
            }

            enchantmentFactor += item.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
            enchantmentFactor += item.getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS);
        }

        return enchantmentFactor;
    }
}
