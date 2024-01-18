package com.readutf.inari.core.event.adapters;

import com.fastasyncworldedit.core.util.NbtUtils;
import com.readutf.inari.core.event.GameAdapterResult;
import com.readutf.inari.core.event.GameEventAdapter;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameManager;
import lombok.AllArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@AllArgsConstructor
public class ChatAdapter implements GameEventAdapter {

    private final GameManager gameManager;

    @Override
    public GameAdapterResult getGame(Event event) {
        if(event instanceof AsyncPlayerChatEvent chatEvent) {
            Game gameByPlayer = gameManager.getGameByPlayer(chatEvent.getPlayer());
            if(gameByPlayer != null) {
                return new GameAdapterResult(gameByPlayer);
            }
        }

        return new GameAdapterResult("Player is not in a game");
    }
}
