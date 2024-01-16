package com.readutf.inari.core.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Cuboid {

    private final Position min, max;

    public Cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.min = new Position(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2));
        this.max = new Position(Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
    }

    @JsonCreator
    public Cuboid(@JsonProperty("min") Position min, @JsonProperty("max") Position max) {
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
}
