package com.readutf.inari.core.event;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameManager;
import com.readutf.inari.core.utils.Position;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class GameEventAdapter {

    protected final GameManager gameManager;

    public GameEventAdapter(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Find the game that the event belongs to
     * @param event the event
     * @return the game
     */
    public abstract @NotNull GameAdapterResult getGame(Event event);

    public GameAdapterResult findByPlayer(Player player) {
        return Optional.ofNullable(gameManager.getGameByPlayer(player)).map(GameAdapterResult::new).orElse(new GameAdapterResult("No game found for player"));
    }

    public GameAdapterResult findByLocation(Location location) {
        Position position = new Position(location);
        Game foundGame = null;
        for (Game game : gameManager.getGames()) {
            if (game.getArena().getBounds().contains(position)) {
                foundGame = game;
                break;
            }
        }

        return foundGame == null ? new GameAdapterResult("No game found for location") : new GameAdapterResult(foundGame);
    }

}
