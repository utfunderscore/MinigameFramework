package com.readutf.inari.core.utils;

import com.readutf.inari.core.InariCore;
import org.bukkit.Bukkit;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorldEditTaskUtil {

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
        System.out.println("isFawe = " + isFawe);


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
}
