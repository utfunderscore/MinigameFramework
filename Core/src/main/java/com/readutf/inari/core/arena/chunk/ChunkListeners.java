package com.readutf.inari.core.arena.chunk;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChunkListeners implements Listener {

    private static boolean registered = false;

    public static void register(JavaPlugin javaPlugin) {
        if(!registered) {
            javaPlugin.getServer().getPluginManager().registerEvents(new ChunkListeners(), javaPlugin);
            registered = true;
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        System.out.printf("Loading chunk at (%d %d)%n", chunk.getX(), chunk.getZ());
    }

}
