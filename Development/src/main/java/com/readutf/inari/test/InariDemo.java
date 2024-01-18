package com.readutf.inari.test;

import co.aikar.commands.PaperCommandManager;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.arena.marker.impl.TileEntityScanner;
import com.readutf.inari.core.arena.selection.impl.WorldEditSelectionManager;
import com.readutf.inari.core.arena.stores.schematic.SchematicArenaManager;
import com.readutf.inari.core.commands.ArenaCommands;
import com.readutf.inari.core.commands.EventDebugCommand;
import com.readutf.inari.core.event.GameEventManager;
import com.readutf.inari.core.game.GameManager;
import com.readutf.inari.test.commands.DevCommand;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class InariDemo extends JavaPlugin {

    private @Getter static InariDemo instance;

    public InariDemo() {
        instance = this;
    }

    @Override
    public void onEnable() {

        WorldEditSelectionManager worldEditSelectionManager = new WorldEditSelectionManager();
        ArenaManager arenaManager = new SchematicArenaManager(new TileEntityScanner(), getDataFolder());
        GameManager gameManager = new GameManager();
        GameEventManager gameEventManager = new GameEventManager(this, gameManager);

        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new ArenaCommands(this, worldEditSelectionManager, arenaManager));
        paperCommandManager.registerCommand(new DevCommand(gameManager, arenaManager, gameEventManager));
        paperCommandManager.registerCommand(new EventDebugCommand(gameEventManager));

    }
}
