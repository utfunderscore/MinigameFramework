package com.readutf.inari.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ThreadUtils {

    public static void ensureSync(JavaPlugin javaPlugin, Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTask(javaPlugin, runnable);
        }
    }

}
