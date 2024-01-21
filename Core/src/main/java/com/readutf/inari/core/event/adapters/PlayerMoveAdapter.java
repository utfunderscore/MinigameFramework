package com.readutf.inari.core.event.adapters;

import com.readutf.inari.core.event.GameAdapterResult;
import com.readutf.inari.core.event.GameEventAdapter;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class PlayerMoveAdapter implements GameEventAdapter {

    private final GameManager gameManager;

    @Override
    public @NotNull GameAdapterResult getGame(Event event) {
        if(event instanceof PlayerMoveEvent playerMoveEvent) {

            Player player = playerMoveEvent.getPlayer();
            Game gameByPlayer = gameManager.getGameByPlayer(player);
            if(gameByPlayer != null) {
                return new GameAdapterResult(gameByPlayer);
            }

        }
        return new GameAdapterResult("Player not in game");
    }
}
