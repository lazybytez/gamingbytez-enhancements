package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.particles;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.plugin.Plugin;

/**
 * This interface represents an AltarParticleActionWrapper.
 * An AltarParticleActionWrapper is used to wrap the action that should be performed when a particle effect is completed on an altar.
 * The action is defined by the onRecipeComplete method, which is called when the particle effect is completed.
 */
public interface AltarParticleActionWrapper {
    /**
     * This method is called when the particle effect is completed on an altar.
     *
     * @param plugin The plugin instance.
     * @param altar The altar on which the particle effect was executed.
     * @param event The event that triggered the particle effect.
     */
    void onRecipeComplete(Plugin plugin, AltarInterface altar, PlayerItemFrameChangeEvent event);
}