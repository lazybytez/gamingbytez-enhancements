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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.excavationcharge;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.blast.BlastShape;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.excavationcharge.ExcavationChargeManager;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePalette;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Listener that cycles the blast shape of an Excavation Charge held in hand.
 * <p>
 * Cycling is the sneaking right click. Placing is the same click without sneaking and belongs to
 * {@link PlaceExcavationChargeListener}, so this listener returns as soon as the player is not sneaking
 * and the two never both act on one click.
 */
public class CycleExcavationChargeShapeListener implements Listener {

    private final MythicAltarFeature mythicAltarFeature;
    private final Messenger messenger;

    public CycleExcavationChargeShapeListener(MythicAltarFeature mythicAltarFeature, Messenger messenger) {
        this.mythicAltarFeature = Objects.requireNonNull(
                mythicAltarFeature, "mythicAltarFeature must not be null");
        this.messenger = Objects.requireNonNull(messenger, "messenger must not be null");
    }

    /**
     * Advance the blast shape of the Excavation Charge held in the main hand.
     * <p>
     * The event is cancelled so the click neither places the charge nor triggers any vanilla use
     * of the end crystal.
     *
     * @param event The player interact event
     */
    @EventHandler
    public void onCycleExcavationChargeShape(PlayerInteractEvent event) {
        // Not ignoreCancelled: a right click into the air arrives as a cancelled event by
        // definition, because the interact event's cancelled state is its use-block result and
        // there is no block. Cycling changes only the held item, so cancelled events are fine.
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (!CycleExcavationChargeShapeListener.isRightClick(event.getAction())) {
            return;
        }

        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.END_CRYSTAL) {
            return;
        }

        ExcavationChargeManager excavationChargeManager = this.mythicAltarFeature
                .getCustomItemManagerRegistry()
                .getCustomItemManager(ExcavationChargeManager.class);

        if (!excavationChargeManager.isCustomItem(item)) {
            return;
        }

        event.setCancelled(true);

        BlastShape nextShape = excavationChargeManager.getShape(item).next();
        excavationChargeManager.setShape(item, nextShape);

        this.announceShape(player, nextShape);
    }

    /**
     * Tell the player which shape the charge now carries.
     *
     * @param player    The player holding the charge
     * @param nextShape The shape the charge was advanced to
     */
    private void announceShape(Player player, BlastShape nextShape) {
        // Deliberately unprefixed: the action bar is a single transient line above the hotbar,
        // and the owner decided the bracket prefix is noise there rather than attribution.
        player.sendActionBar(
                Component.text("Shape: ", MessagePalette.BODY)
                        .append(Component.text(nextShape.getDisplayName(), MessagePalette.VALUE))
        );

        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.6f, 1.4f);
    }

    /**
     * Check whether the action is a right click, against air or against a block.
     *
     * @param action The player action
     * @return True if the action is a right click
     */
    private static boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }
}
