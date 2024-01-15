package com.readutf.inari.core.utils;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
public class WorldCuboid extends Cuboid{

    private final World world;

    public WorldCuboid(World world, Position min, Position max) {
        super(min, max);
        this.world = world;
    }

    public boolean contains(Location location) {
        return location.getWorld() == world && super.contains(new Position(location));
    }

}
