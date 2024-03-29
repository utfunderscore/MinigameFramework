package com.readutf.inari.test;

import co.aikar.commands.PaperCommandManager;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.arena.marker.impl.TileEntityScanner;
import com.readutf.inari.core.arena.selection.impl.WorldEditSelectionManager;
import com.readutf.inari.core.arena.stores.gridloader.SchematicArenaManager;
import com.readutf.inari.core.arena.stores.gridloader.loader.impl.WorldEditLoader;
import com.readutf.inari.core.commands.ArenaCommands;
import com.readutf.inari.core.commands.EventDebugCommand;
import com.readutf.inari.core.event.GameEventManager;
import com.readutf.inari.core.game.GameManager;
import com.readutf.inari.test.commands.DevCommand;
import com.readutf.inari.test.commands.GameCommand;
import com.readutf.inari.test.games.GameStarterManager;
import com.readutf.inari.test.listeners.DemoListeners;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class InariDemo extends JavaPlugin {

    private @Getter static InariDemo instance;

    private ArenaManager arenaManager;
    private GameManager gameManager;
    private GameStarterManager gameStarterManager;

    public InariDemo() {
        instance = this;
    }

    @Override
    public void onEnable() {

        WorldEditSelectionManager worldEditSelectionManager = new WorldEditSelectionManager();
        this.arenaManager = new SchematicArenaManager(this, new TileEntityScanner(), new WorldEditLoader(this), getDataFolder());
        this.gameManager = new GameManager();
        GameEventManager gameEventManager = new GameEventManager(this, gameManager);

        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new ArenaCommands(this, worldEditSelectionManager, arenaManager));
        paperCommandManager.registerCommand(new DevCommand(gameManager, arenaManager, gameEventManager));
        paperCommandManager.registerCommand(new EventDebugCommand(gameEventManager));
        this.gameStarterManager = new GameStarterManager(arenaManager, gameManager, gameEventManager);
        GameCommand gameCommand = new GameCommand(gameStarterManager);
        paperCommandManager.registerCommand(gameCommand);

        paperCommandManager.getCommandCompletions().registerCompletion("games", c -> gameStarterManager.getGameStarters());

        Bukkit.getPluginManager().registerEvents(new DemoListeners(), this);

    }

    @Override
    public void onDisable() {

        arenaManager.shutdown();
        gameManager.shutdown();

    }
}
