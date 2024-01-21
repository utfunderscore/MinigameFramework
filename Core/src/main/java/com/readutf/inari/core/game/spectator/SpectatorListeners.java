package com.readutf.inari.core.game.spectator;

import com.readutf.inari.core.event.GameEventHandler;
import com.readutf.inari.core.game.Game;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

@RequiredArgsConstructor
public class SpectatorListeners {

    private final Game game;

    @GameEventHandler
    public void onDamageEvent(EntityDamageEvent e) {

        if (!(e.getEntity() instanceof Player player)) return;

        if (game.getSpectatorManager().isSpectator(player.getUniqueId())) {
            e.setCancelled(true);
        }
    }

    @GameEventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player player)) return;

        if(game.getSpectatorManager().isSpectator(player.getUniqueId())) {
            e.setCancelled(true);
        }

    }

}
