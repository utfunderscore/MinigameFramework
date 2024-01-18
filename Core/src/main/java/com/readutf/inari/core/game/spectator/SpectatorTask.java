package com.readutf.inari.core.game.spectator;

import com.readutf.inari.core.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class SpectatorTask extends BukkitRunnable {

    private final Game game;

    public SpectatorTask(Game game) {
        this.game = game;
    }

    @Override
    public void run() {

        for (UUID spectator : game.getSpectatorManager().getSpectators()) {
            SpectatorData data = game.getSpectatorManager().getSpectatorData(spectator);
            if (data == null) continue;

            long respawnIn = data.getRespawnAt() - System.currentTimeMillis();

            Player player = Bukkit.getPlayer(spectator);
            if (player != null) {
                for (Integer messageInterval : new ArrayList<>(data.getMessageIntervals())) {
                    if ((respawnIn / 1000) + 1 <= messageInterval) {
                        for (String s : game.getLang().getRespawnIntervalMessage(player, messageInterval)) {
                            player.sendMessage(s);
                        }
                        data.getMessageIntervals().remove(messageInterval);
                    }
                }
            }

            if (respawnIn <= 0) {
                game.getSpectatorManager().respawnPlayer(spectator);
            }

        }

    }
}
