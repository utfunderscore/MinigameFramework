package com.readutf.inari.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;

public class ThreadUtils {

    public static CompletableFuture<Void> ensureSync(JavaPlugin javaPlugin, Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
            return CompletableFuture.completedFuture(null);
        } else {
            CompletableFuture<Void> future = new CompletableFuture<>();
            Bukkit.getScheduler().runTask(javaPlugin, new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                    future.complete(null);
                }
            });
            return future;
        }
    }

}
