package com.readutf.inari.test.utils;

import com.readutf.inari.test.InariDemo;
import org.bukkit.Bukkit;

public class ThreadUtils {

    public static void ensureSync(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else {
            Bukkit.getScheduler().runTask(InariDemo.getInstance(), runnable);
        }
    }

}
