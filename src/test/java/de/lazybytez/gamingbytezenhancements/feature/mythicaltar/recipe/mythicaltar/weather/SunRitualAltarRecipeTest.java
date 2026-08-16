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
package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.recipe.mythicaltar.weather;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.MythicAltarFeature;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.PedestalLocation;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePalette;
import de.lazybytez.gamingbytezenhancements.lib.message.MessagePrefix;
import de.lazybytez.gamingbytezenhancements.lib.message.Messenger;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pinned representative of the five ritual recipes migrated to {@link Messenger}: the other four
 * ({@code RainRitualAltarRecipe}, {@code ThunderstormRitualAltarRecipe}, {@code TimeDayAltarRecipe},
 * {@code TimeNightAltarRecipe}) apply the identical two send patterns, so this class covers the
 * shared shape rather than duplicating the same assertions five times.
 */
@ExtendWith(MockitoExtension.class)
class SunRitualAltarRecipeTest {
    private static final Messenger MESSENGER =
            new Messenger(MessagePrefix.of("MythicAltar", NamedTextColor.GOLD));

    @Mock
    private MythicAltarFeature mythicAltarFeature;

    @Mock
    private Plugin plugin;

    @Mock
    private AltarInterface altar;

    @Mock
    private PlayerItemFrameChangeEvent event;

    @Mock
    private Player player;

    @Mock
    private World world;

    @Mock
    private ItemFrame centerPedestal;

    @Mock
    private Runnable removeLock;

    @Test
    void onRecipeComplete_sendsErrorThroughMessenger_whenWeatherAlreadyClear() {
        when(this.mythicAltarFeature.getMessenger()).thenReturn(MESSENGER);
        when(this.event.getPlayer()).thenReturn(this.player);
        when(this.player.getWorld()).thenReturn(this.world);
        when(this.world.isClearWeather()).thenReturn(true);
        when(this.altar.getPedestals()).thenReturn(Map.of(PedestalLocation.CENTER, this.centerPedestal));

        SunRitualAltarRecipe recipe = new SunRitualAltarRecipe(this.mythicAltarFeature);

        recipe.onRecipeComplete(this.plugin, this.altar, this.event, this.removeLock);

        Component expected = MESSENGER.prefixed(
                Component.text("The weather is already clear!", MessagePalette.ERROR));
        verify(this.player).sendMessage(expected);
        verify(this.removeLock).run();
    }

    @Test
    void onRecipeComplete_broadcastsPlayerNameAsSubjectComponent_whenWeatherCleared() {
        when(this.mythicAltarFeature.getMessenger()).thenReturn(MESSENGER);
        when(this.event.getPlayer()).thenReturn(this.player);
        when(this.player.getWorld()).thenReturn(this.world);
        when(this.player.getName()).thenReturn("Herobrine");
        when(this.world.isClearWeather()).thenReturn(false);

        Location centerLocation = new Location(this.world, 0, 64, 0);
        when(this.altar.getLocation()).thenReturn(centerLocation);
        when(this.altar.getPedestals()).thenReturn(Map.of(PedestalLocation.CENTER, this.centerPedestal));

        try (MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class)) {
            BukkitScheduler scheduler = mock(BukkitScheduler.class);
            bukkit.when(Bukkit::getScheduler).thenReturn(scheduler);
            when(scheduler.scheduleSyncDelayedTask(eq(this.plugin), any(Runnable.class), anyLong()))
                    .thenAnswer(invocation -> {
                        // Only the delay-100 task reaches the recipe callback; the earlier ones only draw particles.
                        long delay = invocation.getArgument(2);
                        if (delay == 100L) {
                            Runnable task = invocation.getArgument(1);
                            task.run();
                        }
                        return 0;
                    });

            SunRitualAltarRecipe recipe = new SunRitualAltarRecipe(this.mythicAltarFeature);

            recipe.onRecipeComplete(this.plugin, this.altar, this.event, this.removeLock);

            Component expectedBody = Component.text("The weather has been cleared by ", MessagePalette.EMPHASIS)
                    .append(Component.text("Herobrine", MessagePalette.SUBJECT))
                    .append(Component.text(" using the sun ritual!", MessagePalette.EMPHASIS));
            bukkit.verify(() -> Bukkit.broadcast(MESSENGER.prefixed(expectedBody)));
        }

        verify(this.removeLock).run();
    }
}
