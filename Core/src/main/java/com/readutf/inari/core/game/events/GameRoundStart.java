package com.readutf.inari.core.game.events;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.stage.Round;
import lombok.Getter;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameRoundStart extends GameEvent {

    private static @Getter final HandlerList handlerList = new HandlerList();

    private final @Getter Round round;

    public GameRoundStart(Game game, Round round) {
        super(game);
        this.round = round;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
