package com.readutf.inari.test;

import co.aikar.commands.PaperCommandManager;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.arena.marker.impl.TileEntityScanner;
import com.readutf.inari.core.arena.selection.impl.WorldEditSelectionManager;
import com.readutf.inari.core.arena.stores.gridloader.SchematicArenaManager;
import com.readutf.inari.core.arena.stores.gridloader.loader.impl.WorldEditLoader;
import com.readutf.inari.core.commands.ArenaCommands;
import com.readutf.inari.core.commands.EventDebugCommand;
import com.readutf.inari.core.commands.completions.GameCompletions;
import com.readutf.inari.core.event.GameEventManager;
import com.readutf.inari.core.game.GameManager;
import com.readutf.inari.core.scoreboard.ScoreboardManager;
import com.readutf.inari.test.commands.DevCommand;
import com.readutf.inari.test.commands.GameCommand;
import com.readutf.inari.test.games.GameStarterManager;
import com.readutf.inari.test.listeners.DemoListeners;
import lombok.Getter;
import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow;
import me.lucko.spark.api.statistic.misc.DoubleAverageInfo;
import me.lucko.spark.api.statistic.types.GenericStatistic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Getter
public class InariDemo extends JavaPlugin {

    private @Getter static InariDemo instance;

    private ArenaManager arenaManager;
    private GameManager gameManager;
    private GameStarterManager gameStarterManager;
    private GameEventManager gameEventManager;

    public InariDemo() {
        instance = this;
    }

    @Override
    public void onEnable() {

        WorldEditSelectionManager worldEditSelectionManager = new WorldEditSelectionManager();
        this.arenaManager = new SchematicArenaManager(this, new TileEntityScanner(), new WorldEditLoader(this), getDataFolder());
        this.gameManager = new GameManager();
        this.gameEventManager = new GameEventManager(this, gameManager);
        this.gameStarterManager = new GameStarterManager(arenaManager, gameManager, new ScoreboardManager(this), gameEventManager);

        PaperCommandManager paperCommandManager = new PaperCommandManager(this);

        paperCommandManager.getCommandCompletions().registerCompletion("gameids", new GameCompletions.GameIdCompletion(gameManager));
        paperCommandManager.getCommandCompletions().registerCompletion("gameplayers", new GameCompletions.GamePlayersCompletion(gameManager));
        paperCommandManager.getCommandCompletions().registerCompletion("gametypes", c -> gameStarterManager.getGameStarters());


        paperCommandManager.registerCommand(new ArenaCommands(this, worldEditSelectionManager, arenaManager));
        paperCommandManager.registerCommand(new DevCommand(gameManager, arenaManager, gameEventManager));
        paperCommandManager.registerCommand(new EventDebugCommand(gameEventManager));
        paperCommandManager.registerCommand(new GameCommand(gameStarterManager));


        Bukkit.getPluginManager().registerEvents(new DemoListeners(), this);
    }

    @Override
    public void onDisable() {

        arenaManager.shutdown();
        gameManager.shutdown();

    }
}
