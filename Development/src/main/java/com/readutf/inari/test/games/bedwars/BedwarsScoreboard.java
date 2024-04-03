package com.readutf.inari.test.games.bedwars;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.team.Team;
import com.readutf.inari.core.scoreboard.ScoreboardProvider;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BedwarsScoreboard implements ScoreboardProvider {

    private final Game game;
    private final BedwarsRound bedwarsRound;

    public BedwarsScoreboard(BedwarsRound bedwarsRound) {
        this.game = bedwarsRound.getGame();
        this.bedwarsRound = bedwarsRound;
    }

    @Override
    public String getTitle(Player player) {
        return "&6&lBedwars";
    }

    @Override
    public List<String> getLines(Player player) {
        ArrayList<String> lines = new ArrayList<>();

        lines.add(" ".repeat(16));
        for (Team team : game.getTeams()) {
            BedwarsTeam bedwarsTeam = (BedwarsTeam) team;
            lines.add("%s%s&7 %s %s".formatted(
                    team.getColor().getColorCode(),
                    team.getTeamName().substring(0, 1),
                    team.getTeamName(),
                    bedwarsTeam.hasBed() ? "\u2705" : "\u274C"
            ));

        }


        return lines;
    }
}
