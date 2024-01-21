package com.readutf.inari.core.game;

import com.readutf.inari.core.game.exception.GameException;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager {

    private @Getter
    static GameManager instance;

    private final Map<UUID, Game> idToGame;
    private final Map<UUID, Game> playerToGame;

    public GameManager() {
        instance = this;
        idToGame = new HashMap<>();
        playerToGame = new HashMap<>();
    }

    public void startGame(Game game) throws GameException {
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

    public void removeGame(Game game) {
        for (UUID allPlayer : game.getAllPlayers()) {
            playerToGame.remove(allPlayer);
        }
        idToGame.remove(game.getGameId());
    }

    public void shutdown() {
        for (Game value : idToGame.values()) {
            value.endGame(null, GameEndReason.CANCELLED);
        }
    }
}
