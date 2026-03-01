package de.lazybytez.gamingbytezenhancements.feature.customloot.service;

import org.bukkit.damage.DamageSource;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantmentLevelOnItemDeterminer {

    /**
     * Return the enchantment level if the direct damage is done with a weapon with the enchantment.
     * Return 0 otherwise.
     *
     * @return Level of the enchantment on the weapon
     */
    public int getEnchantmentLevelOnMeleeWeapon(EntityDeathEvent event, Enchantment enchantment) {
        DamageSource damageSource = event.getDamageSource();
        if (damageSource.isIndirect()) {
            return 0;
        }

        Entity directEntity = damageSource.getDirectEntity();

        if ( !(directEntity instanceof Player)) {
            return 0;
        }

        ItemStack itemInMainHand = ((Player) directEntity).getInventory().getItemInMainHand();

        return itemInMainHand.getEnchantmentLevel(enchantment);
    }
}
