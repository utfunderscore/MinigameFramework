package com.readutf.inari.core.game;

import com.readutf.inari.core.arena.ActiveArena;
import com.readutf.inari.core.event.GameEventManager;
import com.readutf.inari.core.event.testlistener.TestListener;
import com.readutf.inari.core.game.death.DeathListeners;
import com.readutf.inari.core.game.death.DeathManager;
import com.readutf.inari.core.game.exception.MatchException;
import com.readutf.inari.core.game.lang.DefaultGameLang;
import com.readutf.inari.core.game.spawning.SpawnFinder;
import com.readutf.inari.core.game.spectator.SpectatorManager;
import com.readutf.inari.core.game.stage.Round;
import com.readutf.inari.core.game.stage.RoundCreator;
import com.readutf.inari.core.game.team.Team;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@Getter
@Setter
public class Game {

    private final UUID gameId;
    private final JavaPlugin javaPlugin;
    private final List<Team> playerTeams;
    private final ArrayDeque<RoundCreator> stages;
    private final GameEventManager gameEventManager;
    private final DeathManager deathManager;
    private final SpectatorManager spectatorManager;

    private SpawnFinder playerSpawnFinder;
    private SpawnFinder spectatorSpawnFinder;
    private GameLang lang;
    private ActiveArena arena;
    private Round currentRound;

    protected Game(JavaPlugin javaPlugin, GameEventManager gameEventManager, ActiveArena intialArena, List<Team> playerTeams, RoundCreator... stageCreators) {
        this.gameId = UUID.randomUUID();
        this.javaPlugin = javaPlugin;
        this.arena = intialArena;
        this.playerTeams = playerTeams;
        this.gameEventManager = gameEventManager;
        this.spectatorManager = new SpectatorManager(this);
        this.lang = new DefaultGameLang();
        this.stages = new ArrayDeque<>(Arrays.asList(stageCreators));
        this.deathManager = new DeathManager(this);



        Arrays.asList(
                new TestListener(),
                new DeathListeners(deathManager)
        ).forEach(o -> gameEventManager.scanForListeners(this, o));

        gameEventManager.scanForListeners(this, new TestListener());

    }

    public void start() throws MatchException {
        RoundCreator creator = stages.poll();
        if (creator == null) throw new MatchException("No stages to start");
        currentRound = creator.createRound(this, null);
        currentRound.init();

        for (Player alivePlayers : getOnlineAndAlivePlayers()) {
            Team team = getTeamByPlayer(alivePlayers);
            alivePlayers.teleport(playerSpawnFinder.findSpawn(this, alivePlayers.getUniqueId()));
        }
    }

    public void end() {
        spectatorManager.shutdown();
    }

    public List<Player> getOnlineAndAlivePlayers() {
        List<Player> list = new ArrayList<>();
        for (UUID uuid : getAllPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline() || !isAlive(player)) continue;
            list.add(player);
        }
        return list;
    }

    public List<Player> getOnlinePlayers() {
        List<Player> list = new ArrayList<>();
        for (UUID uuid : getAllPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) continue;
            list.add(player);
        }
        return list;
    }

    public Team getTeamById(int index) {
        return playerTeams.get(index);
    }

    public int getTeamIndex(Team team) {
        return playerTeams.indexOf(team);
    }

    public int getTeamIndex(UUID player) {
        return getTeamIndex(getTeamByPlayer(player));
    }

    public Team getTeamByPlayer(Player player) {
        return getTeamByPlayer(player.getUniqueId());
    }

    public Team getTeamByPlayer(UUID playerId) {
        for (Team playerTeam : playerTeams) {
            if (playerTeam.getPlayers().contains(playerId)) return playerTeam;
        }
        return null;
    }

    public boolean isAlive(Player player) {
        return !spectatorManager.isSpectator(player.getUniqueId());
    }

    public List<UUID> getAllPlayers() {
        List<UUID> list = new ArrayList<>();
        for (Team playerTeam : playerTeams) {
            List<UUID> players = playerTeam.getPlayers();
            list.addAll(players);
        }
        return list;
    }

    public static GameBuilder builder(JavaPlugin javaPlugin,
                                      ActiveArena intialArena,
                                      GameEventManager gameEventManager,
                                      List<Team> playerTeams,
                                      RoundCreator... stageCreators) {
        return new GameBuilder(javaPlugin, intialArena, gameEventManager, playerTeams, stageCreators);
    }

}
