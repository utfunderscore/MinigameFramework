package com.readutf.inari.test.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import com.readutf.inari.core.arena.ActiveArena;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.arena.exceptions.ArenaLoadException;
import com.readutf.inari.core.arena.meta.ArenaMeta;
import com.readutf.inari.core.arena.stores.gridworld.GridArenaManager;
import com.readutf.inari.core.arena.stores.worldloader.WorldArenaManager;
import com.readutf.inari.core.event.GameEventManager;
import com.readutf.inari.core.game.GameManager;
import com.readutf.inari.core.utils.ChunkCopy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DevCommand extends BaseCommand {

    private final WorldArenaManager worldArenaManager;

    public DevCommand(WorldArenaManager worldArenaManager) {
        this.worldArenaManager = worldArenaManager;
    }

    @CommandAlias("test-world-arena")
    public void test(Player player, String arenaName, int numOfArenas) {

        ArenaMeta found = worldArenaManager.findAvailableArenas(arenaMeta -> arenaMeta.getName().equalsIgnoreCase(arenaName)).stream().findFirst().orElse(null);
        if (found == null) {
            player.sendMessage("No arena found with the name " + arenaName);
            return;
        }

        long start = System.currentTimeMillis();

        List<CompletableFuture<ActiveArena>> futures = IntStream.range(0, numOfArenas).mapToObj(value -> {
            try {
                return worldArenaManager.load(found);
            } catch (ArenaLoadException e) {
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).toList();

        CompletableFuture<Void> allLoaded = CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));

        allLoaded.thenAccept(unused -> {
            player.sendMessage(futures.size() + " arenas loaded in " + (System.currentTimeMillis() - start) + "ms");

            for (CompletableFuture<ActiveArena> future : futures) {
                ActiveArena join = future.join();
                join.free();
            }

        });


    }

}
