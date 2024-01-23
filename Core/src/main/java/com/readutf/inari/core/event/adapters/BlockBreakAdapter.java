package com.readutf.inari.core.event.adapters;

import com.readutf.inari.core.event.GameAdapterResult;
import com.readutf.inari.core.event.GameEventAdapter;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class BlockBreakAdapter extends GameEventAdapter {

    public BlockBreakAdapter(GameManager gameManager) {
        super(gameManager);
    }

    @Override
    public @NotNull GameAdapterResult getGame(Event event) {

        if(event instanceof BlockBreakEvent blockPlaceEvent) {
            Player player = blockPlaceEvent.getPlayer();

            Game gameByPlayer = gameManager.getGameByPlayer(player);
            if(gameByPlayer != null) {
                return new GameAdapterResult(gameByPlayer);
            }

            return new GameAdapterResult("Player is not in a game");
        }
        return new GameAdapterResult("Invalid event type");
    }
}
