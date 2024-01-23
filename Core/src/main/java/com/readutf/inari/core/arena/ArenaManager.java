package com.readutf.inari.core.arena;

import com.readutf.inari.core.arena.exceptions.ArenaLoadException;
import com.readutf.inari.core.arena.exceptions.ArenaStoreException;
import com.readutf.inari.core.arena.marker.Marker;
import com.readutf.inari.core.arena.marker.MarkerScanner;
import com.readutf.inari.core.arena.meta.ArenaMeta;
import com.readutf.inari.core.utils.WorldCuboid;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.BlockSign;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.craftbukkit.v1_20_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
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

        Arena arena = new Arena(name, cuboid.toCuboid(), new ArenaMeta(name, "test", new MaterialData(Material.PAPER)), markers);

        for (Marker marker : markers) {
            Block block = marker.getPosition().toLocation(cuboid.getWorld()).getBlock();
            block.setType(Material.AIR);

        }
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
