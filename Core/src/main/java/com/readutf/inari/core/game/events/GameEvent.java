package com.readutf.inari.core.game.events;

import com.readutf.inari.core.game.Game;
import org.bukkit.event.Event;

public abstract class GameEvent extends Event {

    private final Game game;

    public GameEvent(Game game) {
        this.game = game;
    }
}
