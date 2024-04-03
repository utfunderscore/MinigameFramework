package com.readutf.inari.test.games.bedwars.generator;

import com.readutf.inari.core.game.Game;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public class Generator {

    private final Game game;
    private final UUID id;
    private final Location location;
    private final List<String> loreLines;
    private final GeneratorLevel currentLevel;
    private final List<GeneratorLevel> levels;

    public Generator(Game game, Location location, List<String> loreLines, List<GeneratorLevel> levels) {
        this.game = game;
        this.id = UUID.randomUUID();
        this.location = location;
        this.loreLines = loreLines;
        this.currentLevel = levels.get(0);
        this.levels = levels;
    }

    public boolean hasItem(ItemStack itemStack) {
        for (GeneratorLevel generatorLevel : levels) {
            for (GeneratorItem generatorItem : generatorLevel.getItems()) {
                if (generatorItem.getItem().isSimilar(itemStack)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void tick() {
        GeneratorLevel currentLevel = getCurrentLevel();

        for (GeneratorItem item : currentLevel.getItems()) {
            int lastDrop = item.getLastDrop();

            if(MinecraftServer.currentTick - lastDrop >= item.getTicksBetweenDrop()) {
                item.setLastDrop(MinecraftServer.currentTick);
                item.drop(this, location);
            }

        }
    }

}
