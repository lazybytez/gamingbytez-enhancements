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

        double baseDamage = e.getDamage();
        double intendedDamage = this.armorBasedCreeperDamageCalculator.calculateDamage(
                p.getEquipment().getArmorContents(),
                armorPointAttribute == null ? 0.0 : armorPointAttribute.getValue(),
                armorToughnessAttribute == null ? 0.0 : armorToughnessAttribute.getValue(),
                baseDamage
        );

        e.setDamage(CreeperDamageListener.baseDealing(intendedDamage, baseDamage, e.getFinalDamage()));
    }

    /**
     * Works out the base damage that leaves the player taking the damage this feature intends.
     * <p>
     * The damage set here is the blast before armour, and the server still takes armour, protection
     * and effects off it afterwards. This feature already reads armour to decide how hard a creeper
     * hits, so letting the server subtract it a second time counted it twice and flattened the whole
     * scale: the hardest hit this feature can produce arrived as an ordinary one, and the occasional
     * one shot it exists for could never land.
     * <p>
     * The reduction the server is about to apply is measured from the damage as it stands, and the
     * base is raised by the same proportion, so the number that reaches the player is the number
     * this feature chose. A player wearing nothing has no reduction to undo and is left alone.
     *
     * @param intendedDamage The damage the player should end up taking.
     * @param baseDamage     The blast damage before any reduction.
     * @param finalDamage    The damage the player would take if this feature changed nothing.
     * @return The base damage to set on the event.
     */
    static double baseDealing(double intendedDamage, double baseDamage, double finalDamage) {
        if (baseDamage <= 0.0 || finalDamage <= 0.0) {
            return intendedDamage;
        }

        return intendedDamage * (baseDamage / finalDamage);
    }
}
