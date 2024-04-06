package com.readutf.inari.core.arena.stores.gridworld;

import net.minecraft.server.MinecraftServer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayDeque;
import java.util.concurrent.CompletableFuture;

public class ChunkCopyBalancer extends BukkitRunnable {

    private final ArrayDeque<Runnable> chunkPasteTasks;
    private final int maxChunksPerTick;
    private final CompletableFuture<Boolean> future;

    public ChunkCopyBalancer(ArrayDeque<Runnable> chunkPasteTasks, int maxChunksPerTick) {
        this.chunkPasteTasks = chunkPasteTasks;
        this.maxChunksPerTick = maxChunksPerTick;
        this.future = new CompletableFuture<>();
    }

    @Override
    public void run() {

        long timeTaken = 0;

        int pasted = 0;

        if(MinecraftServer.currentTick == 0) {
            for (Runnable chunkPasteTask : chunkPasteTasks) {
                chunkPasteTask.run();
            }
            return;
        }

        while (pasted < maxChunksPerTick && !chunkPasteTasks.isEmpty()) {
            long start = System.currentTimeMillis();
            Runnable poll = chunkPasteTasks.poll();
            poll.run();
            timeTaken += System.currentTimeMillis() - start;
            pasted++;
        }
        System.out.println("pasted: " + pasted + " chunks in " + timeTaken + "ms");


        if (chunkPasteTasks.isEmpty()) {
            future.complete(true);
            cancel();
        }
    }

    public CompletableFuture<Boolean> start(JavaPlugin javaPlugin) {
        if(MinecraftServer.currentTick == 0) {
            run();
            return CompletableFuture.completedFuture(true);
        }
        runTaskTimer(javaPlugin, 0, 1);
        return future;
    }
}