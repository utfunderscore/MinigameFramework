package com.readutf.inari.core.game.events;

import com.readutf.inari.core.game.Game;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameDeathEvent extends PlayerGameEvent {

    private static @Getter HandlerList handlerList = new HandlerList();

    public GameDeathEvent(Player player, Game game) {
        super(player, game);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
