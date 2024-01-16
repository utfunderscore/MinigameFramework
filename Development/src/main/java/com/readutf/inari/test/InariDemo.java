package com.readutf.inari.test;

import co.aikar.commands.PaperCommandManager;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.arena.marker.impl.TileEntityScanner;
import com.readutf.inari.core.arena.selection.impl.WorldEditSelectionManager;
import com.readutf.inari.core.arena.stores.SchematicArenaManager;
import com.readutf.inari.core.commands.ArenaCommands;
import org.bukkit.plugin.java.JavaPlugin;

public class InariDemo extends JavaPlugin {

    @Override
    public void onEnable() {

        WorldEditSelectionManager worldEditSelectionManager = new WorldEditSelectionManager();
        ArenaManager arenaManager = new SchematicArenaManager(new TileEntityScanner(), getDataFolder());

        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new ArenaCommands(this, worldEditSelectionManager, arenaManager));

    }
}
