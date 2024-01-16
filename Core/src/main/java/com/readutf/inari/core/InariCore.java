package com.readutf.inari.core;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class InariCore {

    private @Getter static InariCore instance;

    private final JavaPlugin javaPlugin;

    public InariCore(JavaPlugin javaPlugin) {
        instance = this;
        this.javaPlugin = javaPlugin;
    }



}
