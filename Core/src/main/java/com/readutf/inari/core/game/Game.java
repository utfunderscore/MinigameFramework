package com.readutf.inari.core.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.readutf.inari.core.arena.ActiveArena;
import com.readutf.inari.core.event.GameEventManager;
import com.readutf.inari.core.event.testlistener.TestListener;
import com.readutf.inari.core.game.death.DeathListeners;
import com.readutf.inari.core.game.death.DeathManager;
import com.readutf.inari.core.game.events.GameEndEvent;
import com.readutf.inari.core.game.events.GameRoundStart;
import com.readutf.inari.core.game.events.GameStartEvent;
import com.readutf.inari.core.game.exception.GameException;
import com.readutf.inari.core.game.lang.DefaultGameLang;
import com.readutf.inari.core.game.rejoin.RejoinListeners;
import com.readutf.inari.core.game.scoreboard.ScoreboardListeners;
import com.readutf.inari.core.game.spawning.SpawnFinder;
import com.readutf.inari.core.game.spectator.SpectatorData;
import com.readutf.inari.core.game.spectator.SpectatorListeners;
import com.readutf.inari.core.game.spectator.SpectatorManager;
import com.readutf.inari.core.game.stage.Round;
import com.readutf.inari.core.game.stage.RoundCreator;
import com.readutf.inari.core.game.task.GameThread;
import com.readutf.inari.core.game.team.Team;
import com.readutf.inari.core.logging.GameLoggerFactory;
import com.readutf.inari.core.logging.Logger;
import com.readutf.inari.core.logging.impl.GameLogger;
import com.readutf.inari.core.logging.store.FlatFileLogStore;
import com.readutf.inari.core.scoreboard.ScoreboardManager;
import com.readutf.inari.core.scoreboard.ScoreboardProvider;
import com.readutf.inari.core.utils.serialize.ConfigurationSerializableAdapter;
import com.readutf.inari.core.utils.serialize.ItemStackAdapter;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.util.*;

@Getter
@Setter
public class Game {


    private static Timer timer = new Timer();
    private static @Getter Gson gson = new GsonBuilder()
            .setObjectToNumberStrategy(JsonReader::nextInt)
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ConfigurationSerializableAdapter())
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    private final UUID gameId;
    private final JavaPlugin javaPlugin;
    private final Logger logger;
    private final List<Team> teams;
    private final ArrayDeque<RoundCreator> stages;
    private final GameEventManager gameEventManager;
    private final DeathManager deathManager;
    private final SpectatorManager spectatorManager;
    private final ScoreboardManager scoreboardManager;
    private final GameThread gameThread;
    private final Map<String, String> attributes;
    private final GameLoggerFactory loggerFactory;

    private SpawnFinder playerSpawnFinder;
    private @NotNull GameState gameState;
    private SpawnFinder spectatorSpawnFinder;
    private ScoreboardProvider scoreboardProvider;
    private GameLang lang;
    private ActiveArena arena;
    private Round currentRound;



    protected Game(JavaPlugin javaPlugin,
                   GameEventManager gameEventManager,
                   ScoreboardManager scoreboardManager,
                   ActiveArena intialArena,
                   List<Team> teams,
                   RoundCreator... stageCreators
    ) {
        this.gameId = UUID.randomUUID();
        this.loggerFactory = new GameLoggerFactory(this, gameId1 -> new FlatFileLogStore(gameId1, javaPlugin.getDataFolder()));
        this.javaPlugin = javaPlugin;
        this.arena = intialArena;
        this.teams = teams;
        this.logger = loggerFactory.getLogger(Game.class);
        this.gameEventManager = gameEventManager;
        this.attributes = new HashMap<>();
        this.spectatorManager = new SpectatorManager(this);
        this.lang = new DefaultGameLang();
        this.scoreboardManager = scoreboardManager;
        this.gameThread = new GameThread(this);
        this.stages = new ArrayDeque<>(Arrays.asList(stageCreators));
        this.deathManager = new DeathManager(this);
        this.gameState = GameState.WAITING;

        timer.schedule(gameThread, 0, 1);

        Arrays.asList(
                new TestListener(),
                new DeathListeners(deathManager),
                new SpectatorListeners(this),
                new RejoinListeners(this),
                new ScoreboardListeners(this)
        ).forEach(this::registerListeners);
    }

    public void setScoreboard(ScoreboardProvider provider) {
        this.scoreboardProvider = provider;
        for (Player onlinePlayer : getOnlinePlayers()) {
            scoreboardManager.setPlayerBoard(onlinePlayer, provider);
        }
    }

    public void start() throws GameException {
        if (gameState != GameState.WAITING) throw new GameException("Game is already started");

        logger.info("Starting match " + gameId);

        startNextRound();

        Bukkit.getPluginManager().callEvent(new GameStartEvent(this));
        this.gameState = GameState.ACTIVE;
    }

    public void endRound(@Nullable Team winner) {
        if (!Bukkit.isPrimaryThread()) {
            throw new IllegalStateException("endRound must be called from the main thread");
        }

        currentRound.roundEnd(winner);

        try {
            startNextRound();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private int round = 0;

    private void startNextRound() throws GameException {
        RoundCreator creator = stages.poll();
        if (creator == null) {
            endGame(null, GameEndReason.NO_ROUNDS_LEFT);
            return;
        }
        currentRound = creator.createRound(this, currentRound);


        for (UUID spectator : spectatorManager.getSpectators()) {
            Player player = Bukkit.getPlayer(spectator);
            if (player == null || !player.isOnline()) continue;
            spectatorManager.respawnPlayer(player.getUniqueId(), false);
        }

        for (Player alivePlayers : getOnlineAndAlivePlayers()) {
            alivePlayers.teleport(playerSpawnFinder.findSpawn(alivePlayers));
        }

        currentRound.roundStart();
        round++;
        Bukkit.getPluginManager().callEvent(new GameRoundStart(this, currentRound));
    }

    public void endGame(Team winner, GameEndReason reason) {
        gameState = GameState.ENDED;

        GameEndEvent event = new GameEndEvent(this, winner, reason);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        for (Player onlinePlayer : getOnlinePlayers()) {
            for (Component c : lang.getGameSummaryMessage(onlinePlayer)) {
                onlinePlayer.sendMessage(c);
            }

            SpectatorData spectatorData = spectatorManager.getSpectatorData(onlinePlayer.getUniqueId());
            if (spectatorData != null) {
                spectatorManager.revertState(onlinePlayer);
            }

        }

        arena.free();
        currentRound.roundEnd(winner);
        gameThread.cancel();
        spectatorManager.shutdown();
        gameEventManager.unregisterGame(this);
        loggerFactory.shutdown();
        GameManager.getInstance().removeGame(this);

    }

    public void messageAlive(Component component) {

        for (Player onlineAndAlivePlayer : getOnlineAndAlivePlayers()) {
            onlineAndAlivePlayer.sendMessage(component);
        }

    }

    public void messageAll(Component component) {
        for (Player onlinePlayer : getOnlinePlayers()) {
            onlinePlayer.sendMessage(component);
        }
    }

    public void killPlayer(Player player) {
        deathManager.killPlayer(player);
    }

    public void registerListeners(Object object) {
        gameEventManager.scanForListeners(this, object);
    }

    public void unregisterListeners(Object object) {
        gameEventManager.unregisterListeners(this, object);
    }

    public void setNextRound(Round round) {
        stages.addFirst((game, previousRound) -> round);
    }

    public void changeArena(ActiveArena arena) {
        this.arena.free();
        this.arena = arena;
    }

    public List<Player> getOnlineAndAlivePlayers() {
        List<Player> list = new ArrayList<>();
        for (UUID uuid : getAllPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline() || !isAlive(player.getUniqueId())) continue;
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
        return teams.get(index);
    }

    public int getTeamIndex(Team team) {
        return teams.indexOf(team);
    }

    public int getTeamIndex(UUID player) {
        return getTeamIndex(getTeamByPlayer(player));
    }

    public Team getTeamByPlayer(Player player) {
        return getTeamByPlayer(player.getUniqueId());
    }

    public Team getTeamByPlayer(UUID playerId) {
        for (Team playerTeam : teams) {
            if (playerTeam.getPlayers().contains(playerId)) return playerTeam;
        }
        return null;
    }

    public List<Team> getAliveTeams() {
        return teams.stream().filter(team -> team.getPlayers().stream().anyMatch(this::isAlive)).toList();
    }

    public @Nullable <T> T getAttribute(String key, Class<T> clazz) {
        if(String.class == clazz) return clazz.cast(attributes.get(key));

        String content = attributes.get(key);
        if (content == null) return null;
        return gson.fromJson(content, clazz);
    }

    public void setAttribute(String key, Object value) {
        if(value instanceof String) {
            attributes.put(key, (String) value);
            return;
        }
        attributes.put(key, gson.toJson(value));
    }

    public boolean isAlive(UUID playerId) {
        return !spectatorManager.isSpectator(playerId);
    }

    public List<UUID> getAllPlayers() {
        List<UUID> list = new ArrayList<>();
        for (Team playerTeam : teams) {
            List<UUID> players = playerTeam.getPlayers();
            list.addAll(players);
        }
        return list;
    }

    public static GameBuilder builder(JavaPlugin javaPlugin,
                                      ActiveArena intialArena,
                                      GameEventManager gameEventManager,
                                      ScoreboardManager scoreboardManager,
                                      List<Team> playerTeams,
                                      RoundCreator... stageCreators) {
        return new GameBuilder(javaPlugin, intialArena, gameEventManager, scoreboardManager, playerTeams, stageCreators);
    }
}
