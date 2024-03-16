package de.lazybytez.gamingbytezenhancements.feature.mythicaltar.particles;

import de.lazybytez.gamingbytezenhancements.feature.mythicaltar.altar.AltarInterface;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;

public interface AltarParticleEffectInterface {
    void executeParticleEffect(AltarInterface altar, PlayerItemFrameChangeEvent event, AltarParticleActionWrapper action);
}
