package com.readutf.inari.core.game.events;

import com.readutf.inari.core.game.Game;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameStartEvent extends GameEvent{

    private static @Getter HandlerList handlerList = new HandlerList();

    public GameStartEvent(Game game) {
        super(game);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
