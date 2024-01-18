package com.readutf.inari.core.event;

import org.bukkit.event.Event;

public interface GameEventAdapter {

    /**
     * Find the game that the event belongs to
     * @param event the event
     * @return the game
     */
    GameAdapterResult getGame(Event event);

}
