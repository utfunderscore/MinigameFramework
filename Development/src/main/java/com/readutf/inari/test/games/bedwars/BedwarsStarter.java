package com.readutf.inari.test.games.bedwars;

import com.readutf.inari.core.arena.ActiveArena;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.arena.meta.ArenaMeta;
import com.readutf.inari.core.event.GameEventManager;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameManager;
import com.readutf.inari.core.game.exception.GameException;
import com.readutf.inari.core.game.spawning.impl.TeamBasedSpawning;
import com.readutf.inari.core.game.team.Team;
import com.readutf.inari.core.game.team.TeamColor;
import com.readutf.inari.core.scoreboard.ScoreboardManager;
import com.readutf.inari.core.utils.ThreadUtils;
import com.readutf.inari.test.InariDemo;
import com.readutf.inari.test.games.GameStarter;
import com.readutf.inari.test.games.shared.AwaitingPlayersStage;
import com.readutf.inari.test.games.sumo.SumoSpectatorSpawnFinder;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class BedwarsStarter implements GameStarter {

    private final ArenaManager arenaManager;
    private final GameManager gameManager;
    private final GameEventManager eventManager;
    private final ScoreboardManager scoreboardManager;

    private final Map<String, TeamColor> TEAM_COLORS = Map.of(
            "Red", TeamColor.RED,
            "Blue", TeamColor.BLUE,
            "Green", TeamColor.GREEN,
            "Yellow", TeamColor.YELLOW,
            "Aqua", TeamColor.AQUA,
            "White", TeamColor.WHITE,
            "Pink", TeamColor.LIGHT_PURPLE,
            "Gray", TeamColor.DARK_GRAY

    );

    @Override
    public CompletableFuture<Game> startGame(List<List<UUID>> teams) throws Exception {


        List<ArenaMeta> availableArenas = arenaManager.findAvailableArenas(arenaMeta -> arenaMeta.getName().startsWith("bedwars"));
        if (availableArenas.isEmpty()) throw new Exception("No available arenas");

        ActiveArena load = arenaManager.load(availableArenas.get(ThreadLocalRandom.current().nextInt(availableArenas.size())));

        CompletableFuture<Game> future = new CompletableFuture<>();

        List<Map.Entry<String, TeamColor>> teamColorsSet = new ArrayList<>(TEAM_COLORS.entrySet());

        List<Team> createdTeams = new ArrayList<>();
        for (int i = 0; i < teams.size(); i++) {
            Map.Entry<String, TeamColor> color = teamColorsSet.get(i);
            createdTeams.add(new BedwarsTeam(color.getKey(), color.getValue(), teams.get(i)));
        }

        Game createdMatch = Game.builder(InariDemo.getInstance(), load, eventManager, scoreboardManager, createdTeams,
                        (game, previousRound) -> new AwaitingPlayersStage(game, 2, 10),
                        (game, previousRound) -> new BedwarsRound(game))
                .setPlayerSpawnHandler(game -> new TeamBasedSpawning(game, "spawn"))
                .setSpectatorSpawnHandler(SumoSpectatorSpawnFinder::new)
                .build();

        ThreadUtils.ensureSync(InariDemo.getInstance(), () -> {
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
