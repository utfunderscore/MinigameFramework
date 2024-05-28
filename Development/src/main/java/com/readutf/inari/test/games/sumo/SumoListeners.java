package com.readutf.inari.test.games.sumo;

import com.readutf.inari.core.event.GameEventHandler;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameEndReason;
import com.readutf.inari.core.game.events.GameDeathEvent;
import com.readutf.inari.core.game.events.GameSpectateEvent;
import com.readutf.inari.core.game.events.GameStartEvent;
import com.readutf.inari.core.game.exception.GameException;
import com.readutf.inari.core.game.spawning.SpawnFinder;
import com.readutf.inari.core.game.spectator.SpectatorData;
import com.readutf.inari.core.game.team.Team;
import com.readutf.inari.core.logging.Logger;
import com.readutf.inari.core.utils.ColorUtils;
import com.readutf.inari.core.utils.Position;
import com.readutf.inari.test.InariDemo;
import com.readutf.inari.test.utils.CancellableTask;
import com.readutf.inari.test.utils.Countdown;
import com.readutf.inari.core.utils.ThreadUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

import static com.readutf.inari.test.utils.Countdown.startCountdown;

public class SumoListeners {

    private final Game game;
    private final Logger logger;

    public SumoListeners(Game game) {
        this.game = game;
        this.logger = game.getLoggerFactory().getLogger(SumoListeners.class);
    }

    @GameEventHandler
    public void onGameStart(GameStartEvent gameStartEvent) {

        for (Player onlinePlayer : game.getOnlinePlayers()) {

            for (Player otherPlayer : game.getOnlinePlayers()) {
                if (onlinePlayer != otherPlayer) {
                    onlinePlayer.showPlayer(InariDemo.getInstance(), otherPlayer);
                }
            }

            onlinePlayer.clearActivePotionEffects();
            onlinePlayer.clearTitle();
            onlinePlayer.setHealth(20);
            onlinePlayer.setFoodLevel(20);
        }

    }

    @GameEventHandler
    public void spectateEvent(GameSpectateEvent e) throws GameException {
        Player player = e.getPlayer();

        if (game.getCurrentRound() instanceof SumoEndRound || game.getAliveTeams().size() == 1) {
            e.setCancelled(true);
            SpawnFinder spawnFinder = game.getPlayerSpawnFinder();
            Location spawn = spawnFinder.findSpawn(player);
            player.teleport(spawn);
            return;
        }

        SpectatorData spectatorData = e.getSpectatorData();

        spectatorData.setRespawn(false);
        spectatorData.setCanFly(true);

    }

    @GameEventHandler()
    public void onDeath(GameDeathEvent e) {

        logger.debug("Player " + e.getPlayer().getName() + " has died");

        if (game.getAliveTeams().size() != 1) {
            logger.info("Not ending round, teams alive: " + game.getAliveTeams().size() + " " + game.getAliveTeams());
            return;
        }


        Team winnerTeam = game.getAliveTeams().get(0);

        final SumoRound currentRound = ((SumoRound) game.getCurrentRound());
        final Map<Team, Integer> teamScores = currentRound.getTeamScores();

        for (Player onlinePlayer : game.getOnlinePlayers()) {
            onlinePlayer.showTitle(getScoreTitle(teamScores, null));
        }

        int newScore = teamScores.getOrDefault(winnerTeam, 0) + 1;
        teamScores.put(winnerTeam, newScore);


        startCountdown(game, 3, new CancellableTask<>() {
            @Override
            public void run(Integer integer) {


                if (integer == 3) {

                    for (Player onlinePlayer : game.getOnlinePlayers()) {
                        onlinePlayer.showTitle(getScoreTitle(teamScores, winnerTeam));
                        onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_ON, 5, 1);
                    }
                }


                if (integer == 0) {
                    for (Player onlinePlayer : game.getOnlinePlayers()) {
                        onlinePlayer.clearTitle();
                    }

                    if (newScore > 2) {
                        game.setNextRound(new SumoEndRound(game, currentRound, winnerTeam));
                    }

                    ThreadUtils.ensureSync(game.getJavaPlugin(), () -> game.endRound(winnerTeam));
                }
            }
        });
    }

    @GameEventHandler
    public void onMove(PlayerMoveEvent e) {


        if (!(game.getCurrentRound() instanceof SumoRound sumoRound)) return;


        //has player moved full block
        if (e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockY() == e.getTo().getBlockY() && e.getFrom().getBlockZ() == e.getTo().getBlockZ())
            return;

        Player player = e.getPlayer();
        if (sumoRound.getCountdown() == null || sumoRound.getCountdown().isActive()) {
            player.teleport(e.getFrom());
            return;
        }

        if (!game.getArena().getBounds().contains(Position.fromLocation(e.getTo()))) {
            game.getDeathManager().killPlayer(player);
            return;
        }

        if (e.getTo().getBlock().getType() == Material.WATER) {
            game.killPlayer(player);
            return;
        }

    }

    @GameEventHandler
    public void onQuit(PlayerQuitEvent e) {
        game.killPlayer(e.getPlayer());

        System.out.println("added with debug");

        if(game.getAliveTeams().size() == 1) {
            game.endGame(game.getAliveTeams().getFirst(), GameEndReason.ENEMIES_ELIMINATED);
        } else if(game.getAliveTeams().isEmpty()) {
            game.endGame(null, GameEndReason.DRAW);
        }
    }

    public Title getScoreTitle(Map<Team, Integer> teamScores, Team upArrowTeam) {
        Team team1 = game.getTeamById(0);
        Team team2 = game.getTeamById(1);

        Integer score1 = teamScores.getOrDefault(team1, 0);
        Integer score2 = teamScores.getOrDefault(team2, 0);

        String team1Names = team1.getAllPlayers().stream().map(OfflinePlayer::getName).collect(Collectors.joining(", "));
        String team2Names = team2.getAllPlayers().stream().map(OfflinePlayer::getName).collect(Collectors.joining(", "));

        Component teamMembersLine = ColorUtils.color("&f%s &7vs &f%s".formatted(team1Names, team2Names));
        Title.Times duration = Title.Times.times(Duration.ZERO, Duration.of(3, ChronoUnit.SECONDS), Duration.ZERO);

        if (upArrowTeam == null) {
            return Title.title(
                    ColorUtils.color("&f%s &7- &f%s".formatted(score1, score2)),
                    teamMembersLine,
                    duration
            );
        }

        if (upArrowTeam == team1) {
            return Title.title(
                    ColorUtils.color("&a%s\u25B2 &7- &f%s".formatted(score1, score2)),
                    teamMembersLine,
                    duration
            );
        } else {
            return Title.title(
                    ColorUtils.color("&f%s &7- &a%s\u25B2".formatted(score1, score2)),
                    teamMembersLine,
                    duration
            );
        }
    }

}
