package com.readutf.inari.core.game.death;

import com.readutf.inari.core.event.GameEventHandler;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.events.GameDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeathManager {

    private final Game game;
    private final Map<UUID, UUID> lastDamager;

    public DeathManager(Game game) {
        this.game = game;
        this.lastDamager = new HashMap<>();
    }

    public void killPlayer(Player player) {

        if(game.getSpectatorManager().isSpectator(player.getUniqueId())) return;


        if (game.getSpectatorManager().setSpectator(player.getUniqueId())) {
            Bukkit.getPluginManager().callEvent(new GameDeathEvent(player, game));
        }


    }

    @GameEventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player damager) || !(e.getEntity() instanceof Player damaged)) return;

        lastDamager.put(damaged.getUniqueId(), damager.getUniqueId());

    }
    public @Nullable UUID getLastDamager(UUID playerId) {
        return lastDamager.get(playerId);
    }

}
