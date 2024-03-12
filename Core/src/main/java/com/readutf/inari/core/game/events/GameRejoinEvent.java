package com.readutf.inari.core.game.events;

import com.readutf.inari.core.game.Game;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameRejoinEvent extends PlayerGameEvent implements Cancellable {

    private static final @Getter HandlerList handlerList = new HandlerList();

    private boolean cancelled;

    public GameRejoinEvent(Player player, Game game) {
        super(player, game);
        this.cancelled = false;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
