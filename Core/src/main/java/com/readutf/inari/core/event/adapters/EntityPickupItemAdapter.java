package com.readutf.inari.core.event.adapters;

import com.readutf.inari.core.event.GameAdapterResult;
import com.readutf.inari.core.event.GameEventAdapter;
import com.readutf.inari.core.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.jetbrains.annotations.NotNull;

public class EntityPickupItemAdapter extends GameEventAdapter {

    public EntityPickupItemAdapter(GameManager gameManager) {
        super(gameManager);
    }

    @Override
    public @NotNull GameAdapterResult getGame(Event event) {
        if (event instanceof EntityPickupItemEvent e && e.getEntity() instanceof Player player) {
            return findByPlayer(player);
        }


        return new GameAdapterResult("No game found.");
    }
}
