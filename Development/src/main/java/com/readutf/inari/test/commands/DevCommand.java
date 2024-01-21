package com.readutf.inari.test.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import com.readutf.inari.core.arena.ActiveArena;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.arena.exceptions.ArenaLoadException;
import com.readutf.inari.core.arena.meta.ArenaMeta;
import com.readutf.inari.core.arena.stores.gridloader.loader.impl.RawDataLoader;
import com.readutf.inari.core.event.GameEventManager;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameManager;
import com.readutf.inari.core.game.exception.GameException;
import com.readutf.inari.core.game.spawning.impl.TeamBasedSpawning;
import com.readutf.inari.core.game.team.Team;
import com.readutf.inari.test.InariDemo;
import com.readutf.inari.test.payload.PayloadRound;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.PacketPlayOutMap;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IChunkAccess;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.readutf.inari.core.arena.stores.gridloader.loader.impl.RawDataLoader.copyNativeChunk;

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
