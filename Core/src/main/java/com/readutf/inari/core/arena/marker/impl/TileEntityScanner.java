package com.readutf.inari.core.arena.marker.impl;

import com.readutf.inari.core.arena.marker.Marker;
import com.readutf.inari.core.arena.marker.MarkerScanner;
import com.readutf.inari.core.logging.GameLoggerFactory;
import com.readutf.inari.core.logging.Logger;
import com.readutf.inari.core.logging.LoggerFactory;
import com.readutf.inari.core.utils.WorldCuboid;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TileEntityScanner implements MarkerScanner {

    private static Logger logger = LoggerFactory.getLogger(TileEntityScanner.class);

    @Override
    public List<Marker> scan(WorldCuboid worldCuboid) {
        long start = System.currentTimeMillis();

        World world = worldCuboid.getWorld();


        ArrayList<Marker> signs = new ArrayList<>();

        int startX = (int) worldCuboid.getMin().getX();
        int startZ = (int) worldCuboid.getMin().getZ();

        int endX = (int) worldCuboid.getMax().getX();
        int endZ = (int) worldCuboid.getMax().getZ();

        HashMap<String, Chunk> chunks = new HashMap<>();

        for (int x = startX; x < endX; x++) {
            for (int z = startZ; z < endZ; z++) {
                String key = (x >> 4) + "" + (z >> 4);
                if(chunks.containsKey(key)) continue;
                chunks.put(key, world.getChunkAt(x >> 4, z >> 4));
            }
        }

        for (Chunk chunk : chunks.values()) {

            if(!chunk.isLoaded()) chunk.load();

            for (BlockState tileEntity : chunk.getTileEntities()) {

                if (tileEntity instanceof Sign) {

                    if(!worldCuboid.contains(tileEntity.getLocation())) continue;

                    Marker marker = Marker.parseFromSign(tileEntity.getLocation());
                    if (marker != null) signs.add(marker);
                }

            }
        }

        logger.debug("Scanned " + signs.size() + " signs in " + (System.currentTimeMillis() - start) + "ms");

        return signs;
    }
}
