package com.readutf.inari.core.event.adapters;

import com.readutf.inari.core.event.GameAdapterResult;
import com.readutf.inari.core.event.GameEventAdapter;
import com.readutf.inari.core.game.GameManager;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

public class BlockPlaceAdapter extends GameEventAdapter {

    public BlockPlaceAdapter(GameManager gameManager) {
        super(gameManager);
    }

    @Override
    public @NotNull GameAdapterResult getGame(Event event) {
        if(event instanceof BlockPlaceEvent blockPlaceEvent) {
            return findByPlayer(blockPlaceEvent.getPlayer());
        }
        return new GameAdapterResult("Event is not a BlockPlaceEvent");
    }
}
