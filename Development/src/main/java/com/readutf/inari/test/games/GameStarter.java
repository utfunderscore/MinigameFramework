package com.readutf.inari.test.games;

import com.readutf.inari.core.arena.meta.ArenaMeta;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.team.Team;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface GameStarter {

    CompletableFuture<Game> startGame(ArenaMeta arenaMeta, List<List<UUID>> teams) throws Exception;

}
