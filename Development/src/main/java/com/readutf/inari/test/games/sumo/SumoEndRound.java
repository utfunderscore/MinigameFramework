package com.readutf.inari.test.games.sumo;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameEndReason;
import com.readutf.inari.core.game.stage.Round;
import com.readutf.inari.core.game.team.Team;
import com.readutf.inari.core.utils.ColorUtils;
import com.readutf.inari.test.utils.CancellableTask;
import com.readutf.inari.test.utils.Countdown;
import com.readutf.inari.test.utils.ThreadUtils;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SumoEndRound implements Round {

    private final Game game;
    private final Team winningTeam;
    private final SumoRound previousRound;

    public SumoEndRound(Game game, SumoRound previousRound, Team winningTeam) {
        this.previousRound = previousRound;
        this.winningTeam = winningTeam;
        this.game = game;
    }

    @Override
    public void roundStart() {

        Team team1 = game.getPlayerTeams().get(0);
        Team team2 = game.getPlayerTeams().get(1);

        Team winner = winningTeam;
        Team losers = team1 == winner ? team2 : team1;



        Map<Team, Integer> teamScores = previousRound.getTeamScores();

        for (Player onlinePlayer : winner.getOnlinePlayers()) {
            onlinePlayer.showTitle(Title.title(
                    ColorUtils.color("&6Victory!"),
                    ColorUtils.color("&f%s &7- &f%s".formatted(teamScores.getOrDefault(team1, 0), teamScores.getOrDefault(team2, 0)))
            ));
        }

        for (Player onlinePlayer : losers.getOnlinePlayers()) {
            onlinePlayer.showTitle(Title.title(
                    ColorUtils.color("&6Victory!"),
                    ColorUtils.color("&f%s &7- &f%s".formatted(teamScores.getOrDefault(team1, 0), teamScores.getOrDefault(team2, 0)))
            ));
        }

        new Countdown(game, 5, new CancellableTask<>() {
            @Override
            public void run(Integer integer) {
                if (integer == 0)
                    ThreadUtils.ensureSync(() -> game.endGame(winningTeam, GameEndReason.ENEMIES_ELIMINATED));
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
}
