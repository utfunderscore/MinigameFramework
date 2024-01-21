package com.readutf.inari.core.game.death;

import com.readutf.inari.core.event.GameEventHandler;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

@RequiredArgsConstructor
public class DeathListeners {

    private final DeathManager deathManager;

    @GameEventHandler
    public void onDeath(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player player)) return;

        if (player.getHealth() - event.getFinalDamage() <= 0) {
            event.setDamage(0);
            deathManager.killPlayer(player);
        }
    }

    @GameEventHandler
    public void onAttacked(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player player)) return;

        if (player.getHealth() - event.getFinalDamage() <= 0) {
            event.setDamage(0);
            deathManager.killPlayer(player);
        }
    }

}
