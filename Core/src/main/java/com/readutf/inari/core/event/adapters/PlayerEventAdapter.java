package com.readutf.inari.core.event.adapters;

import com.readutf.inari.core.event.GameAdapterResult;
import com.readutf.inari.core.event.GameEventAdapter;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerEventAdapter extends GameEventAdapter {

    public PlayerEventAdapter(GameManager gameManager) {
        super(gameManager);
    }

    @Override
    public @NotNull GameAdapterResult getGame(Event event) {
        if(event instanceof PlayerEvent playerEvent) {
            Player player = playerEvent.getPlayer();
            Game gameByPlayer = gameManager.getGameByPlayer(player);
            if(gameByPlayer != null) {
                return new GameAdapterResult(gameByPlayer);
            }
            return new GameAdapterResult("No game found");
        }
        return new GameAdapterResult("Invalid event type.");
    }

}
