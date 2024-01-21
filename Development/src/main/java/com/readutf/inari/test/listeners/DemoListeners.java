package com.readutf.inari.test.listeners;

import com.readutf.inari.core.game.events.GameEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class DemoListeners implements Listener {

    @EventHandler
    public void onGameEnd(GameEndEvent e) {

        for (UUID allPlayer : e.getGame().getAllPlayers()) {
            Player player = Bukkit.getPlayer(allPlayer);
            if(player != null) {
                player.teleport(new Location(Bukkit.getWorld("world"), 0, 100, 0));
            }
        }


    }

}
