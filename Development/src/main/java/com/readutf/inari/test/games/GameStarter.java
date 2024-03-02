package com.readutf.inari.test.games;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.team.Team;

import java.util.List;

public interface GameStarter {

    Game startGame(List<Team> teams) throws Exception;

}
