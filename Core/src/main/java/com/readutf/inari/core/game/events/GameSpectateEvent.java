package com.readutf.inari.core.game.events;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.spectator.SpectatorData;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameSpectateEvent extends PlayerGameEvent {

    private static final HandlerList handlers = new HandlerList();

    private @Setter @Getter SpectatorData spectatorData;

    public GameSpectateEvent(Player player, Game game, SpectatorData spectatorData) {
        super(player, game);
        this.spectatorData = spectatorData;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }


}
