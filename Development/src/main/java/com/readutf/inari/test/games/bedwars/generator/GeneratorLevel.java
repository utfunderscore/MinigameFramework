package com.readutf.inari.test.games.bedwars.generator;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.List;

@Getter
public class GeneratorLevel {

    private final List<GeneratorItem> items;
    private final Duration upgradeTime;

    public GeneratorLevel(List<GeneratorItem> items, Duration upgradeTime) {
        this.items = items;
        this.upgradeTime = upgradeTime;
    }

}
