package com.readutf.inari.test.games.bedwars.generator;

import com.readutf.inari.core.game.Game;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class GeneratorManager extends BukkitRunnable {

    private final HashMap<UUID, Generator> generators;

    public GeneratorManager(Game game) {
        this.generators = new HashMap<>();
        runTaskTimer(game.getJavaPlugin(), 0, 1);
    }

    public @Nullable Generator getNearbyGenerator(ItemStack itemStack, Location location) {

        for (Generator value : generators.values()) {
            if (value.getLocation().distance(location) < 3 && value.hasItem(itemStack)) {
                return value;
            }
        }

        return null;
    }

    public void createGenerator(Generator generator) {
        generators.put(generator.getId(), generator);
    }

    public Generator getGenerator(UUID id) {
        return generators.get(id);
    }

    @Override
    public void run() {

        for (Generator generator : generators.values()) {
            generator.tick();

        }

    }
}
