package com.readutf.inari.core.utils;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
public class Position {

    private final double x, y, z;

    /**
     * Creates a new position
     * @param x the x position
     * @param y the y position
     * @param z the z position
     */
    public Position(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Converts a location to a position
     * @param location the location to convert
     */
    public Position(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    /**
     * Returns the current position with the X and Y centered
     * @return the new centered position
     */
    @JsonIgnore
    public Position center() {
        return new Position(getBlockX() + 0.5, getBlockY(), getBlockZ() + 0.5);
    }

    /**
     * Shortcut for {@link #Position(Location)}
     * @param location the location to convert
     * @return the new position
     */
    public static Position fromLocation(Location location) {
        return new Position(location);
    }

    /**
     * Converts the position to a location
     * @param world the world to convert to
     * @return the new location
     */
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

    /**
     * Returns the block X position
     * @return the block X position
     */
    @JsonIgnore
    public int getBlockX() {
        return (int) Math.floor(x);
    }

    /**
     * Returns the block Y position
     * @return the block Y position
     */
    @JsonIgnore
    public int getBlockY() {
        return (int) Math.floor(y);
    }

    /**
     * Returns the block Z position
     * @return the block Z position
     */
    @JsonIgnore
    public int getBlockZ() {
        return (int) Math.floor(z);
    }

    /**
     * Convert the position to a string
     * @return the string
     */
    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
