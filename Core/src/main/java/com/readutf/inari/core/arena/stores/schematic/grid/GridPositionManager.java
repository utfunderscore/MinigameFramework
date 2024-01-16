package com.readutf.inari.core.arena.stores.schematic.grid;

import com.readutf.inari.core.logging.Logger;
import com.readutf.inari.core.logging.LoggerManager;
import lombok.Getter;

import java.util.ArrayDeque;

public class GridPositionManager {

    private static Logger logger = LoggerManager.loggerManager.getLogger(GridPositionManager.class);

    private final ArrayDeque<GridSpace> recentlyFreed = new ArrayDeque<>();
    private final int spaceBetween;

    private int multiplier = 1;
    private int x = 0;
    private int z = 0;
    private int currentStep = 2;
    private int xStep = 1;
    private int zStep = 1;

    public GridPositionManager(int spaceBetween) {
        this.spaceBetween = spaceBetween;
    }

    public GridSpace next() {

        if(!recentlyFreed.isEmpty()) {
            return recentlyFreed.pollFirst();
        }

        if(xStep < currentStep) {
            x += (multiplier);
            xStep++;

            GridSpace gridSpace = new GridSpace(x, z).multiply(spaceBetween);
            logger.debug("Reserving arena grid space " + gridSpace);
            return gridSpace;
        }

        if (zStep < currentStep) {
            z += (multiplier);
            zStep++;

            GridSpace gridSpace = new GridSpace(x, z).multiply(spaceBetween);
            logger.debug("Reserving arena grid space " + gridSpace);
            return gridSpace;
        }

        multiplier = multiplier * -1;
        currentStep++;
        xStep = 1;
        zStep = 1;

        return next();
    }

    public void free(GridSpace space) {
        recentlyFreed.addLast(space);
        logger.debug("Freed arena grid space " + space);
    }

    @Override
    public String toString() {
        return "Test{" +
                "multiplier=" + multiplier +
                ", x=" + x +
                ", y=" + z +
                ", currentStep=" + currentStep +
                ", xStep=" + xStep +
                ", yStep=" + zStep +
                '}';
    }

    @Getter
    public static class GridSpace {

        private final int x;
        private final int z;

        public GridSpace(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public GridSpace multiply(int multiplier) {
            return new GridSpace(x * multiplier, z * multiplier);
        }

        @Override
        public String toString() {
            return "GridSpace{" +
                    "x=" + x +
                    ", y=" + z +
                    '}';
        }
    }

}
