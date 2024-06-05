package com.readutf.inari.core.arena;

import lombok.Getter;
import org.bukkit.World;

import java.util.function.Consumer;

@Getter
public class ActiveArena extends Arena {

    private final World world;
    private final Consumer<ActiveArena> freeArenaFunction;

    public ActiveArena(World world, Arena arena, Consumer<ActiveArena> freeArenaFunction) {
        super(arena.getName(), arena.getBounds(), arena.getArenaMeta(), arena.getMarkers());
        this.world = world;
        this.freeArenaFunction = freeArenaFunction;
    }

    public void free() {
        freeArenaFunction.accept(this);
    }


}
