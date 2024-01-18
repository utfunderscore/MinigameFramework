package com.readutf.inari.core.game;

import com.readutf.inari.core.game.exception.MatchException;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager {

    private final Map<UUID, Game> idToGame;
    private final Map<UUID, Game> playerToGame;

    public GameManager() {
        idToGame = new HashMap<>();
        playerToGame = new HashMap<>();
    }

    public void startGame(Game game) throws MatchException {
        idToGame.put(game.getGameId(), game);
        for (UUID allPlayer : game.getAllPlayers()) {
            playerToGame.put(allPlayer, game);
        }

        game.start();
    }

    public Game getGameById(UUID uuid) {
        return idToGame.get(uuid);
    }

    public Game getGameByPlayer(UUID uuid) {
        return playerToGame.get(uuid);
    }

    public Game getGameByPlayer(Player player) {
        return playerToGame.get(player.getUniqueId());
    }

}
