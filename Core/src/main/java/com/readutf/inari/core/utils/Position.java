package com.readutf.inari.core.utils;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
public class Position {

    private final double x, y, z;

    public Position(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Position(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }

    /**
     * Adds the offset to the position
     * @param offsetX the x offset to add
     * @param offsetY the y offset to add
     * @param offsetZ the z offset to add
     * @return the new position
     */
    public Position add(double offsetX, double offsetY, double offsetZ) {
        return new Position(x + offsetX, y + offsetY, z + offsetZ);
    }

    /**
     * Adds the offset to the position
     * @param position the position to add
     * @return the new position
     */
    public Position add(Position position) {
        return add(position.x, position.y, position.z);
    }

}
