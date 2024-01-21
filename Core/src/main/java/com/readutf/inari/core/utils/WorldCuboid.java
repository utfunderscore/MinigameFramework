package com.readutf.inari.core.utils;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Consumer;

@Getter
public class WorldCuboid extends Cuboid implements Iterable<Position> {

    private final World world;

    public WorldCuboid(World world, Position min, Position max) {
        super(min, max);
        this.world = world;
    }

    public WorldCuboid(World world, double x1, double y1, double z1, double x2, double y2, double z2) {
        super(x1, y1, z1, x2, y2, z2);
        this.world = world;
    }

    public Cuboid toCuboid() {
        return new Cuboid(getMin(), getMax());
    }

    public boolean contains(Location location) {
        return location.getWorld() == world && super.contains(new Position(location));
    }

    @NotNull
    @Override
    public Iterator<Position> iterator() {
        return new PositionIterator(getMin(), getMax());
    }

    public class PositionIterator implements Iterator<Position> {

        private final int xMin;
        private final int yMin;
        private final int xMax;
        private final int yMax;
        private final int zMax;
        private int x, y, z;

        public PositionIterator(Position min, Position max) {
            this.xMin = min.getBlockX();
            this.yMin = min.getBlockY();
            this.xMax = max.getBlockX();
            this.yMax = max.getBlockY();
            this.zMax = max.getBlockZ();
            this.x = xMin;
            this.y = yMin;
            this.z = min.getBlockZ();
        }

        @Override
        public boolean hasNext() {
            return x <= xMax && y <= yMax && z <= zMax;
        }

        @Override
        public Position next() {
            Position position = new Position(x, y, z);
            if (x < xMax) {
                x++;
            } else if (y < yMax) {
                x = xMin;
                y++;
            } else if (z < zMax) {
                x = xMin;
                y = yMin;
                z++;
            }
            return position;
        }

        @Override
        public void forEachRemaining(Consumer<? super Position> action) {
            while (hasNext()) {
                action.accept(next());
            }
        }
    }

}
