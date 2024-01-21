package com.readutf.inari.test.games.sumo;

import com.readutf.inari.core.event.GameEventHandler;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.events.GameSpectateEvent;
import com.readutf.inari.core.game.events.MatchDeathEvent;
import com.readutf.inari.core.game.spectator.SpectatorData;
import com.readutf.inari.core.game.stage.Round;
import com.readutf.inari.core.utils.Position;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

@RequiredArgsConstructor
public class SumoListeners {

    private final Game game;

    @GameEventHandler
    public void matchDeath(GameSpectateEvent e) {

        Player player = e.getPlayer();

        SpectatorData spectatorData = e.getSpectatorData();

        spectatorData.setCanFly(true);
        spectatorData.setRespawn(false);

    }

    @GameEventHandler
    public void onMove(PlayerMoveEvent e) {

        Round currentRound = game.getCurrentRound();
        if(!(currentRound instanceof SumoRound sumoRound)) return;

        //has player moved full block
        if(e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockY() == e.getTo().getBlockY() && e.getFrom().getBlockZ() == e.getTo().getBlockZ()) return;

        Player player = e.getPlayer();
        if (sumoRound.getCountdown().isActive()) {
            player.teleport(e.getFrom());
            return;
        }

        if(!game.getArena().getBounds().contains(Position.fromLocation(e.getTo()))) {
            game.getDeathManager().killPlayer(player);
            return;
        }

        if(e.getTo().getBlock().getType() == Material.WATER) {
            game.killPlayer(player);
            return;
        }

    }

}
