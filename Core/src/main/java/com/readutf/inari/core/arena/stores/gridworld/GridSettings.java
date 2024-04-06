package com.readutf.inari.core.arena.stores.gridworld;

import lombok.Getter;

@Getter
public class GridSettings {

    private final int currentX;
    private final int currentZ;

    public GridSettings(int currentX, int currentZ) {
        this.currentX = currentX;
        this.currentZ = currentZ;
    }
}
