package com.readutf.inari.test.games.bedwars;

import com.readutf.inari.core.game.team.Team;
import com.readutf.inari.core.game.team.TeamColor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class BedwarsTeam extends Team {

    private final AtomicBoolean bedState;

    public BedwarsTeam(String teamName, TeamColor color, List<UUID> players) {
        super(teamName, color, players);
        bedState = new AtomicBoolean(true);
    }

    public boolean hasBed() {
        return bedState.get();
    }

    public void setHasBed(boolean hasBed) {
        bedState.set(hasBed);
    }
}
