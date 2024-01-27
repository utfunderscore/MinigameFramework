package com.readutf.inari.core.utils;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Consumer;

@Getter
public class Cuboid implements Iterable<Position> {

    private final Position min, max;

    public Cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.min = new Position(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
        this.max = new Position(Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
    }


    public Cuboid(Position min, Position max) {
        this.min = new Position(Math.min(min.getX(), max.getX()), Math.min(min.getY(), max.getY()), Math.min(min.getZ(), max.getZ()));
        this.max = new Position(Math.max(min.getX(), max.getX()), Math.max(min.getY(), max.getY()), Math.max(min.getZ(), max.getZ()));
    }

    public boolean contains(Position position) {
        return position.getX() >= min.getX()
                && position.getX() <= max.getX()
                && position.getY() >= min.getY()
                && position.getY() <= max.getY()
                && position.getZ() >= min.getZ()
                && position.getZ() <= max.getZ();
    }

    @Override
    public String toString() {
        return "Cuboid{" +
                "min=" + min +
                ", max=" + max +
                '}';
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
