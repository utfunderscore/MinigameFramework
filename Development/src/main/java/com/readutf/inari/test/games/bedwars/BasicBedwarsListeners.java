package com.readutf.inari.test.games.bedwars;

import com.readutf.inari.core.event.GameEventHandler;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.events.GameSpectateEvent;
import com.readutf.inari.core.game.spectator.SpectatorData;
import com.readutf.inari.core.game.team.Team;
import com.readutf.inari.core.utils.ColorUtils;
import com.readutf.inari.core.utils.Position;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class BasicBedwarsListeners {

    private final Game game;
    private final BedwarsRound bedwarsRound;

    public BasicBedwarsListeners(BedwarsRound bedwarsRound) {
        this.bedwarsRound = bedwarsRound;
        this.game = bedwarsRound.getGame();
    }

    @GameEventHandler
    public void onDeath(GameSpectateEvent e) {

        System.out.println("on death");

        Player player = e.getPlayer();
        BedwarsTeam team = (BedwarsTeam) e.getGame().getTeamByPlayer(player);
        if (team == null) return;

        System.out.println("team: " + team.getTeamName());

        System.out.println("has bed: " + team.hasBed());

        if(team.hasBed()) {
            e.setSpectatorData(new SpectatorData(true, 5000, true, List.of()));
        } else {
            e.setSpectatorData(new SpectatorData(false, 0, true, List.of()));
        }

    }

    @GameEventHandler
    public void onBreak(BlockBreakEvent e) {


        if (e.getBlock().getType() == Material.RED_BED) {

            e.setCancelled(true);

            Player player = e.getPlayer();
            Team team = game.getTeamByPlayer(player);
            Team teamFromBedPosition = bedwarsRound.getTeamFromBedPosition(e.getBlock().getLocation());

            if(team == teamFromBedPosition) {
                e.setCancelled(true);
                player.sendMessage(ColorUtils.color("&cYou cannot break your own bed!"));
                return;
            }

            e.getBlock().setType(Material.AIR);
            for (BlockFace blockFace : List.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST)) {
                Block relative = e.getBlock().getRelative(blockFace);
                if (relative.getType() == Material.RED_BED) {
                    relative.setType(Material.AIR);
                }
            }



            bedwarsRound.destroyBed(player, (BedwarsTeam) teamFromBedPosition);
        }


    }

    @GameEventHandler
    public void onMove(PlayerMoveEvent e) {

        if (!game.getArena().getBounds().contains(Position.fromLocation(e.getTo()))) {
            game.killPlayer(e.getPlayer());
        }


    }

}
