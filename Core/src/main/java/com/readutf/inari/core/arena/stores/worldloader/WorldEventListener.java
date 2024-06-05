package com.readutf.inari.core.arena.stores.worldloader;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class WorldEventListener implements Listener {

    @EventHandler
    public void onLoad(WorldInitEvent e) {
        e.getWorld().setKeepSpawnInMemory(false);
    }

}
