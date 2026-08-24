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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CreeperDamageListener implements Listener {
    private static final int EXPANSION_STEPS = 12;
    private static final int REFINEMENT_STEPS = 20;

    private final ArmorBasedCreeperDamageCalculator armorBasedCreeperDamageCalculator;

    public CreeperDamageListener(ArmorBasedCreeperDamageCalculator armorBasedCreeperDamageCalculator) {
        this.armorBasedCreeperDamageCalculator = armorBasedCreeperDamageCalculator;
    }

    @EventHandler
    public void onCreeperDamagePlayer(EntityDamageByEntityEvent e) {
        if (!e.getDamager().getType().equals(EntityType.CREEPER)) {
            return;
        }

        if (!e.getEntity().getType().equals(EntityType.PLAYER)) {
            return;
        }

        Player p = (Player) e.getEntity();

        AttributeInstance armorPointAttribute = p.getAttribute(Attribute.ARMOR);
        AttributeInstance armorToughnessAttribute = p.getAttribute(Attribute.ARMOR_TOUGHNESS);

        double intendedDamage = this.armorBasedCreeperDamageCalculator.calculateDamage(
                p.getEquipment().getArmorContents(),
                armorPointAttribute == null ? 0.0 : armorPointAttribute.getValue(),
                armorToughnessAttribute == null ? 0.0 : armorToughnessAttribute.getValue(),
                this.resistanceLevel(p),
                e.getDamage()
        );

        this.applyAsFinalDamage(e, Math.max(0.0, intendedDamage - p.getAbsorptionAmount()));
    }

    /**
     * Sets the base damage whose reduction leaves the player losing the intended health.
     * <p>
     * The event carries damage before reduction and the server recomputes every modifier against
     * whatever base it is given, so no fixed ratio converts one into the other: armor sheds a
     * smaller share of a large hit than of a small one. The base is searched for instead, by
     * widening a bracket until it spans the intended damage and then halving it, which asks the
     * server what it would deal rather than modelling what it would deal.
     * <p>
     * The search targets health lost, so absorption is taken off the intended damage by the caller
     * rather than being solved away.
     *
     * @param event          The damage event to write the base damage to.
     * @param intendedDamage The health the player should lose.
     */
    void applyAsFinalDamage(EntityDamageEvent event, double intendedDamage) {
        double low = 0.0;
        double high = intendedDamage;

        for (int step = 0; step < CreeperDamageListener.EXPANSION_STEPS; step++) {
            if (this.finalDamageFor(event, high) >= intendedDamage) {
                break;
            }

            low = high;
            high *= 2.0;
        }

        for (int step = 0; step < CreeperDamageListener.REFINEMENT_STEPS; step++) {
            double middle = (low + high) / 2.0;

            if (this.finalDamageFor(event, middle) < intendedDamage) {
                low = middle;

                continue;
            }

            high = middle;
        }

        event.setDamage(high);
    }

    private double finalDamageFor(EntityDamageEvent event, double baseDamage) {
        event.setDamage(baseDamage);

        return event.getFinalDamage();
    }

    private int resistanceLevel(Player player) {
        PotionEffect resistance = player.getPotionEffect(PotionEffectType.RESISTANCE);

        if (resistance == null) {
            return 0;
        }

        return resistance.getAmplifier() + 1;
    }
}
