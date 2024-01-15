package com.readutf.inari.core.utils;

import lombok.Getter;

@Getter
public class Cuboid {

    private final Position min, max;

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

}
