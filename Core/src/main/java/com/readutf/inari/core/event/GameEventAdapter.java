package com.readutf.inari.core.event;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public interface GameEventAdapter {

    /**
     * Find the game that the event belongs to
     * @param event the event
     * @return the game
     */
    @NotNull GameAdapterResult getGame(Event event);

}
