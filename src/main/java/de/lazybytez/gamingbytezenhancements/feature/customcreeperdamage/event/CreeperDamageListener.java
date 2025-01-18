package de.lazybytez.gamingbytezenhancements.feature.customcreeperdamage.event;

import de.lazybytez.gamingbytezenhancements.feature.customcreeperdamage.service.ArmorBasedCreeperDamageCalculator;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CreeperDamageListener implements Listener {
    private final ArmorBasedCreeperDamageCalculator armorBasedCreeperDamageCalculator;

    public CreeperDamageListener(ArmorBasedCreeperDamageCalculator armorBasedCreeperDamageCalculator) {
        this.armorBasedCreeperDamageCalculator = armorBasedCreeperDamageCalculator;
    }

    @EventHandler
    public void onCreeperDamagePlayer(EntityDamageByEntityEvent e) {
        if (!e.getDamager().getType().equals(org.bukkit.entity.EntityType.CREEPER)) {
            return;
        }

        if (!e.getEntity().getType().equals(org.bukkit.entity.EntityType.PLAYER)) {
            return;
        }

        Player p = (Player) e.getEntity();

        AttributeInstance armorPointAttribute = p.getAttribute(Attribute.ARMOR);
        AttributeInstance armorToughnessAttribute = p.getAttribute(Attribute.ARMOR_TOUGHNESS);

        e.setDamage(this.armorBasedCreeperDamageCalculator.calculateDamage(
                p.getEquipment().getArmorContents(),
                armorPointAttribute == null ? 0.0 : armorPointAttribute.getValue(),
                armorToughnessAttribute == null ? 0.0 : armorToughnessAttribute.getValue(),
                e.getDamage()
        ));
    }
}
