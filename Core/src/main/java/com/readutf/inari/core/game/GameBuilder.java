package com.readutf.inari.core.game;

import com.google.common.base.Preconditions;
import com.readutf.inari.core.arena.ActiveArena;
import com.readutf.inari.core.event.GameEventManager;
import com.readutf.inari.core.game.spawning.SpawnFinder;
import com.readutf.inari.core.game.stage.RoundCreator;
import com.readutf.inari.core.game.team.Team;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class GameBuilder {

    private final Game game;

    public GameBuilder(JavaPlugin javaPlugin,
                       ActiveArena intialArena,
                       GameEventManager gameEventManager,
                       List<Team> playerTeams,
                       RoundCreator... stageCreators) {
        this.game = new Game(javaPlugin, gameEventManager, intialArena, playerTeams, stageCreators);
    }

    public GameBuilder(GameCreator gameCreator) {
        this.game = gameCreator.create();
    }

    public GameBuilder setPlayerSpawnHandler( SpawnFinder spawnFinder) {
        game.setPlayerSpawnFinder(spawnFinder);
        return this;
    }

    public GameBuilder setSpectatorSpawnHandler( SpawnFinder spawnFinder) {
        game.setSpectatorSpawnFinder(spawnFinder);
        return this;
    }

    public Game build() {
        Preconditions.checkArgument(game.getPlayerSpawnFinder() != null, "Player spawn finder cannot be null");
        Preconditions.checkArgument(game.getSpectatorSpawnFinder() != null, "Spectator spawn finder cannot be null");

        return game;
    }

}
