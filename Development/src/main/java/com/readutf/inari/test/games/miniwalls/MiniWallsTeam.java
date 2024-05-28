package com.readutf.inari.test.games.miniwalls;

import com.readutf.inari.core.game.team.Team;
import com.readutf.inari.core.game.team.TeamColor;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class MiniWallsTeam extends Team {

    private final AtomicBoolean witherAlive;

    public MiniWallsTeam(String teamName, TeamColor color, List<UUID> players) {
        super(teamName, color, players);
        this.witherAlive = new AtomicBoolean(true);
    }

    public boolean isWitherAlive() {
        return witherAlive.get();
    }

    public void setWitherAlive(boolean witherAlive) {
        this.witherAlive.set(witherAlive);
    }

}
