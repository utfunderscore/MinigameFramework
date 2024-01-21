package com.readutf.inari.core.game.events;

import com.readutf.inari.core.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MatchDeathEvent extends PlayerGameEvent {

    private static final HandlerList handlers = new HandlerList();

    public MatchDeathEvent(Player player, Game game) {
        super(player, game);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
