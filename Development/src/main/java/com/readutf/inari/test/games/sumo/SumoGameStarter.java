package com.readutf.inari.test.games.sumo;

import com.readutf.inari.core.arena.ActiveArena;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.arena.meta.ArenaMeta;
import com.readutf.inari.core.event.GameEventManager;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameManager;
import com.readutf.inari.core.game.exception.GameException;
import com.readutf.inari.core.game.spawning.impl.TeamBasedSpawning;
import com.readutf.inari.core.game.team.Team;
import com.readutf.inari.test.InariDemo;
import com.readutf.inari.test.games.GameStarter;
import com.readutf.inari.test.games.shared.AwaitingPlayersStage;
import com.readutf.inari.test.utils.ThreadUtils;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
public class SumoGameStarter implements GameStarter {

    private final ArenaManager arenaManager;
    private final GameManager gameManager;
    private final GameEventManager eventManager;

    @Override
    public CompletableFuture<Game> startGame(List<Team> teams) throws Exception {


        List<ArenaMeta> availableArenas = arenaManager.findAvailableArenas(arenaMeta -> arenaMeta.getName().startsWith("sumo"));
        if(availableArenas.isEmpty()) throw new Exception("No available arenas");

        ActiveArena load = arenaManager.load(availableArenas.get(ThreadLocalRandom.current().nextInt(availableArenas.size())));

        CompletableFuture<Game> future = new CompletableFuture<>();

        Game createdMatch = Game.builder(InariDemo.getInstance(), load, eventManager, teams,
                        (game, previousRound) -> new AwaitingPlayersStage(game, 2, 60),
                        (game, previousRound) -> new SumoRound(game, null),
                        (game, previousRound) -> new SumoRound(game, (SumoRound) previousRound),
                        (game, previousRound) -> new SumoRound(game, (SumoRound) previousRound),
                        (game, previousRound) -> new SumoRound(game, (SumoRound) previousRound),
                        (game, previousRound) -> new SumoRound(game, (SumoRound) previousRound))
                .setPlayerSpawnHandler(game -> new TeamBasedSpawning(game, "spawn"))
                .setSpectatorSpawnHandler(SumoSpectatorSpawnFinder::new)
                .build();

        ThreadUtils.ensureSync(() -> {
            try {
                gameManager.startGame(createdMatch);
                future.complete(createdMatch);
            } catch (GameException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
