package com.readutf.inari.core.game.death;

import com.readutf.inari.core.event.GameEventHandler;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.events.GameSpectateEvent;
import com.readutf.inari.core.game.spectator.SpectatorData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
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

        player.setHealth(player.getMaxHealth());
        player.getInventory().clear();
        player.setFoodLevel(20);

        GameSpectateEvent event = new GameSpectateEvent(player, game, new SpectatorData(true, 5 * 1000, true, List.of(5, 4, 3, 2, 1)));

        Bukkit.getPluginManager().callEvent(event);

        game.getSpectatorManager().setSpectator(player.getUniqueId(), event.getSpectatorData());
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
