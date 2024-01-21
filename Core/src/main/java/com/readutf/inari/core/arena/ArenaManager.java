package com.readutf.inari.core.arena;

import com.readutf.inari.core.arena.exceptions.ArenaLoadException;
import com.readutf.inari.core.arena.exceptions.ArenaStoreException;
import com.readutf.inari.core.arena.marker.Marker;
import com.readutf.inari.core.arena.marker.MarkerScanner;
import com.readutf.inari.core.arena.meta.ArenaMeta;
import com.readutf.inari.core.utils.WorldCuboid;
import net.minecraft.world.level.block.BlockSign;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public abstract class ArenaManager {

    private final MarkerScanner markerScanner;
    private final Map<String, Arena> arenaTemplates;

    public ArenaManager(MarkerScanner markerScanner) {
        this.markerScanner = markerScanner;
        this.arenaTemplates = new HashMap<>();
    }

    public Arena createArena(String name, WorldCuboid cuboid) throws ArenaStoreException {
        List<Marker> markers = markerScanner.scan(cuboid);
        for (Marker marker : markers) {
            Block signBlock = marker.getPosition().toLocation(cuboid.getWorld()).getBlock();
            signBlock.setType(Material.AIR);
            Block above = signBlock.getRelative(BlockFace.UP);
            if(above.getType() == Material.SKELETON_SKULL) {
                above.setType(Material.AIR);
            }
        }

        Arena arena = new Arena(name, cuboid.toCuboid(), new ArenaMeta(name, "test", new MaterialData(Material.PAPER)), markers);

        save(cuboid, arena);
        arenaTemplates.put(name, arena);

        return arena;
    }

    protected abstract void save(WorldCuboid worldCuboid, Arena arena) throws ArenaStoreException;

    public void shutdown() {}

    public abstract ActiveArena load(ArenaMeta arenaMeta) throws ArenaLoadException;

    public abstract void unload(Arena arena);

    public abstract List<ArenaMeta> findAvailableArenas(Predicate<ArenaMeta> predicate);



}
