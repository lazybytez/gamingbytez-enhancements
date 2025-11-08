package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.event.magicxpbottle;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.item.magicxpbottle.MagicXpBottleManager;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.textOfChildren;

/**
 * Listener that implements that the magic xp bottle can be used to spawn experience orbs.
 */
public class UseMagicXpBottleOnClickListener implements Listener {

    public static final int MAGIC_BOTTLE_XP_DROP = 450;
    public static final int MAGIC_BOTTLE_XP_COOLDOWN = 500;

    private final MythicAltarFeature mythicAltarFeature;

    private final Map<UUID, Long> playerLastMagicXpBottleUse;

    public UseMagicXpBottleOnClickListener(MythicAltarFeature mythicAltarFeature) {
        this.mythicAltarFeature = mythicAltarFeature;
        this.playerLastMagicXpBottleUse = new ConcurrentHashMap<>();
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (!event.hasItem()) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        // This is also checked by the magic xp bottle manager, but this check is less expensive
        if (!item.getType().equals(Material.EXPERIENCE_BOTTLE)) {
            return;
        }

        Player player = event.getPlayer();
        MagicXpBottleManager magicXpBottleManager = this.mythicAltarFeature
                .getCustomItemManagerRegistry()
                .getCustomItemManager(MagicXpBottleManager.class);
        if (!magicXpBottleManager.isCustomItem(item)) {
            return;
        }

        event.setCancelled(true);

        // Handle cooldown
        if (this.playerLastMagicXpBottleUse.containsKey(player.getUniqueId())) {
            if ((System.currentTimeMillis() - this.playerLastMagicXpBottleUse.get(player.getUniqueId())) > UseMagicXpBottleOnClickListener.MAGIC_BOTTLE_XP_COOLDOWN) {
                this.playerLastMagicXpBottleUse.remove(player.getUniqueId());
            }

            return;
        }
        this.playerLastMagicXpBottleUse.put(player.getUniqueId(), System.currentTimeMillis());

        int currentXp = magicXpBottleManager.getExperience(item);
        if (currentXp == 0) {
            player.sendMessage(textOfChildren(
                    MythicAltarFeature.CHAT_MESSAGE_PREFIX,
                    text("You have no XP left in your magic XP bottle!", NamedTextColor.RED)));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);

            this.playerLastMagicXpBottleUse.put(player.getUniqueId(), System.currentTimeMillis());

            return;
        }

        player.getWorld().spawnEntity(
                player.getLocation(),
                EntityType.EXPERIENCE_ORB,
                CreatureSpawnEvent.SpawnReason.CUSTOM,
                entity -> {
                    if (!(entity instanceof ExperienceOrb)) {
                        return;
                    }


                    ((ExperienceOrb) entity).setExperience(
                            Math.min(UseMagicXpBottleOnClickListener.MAGIC_BOTTLE_XP_DROP, currentXp)
                    );
                }
        );
        magicXpBottleManager.removeExperience(item, UseMagicXpBottleOnClickListener.MAGIC_BOTTLE_XP_DROP);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!this.playerLastMagicXpBottleUse.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }

        this.playerLastMagicXpBottleUse.remove(event.getPlayer().getUniqueId());
    }
}
