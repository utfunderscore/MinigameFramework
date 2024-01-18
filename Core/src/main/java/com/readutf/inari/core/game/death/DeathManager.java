package com.readutf.inari.core.game.death;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.events.GameSpectateEvent;
import com.readutf.inari.core.game.spectator.SpectatorData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class DeathManager {

    private final Game game;

    public DeathManager(Game game) {
        this.game = game;
    }

    public void killPlayer(Player player) {

        player.setHealth(player.getMaxHealth());
        player.getInventory().clear();
        player.setFoodLevel(20);

        GameSpectateEvent event = new GameSpectateEvent(player, game, new SpectatorData(true, 5 * 1000, true, List.of(5, 4, 3, 2, 1)));

        Bukkit.getPluginManager().callEvent(event);

        game.getSpectatorManager().setSpectator(player.getUniqueId(), event.getSpectatorData());
    }

}
