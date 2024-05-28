package com.readutf.inari.test.games.miniwalls;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.exception.GameException;
import com.readutf.inari.core.game.stage.Round;
import com.readutf.inari.core.game.team.Team;
import com.readutf.inari.core.utils.ColorUtils;
import com.readutf.inari.core.utils.Cuboid;
import com.readutf.inari.core.utils.Position;
import com.readutf.inari.test.games.miniwalls.wither.WitherManager;
import com.readutf.inari.test.utils.CancellableTask;
import com.readutf.inari.test.utils.Countdown;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.readutf.inari.test.utils.Countdown.startCountdown;

@RequiredArgsConstructor
public class MiniWallsPrepRound implements Round {

    private final Game game;
    private final List<Location> witherLocations;
    private final List<Cuboid> wallBounds;
    private final WitherManager witherManager;

    public MiniWallsPrepRound(Game game) throws GameException {
        this.game = game;
        this.witherLocations = getWitherMarkers(game);
        this.wallBounds = getWallBounds(game);
        this.witherManager = new WitherManager(game, this);

        System.out.println("walls: " + wallBounds);
    }


    @Override
    public void roundStart() {

        startCountdown(game, Duration.ofSeconds(15), new CancellableTask<>() {
            @Override
            public void run(Integer integer) {
                if(integer < 4) {
                    game.messageAll(ColorUtils.color("Prep phase ending in " + integer + " seconds"));
                }

                if(integer == 0) {
                    for (Cuboid wallBound : wallBounds) {
                        for (Position position : wallBound) {
                            game.getArena().getWorld().getBlockAt(position.toLocation(game.getArena().getWorld())).setType(Material.AIR);
                        }
                    }

                    game.messageAll(ColorUtils.color("Prep phase has ended"));

                }

            }
        });


    }

    @Override
    public void roundEnd(@Nullable Team winnerTeam) {

    }

    @Override
    public boolean hasRoundEnded() {
        return false;
    }

    public MiniWallsTeam getTeam(UUID playerId) {
        return (MiniWallsTeam) game.getTeamByPlayer(playerId);
    }

    @NotNull
    private static List<Cuboid> getWallBounds(Game game) {
        return Optional.ofNullable(game.getArena().getCuboids("wall")).orElse(Collections.emptyList());
    }

    @NotNull
    private static List<Location> getWitherMarkers(Game game) {
        return game.getArena().getMarkers("wither:").stream().map(marker -> marker.toLocation(game.getArena().getWorld())).toList();
    }

}
