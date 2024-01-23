package com.readutf.inari.core.event.adapters;

import com.readutf.inari.core.event.GameAdapterResult;
import com.readutf.inari.core.event.GameEventAdapter;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameManager;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class ChatAdapter extends GameEventAdapter {

    public ChatAdapter(GameManager gameManager) {
        super(gameManager);
    }

    @Override
    public @NotNull GameAdapterResult getGame(Event event) {
        if(event instanceof AsyncPlayerChatEvent chatEvent) {
            Game gameByPlayer = gameManager.getGameByPlayer(chatEvent.getPlayer());
            if(gameByPlayer != null) {
                return new GameAdapterResult(gameByPlayer);
            }
        }

        return new GameAdapterResult("Player is not in a game");
    }
}
