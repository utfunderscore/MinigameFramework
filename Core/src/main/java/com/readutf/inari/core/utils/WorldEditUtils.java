package com.readutf.inari.core.utils;

import com.readutf.inari.core.InariCore;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Bukkit;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorldEditUtils {

    private static boolean isFawe;
    private static ExecutorService executor = Executors.newCachedThreadPool();

    static {
        try {
            Class.forName("com.fastasyncworldedit.core.FaweAPI");
            isFawe = true;
        } catch (ClassNotFoundException e) {
            isFawe = false;
        }
    }

    public static <T> CompletableFuture<T> runTask(Callable<T> runnable) {
        if (isFawe) {
            try {
                return CompletableFuture.completedFuture(runnable.call());
            } catch (Exception e) {
                return CompletableFuture.failedFuture(e);
            }
        } else {

            CompletableFuture<T> future = new CompletableFuture<>();
            Bukkit.getScheduler().runTask(InariCore.getInstance().getJavaPlugin(), () -> {
                try {
                    future.complete(runnable.call());
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });
            return future;
        }
    }

    public static CuboidRegion toCuboidRegion(WorldCuboid worldCuboid) {
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(worldCuboid.getWorld());
        BlockVector3 min = BlockVector3.at(worldCuboid.getMin().getX(), worldCuboid.getMin().getY(), worldCuboid.getMin().getZ());
        BlockVector3 max = BlockVector3.at(worldCuboid.getMax().getX(), worldCuboid.getMax().getY(), worldCuboid.getMax().getZ());

        return new CuboidRegion(world, min, max);
    }
}
