package com.readutf.inari.test.games.sumo;

import com.readutf.inari.core.arena.ActiveArena;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.arena.exceptions.ArenaLoadException;
import com.readutf.inari.core.arena.meta.ArenaMeta;
import com.readutf.inari.core.event.GameEventManager;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameManager;
import com.readutf.inari.core.game.exception.GameException;
import com.readutf.inari.core.game.spawning.impl.TeamBasedSpawning;
import com.readutf.inari.core.game.team.Team;
import com.readutf.inari.test.InariDemo;
import com.readutf.inari.test.games.GameStarter;
import com.readutf.inari.test.payload.PayloadRound;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class SumoGameStarter implements GameStarter {

    private final ArenaManager arenaManager;
    private final GameManager gameManager;
    private final GameEventManager eventManager;

    @Override
    public void startGame(List<Team> teams) {


        Optional<ArenaMeta> first = arenaManager.findAvailableArenas(arenaMeta -> arenaMeta.getName().startsWith("sumo")).stream().findFirst();
        first.ifPresent(arenaMeta -> {
            try {
                ActiveArena load = arenaManager.load(arenaMeta);

                Game game = Game.builder(InariDemo.getInstance(), load, eventManager, teams,
                                (game1, previousRound) -> new SumoRound(game1, (SumoRound) previousRound),
                                (game1, previousRound) -> new SumoRound(game1, (SumoRound) previousRound),
                                (game1, previousRound) -> new SumoRound(game1, (SumoRound) previousRound),
                                (game1, previousRound) -> new SumoRound(game1, (SumoRound) previousRound),
                                (game1, previousRound) -> new SumoRound(game1, (SumoRound) previousRound))
                        .setPlayerSpawnHandler(TeamBasedSpawning.fromArena(load, "spawn"))
                        .setSpectatorSpawnHandler(new SumoSpectatorSpawnFinder())
                        .build();

                gameManager.startGame(game);

            } catch (ArenaLoadException | GameException e) {
                e.printStackTrace();
            }

        });

    }
}
