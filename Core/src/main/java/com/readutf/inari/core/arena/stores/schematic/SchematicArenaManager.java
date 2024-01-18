package com.readutf.inari.core.arena.stores.schematic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.readutf.inari.core.arena.WorldArena;
import com.readutf.inari.core.arena.stores.schematic.grid.GridPositionManager;
import com.readutf.inari.core.arena.Arena;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.arena.exceptions.ArenaLoadException;
import com.readutf.inari.core.arena.exceptions.ArenaStoreException;
import com.readutf.inari.core.arena.marker.MarkerScanner;
import com.readutf.inari.core.arena.meta.ArenaMeta;
import com.readutf.inari.core.logging.Logger;
import com.readutf.inari.core.logging.LoggerManager;
import com.readutf.inari.core.utils.*;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockState;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class SchematicArenaManager extends ArenaManager {

    private static final WorldCreator worldCreator = new WorldCreator("active_arenas");
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private @Getter static final World world;
    private static Logger logger = LoggerManager.getInstance().getLogger(SchematicArenaManager.class);

    static {
        worldCreator.type(WorldType.FLAT);
        worldCreator.generator(new VoidChunkGenerator());
        world = worldCreator.createWorld();

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(MaterialData.class, new MaterialDataDeserializer());
        objectMapper.registerModule(simpleModule);
    }


    private final File arenasFolder;
    private final List<ArenaMeta> availableArenas;
    private final AtomicInteger currentOffset = new AtomicInteger(0);
    private final GridPositionManager gridPositionManager;

    public SchematicArenaManager(MarkerScanner markerScanner, File pluginFolder) {
        super(markerScanner);
        this.arenasFolder = setupArenasFolder(pluginFolder);
        this.availableArenas = loadAvailableArenas();
        this.gridPositionManager = new GridPositionManager(100);
    }

    @Override
    public void save(WorldCuboid worldCuboid, Arena arena) throws ArenaStoreException {

        File arenaFolder = new File(arenasFolder, arena.getName());
        if (arenaFolder.mkdirs()) logger.fine("Created arena folder");

        arena = arena.makeRelative();

        try {
            WorldEditTaskUtil.runTask(() -> {
                saveSchematic(worldCuboid, arenaFolder);
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ArenaStoreException("Failed to save schematic.");
        }


        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(arenaFolder, "arena.json"), arena);
        } catch (IOException e) {
            throw new ArenaStoreException(e.getMessage());
        }

        availableArenas.add(arena.getArenaMeta());
    }

    private void saveSchematic(WorldCuboid worldCuboid, File arenaFolder) throws ArenaStoreException {
        long start = System.currentTimeMillis();

        CuboidRegion region = toCuboidRegion(worldCuboid);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(region.getWorld(), -1);
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
        forwardExtentCopy.setCopyingEntities(false);
        try {
            Operations.complete(forwardExtentCopy);
        } catch (WorldEditException e) {
            throw new ArenaStoreException(e.getMessage());
        }


        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(new File(arenaFolder, "arena.schematic")))) {
            writer.write(clipboard);
        } catch (IOException e) {
            throw new ArenaStoreException(e.getMessage());
        }

        logger.fine("Saved schematic in " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public WorldArena load(ArenaMeta arenaMeta) throws ArenaLoadException {

        Arena arena;

        try {
            arena = loadArena(arenaMeta);
        } catch (IOException e) {
            throw new ArenaLoadException("Failed to parse json.", e);
        } catch (WorldEditException e) {
            throw new ArenaLoadException("Failed to load schematic.", e);
        }
        if (arena == null) throw new ArenaLoadException("Arena not found");


        return new WorldArena(world, arena);
    }

    @Override
    public void unload(Arena arena) {

        Cuboid bounds = arena.getBounds();
        Position min = bounds.getMin();


        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            BlockState blockState = BukkitAdapter.adapt(Material.AIR.createBlockData());
            Position max = bounds.getMax();
            editSession.setBlocks((Region) toCuboidRegion(new WorldCuboid(world, min, max)), blockState);

            editSession.setFastMode(true);

            Operations.complete(editSession.commit());

            logger.debug("Filling " + min.getX() + ", " + min.getY() + ", " + min.getZ() + " - " + max.getX() + ", " + max.getY() + ", " + +max.getZ());
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }

        gridPositionManager.free(new GridPositionManager.GridSpace((int) min.getX(), (int) min.getZ()));

        logger.fine("Unloaded arena " + arena.getName());
    }

    public Arena loadArena(ArenaMeta arenaMeta) throws IOException, WorldEditException {
        File arenaFolder = new File(arenasFolder, arenaMeta.getName());
        if (!arenaFolder.exists()) return null;

        Arena arena = objectMapper.readValue(new File(arenaFolder, "arena.json"), Arena.class);

        GridPositionManager.GridSpace gridSpace = gridPositionManager.next();
        Position origin = new Position(gridSpace.getX(), 5, gridSpace.getZ());

        arena = arena.makeRelative(origin);

        pasteSchematic(arenaFolder, origin);


        return arena;
    }

    private static void pasteSchematic(File arenaFolder, Position origin) throws IOException {
        long start = System.currentTimeMillis();

        ClipboardReader reader = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(new FileInputStream(new File(arenaFolder, "arena.schematic")));
        Clipboard read = reader.read();

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            Operation pasteOperation = new ClipboardHolder(read).createPaste(editSession)
                    .to(BlockVector3.at(origin.getX(), origin.getY(), origin.getZ()))
                    .ignoreAirBlocks(true)
                    .build();

            Operations.complete(pasteOperation);
        }

        logger.fine("Pasted schematic at " + origin + " in " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public @Nullable List<ArenaMeta> findAvailableArenas(Predicate<ArenaMeta> predicate) {
        return availableArenas.stream().filter(predicate).toList();
    }

    public List<ArenaMeta> loadAvailableArenas() {
        List<ArenaMeta> available = new ArrayList<>();

        File[] files = arenasFolder.listFiles();
        if (files == null) return Collections.emptyList();
        for (File file : files) {
            if (file.isDirectory()) {
                File arenaFile = new File(file, "arena.json");
                if (arenaFile.exists()) {
                    try {
                        Arena arena = objectMapper.readValue(arenaFile, Arena.class);
                        available.add(arena.getArenaMeta());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return available;
    }

    @NotNull
    private File setupArenasFolder(File pluginFolder) {
        final File arenasFolder;
        arenasFolder = new File(pluginFolder, "arenas");
        if (arenasFolder.mkdirs()) System.out.println("Created arenas folder");
        return arenasFolder;
    }

    public CuboidRegion toCuboidRegion(WorldCuboid worldCuboid) {
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(worldCuboid.getWorld());
        BlockVector3 min = BlockVector3.at(worldCuboid.getMin().getX(), worldCuboid.getMin().getY(), worldCuboid.getMin().getZ());
        BlockVector3 max = BlockVector3.at(worldCuboid.getMax().getX(), worldCuboid.getMax().getY(), worldCuboid.getMax().getZ());

        return new CuboidRegion(world, min, max);
    }

    public static class VoidChunkGenerator extends ChunkGenerator {

        @Override
        public List<BlockPopulator> getDefaultPopulators(World world) {return List.of();}

        @Override
        public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ,
                                  @NotNull ChunkData chunkData) {}

        @Override
        public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ,
                                    @NotNull ChunkData chunkData) {}

        @Override
        public void generateBedrock(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ,
                                    @NotNull ChunkData chunkData) {}

        @Override
        public void generateCaves(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ,
                                  @NotNull ChunkData chunkData) {}

        @Override
        @Nullable
        public BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {return new VoidBiomeProvider();}

        @Override
        public boolean canSpawn(World world, int x, int z) {return true;}

        @Override
        public Location getFixedSpawnLocation(World world, Random random) {return new Location(world, 0, 100, 0);}
    }

    private static class VoidBiomeProvider extends BiomeProvider {

        @Override
        public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {return Biome.THE_VOID;}

        @Override
        public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {return List.of(Biome.THE_VOID);}

    }

}
