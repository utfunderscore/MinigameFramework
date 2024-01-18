package com.readutf.inari.core.arena;

import com.readutf.inari.core.arena.Arena;
import lombok.Getter;
import org.bukkit.World;

@Getter
public class WorldArena extends Arena {

    private final World world;

    public WorldArena(World world, Arena arena) {
        super(arena.getName(), arena.getBounds(), arena.getArenaMeta(), arena.getMarkers());
        this.world = world;
    }



}
