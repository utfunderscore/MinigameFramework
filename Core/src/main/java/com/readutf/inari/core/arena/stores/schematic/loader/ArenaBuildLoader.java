package com.readutf.inari.core.arena.stores.schematic.loader;

import com.readutf.inari.core.arena.exceptions.ArenaStoreException;
import com.readutf.inari.core.utils.Position;
import com.readutf.inari.core.utils.WorldCuboid;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;

public interface ArenaBuildLoader  {

    void pasteSchematic(World world, File arenaFolder, Position origin) throws IOException;

    void saveSchematic(WorldCuboid worldCuboid, File arenaFolder) throws ArenaStoreException;

}
