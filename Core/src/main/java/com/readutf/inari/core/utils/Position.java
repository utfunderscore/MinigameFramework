package com.readutf.inari.core.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
public class Position {

    private final double x, y, z;

    @JsonCreator
    public Position(@JsonProperty("x") double x, @JsonProperty("y") double y, @JsonProperty("z") double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Position(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    @JsonIgnore
    public int getBlockX() {
        return (int) Math.floor(x);
    }

    @JsonIgnore
    public int getBlockY() {
        return (int) Math.floor(y);
    }

    @JsonIgnore
    public int getBlockZ() {
        return (int) Math.floor(z);
    }

    public static Position fromLocation(Location location) {
        return new Position(location);
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
     * Subtracts the offset from the position
     * @param offsetX the x offset to subtract
     * @param offsetY the y offset to subtract
     * @param offsetZ the z offset to subtract
     * @return the new position
     */
    public Position subtract(double offsetX, double offsetY, double offsetZ) {
        return new Position(x - offsetX, y - offsetY, z - offsetZ);
    }

    /**
     * Adds the offset to the position
     * @param position the position to add
     * @return the new position
     */
    public Position add(Position position) {
        return add(position.x, position.y, position.z);
    }

    /**
     * Subtracts the offset from the position
     * @param position the position to subtract
     * @return the new position
     */
    public Position subtract(Position position) {
        return subtract(position.x, position.y, position.z);
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
