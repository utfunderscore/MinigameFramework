package com.readutf.inari.test.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.event.GameEventManager;
import com.readutf.inari.core.game.GameManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class DevCommand extends BaseCommand {

    private final GameManager gameManager;
    private final ArenaManager arenaManager;
    private final GameEventManager eventManager;

//    RawDataLoader rawDataLoader;
//
//    {
//        try {
//            rawDataLoader = new RawDataLoader(null);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }


    @SneakyThrows
    @CommandAlias("dev")
    public void oneTeam(Player player) {

//        WorldServer fromWorld = ((CraftWorld) player.getWorld()).getHandle();
//        World arenaGrid = rawDataLoader.getArenaGrid();
//        WorldServer targetWorld = ((CraftWorld) arenaGrid).getHandle();
//
//
//        for (int chunkX = 0; chunkX < 20; chunkX++) {
//            for (int chunkZ = 0; chunkZ < 20; chunkZ++) {
//                player.getWorld().getChunkAt(chunkX, chunkZ).load(true);
//                 arenaGrid.getChunkAt(chunkX, chunkZ).load(true);
//            }
//        }
//
//
//        long start = System.currentTimeMillis();
//        long previousChunk = System.currentTimeMillis();
//        for (int chunkX = 0; chunkX < 20; chunkX++) {
//            for (int chunkZ = 0; chunkZ < 20; chunkZ++) {
//
//                Chunk chunk = (Chunk) fromWorld.a(chunkX, chunkZ, ChunkStatus.n, true);
//                Chunk targetChunk = (Chunk) targetWorld.a(chunkX, chunkZ, ChunkStatus.n, true);
//
//                System.out.println("since previous: " + (System.currentTimeMillis() - previousChunk));
//                copyNativeChunk(chunk, targetChunk);
//                previousChunk = System.currentTimeMillis();
//            }
//
//        }
//
//        long end = System.currentTimeMillis();
//        player.sendMessage(ChatColor.GREEN + "Copied chunks in " + (end - start) + "ms");

    }


}
