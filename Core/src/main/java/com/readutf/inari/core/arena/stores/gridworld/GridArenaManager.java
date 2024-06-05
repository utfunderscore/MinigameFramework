package com.readutf.inari.core.arena.stores.gridworld;

import com.fastasyncworldedit.core.Fawe;
import com.fastasyncworldedit.core.FaweAPI;
import com.fastasyncworldedit.core.extent.processor.lighting.RelightMode;
import com.fastasyncworldedit.core.extent.processor.lighting.Relighter;
import com.google.gson.Gson;
import com.readutf.inari.core.arena.ActiveArena;
import com.readutf.inari.core.arena.Arena;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.arena.GridPositionManager;
import com.readutf.inari.core.arena.exceptions.ArenaLoadException;
import com.readutf.inari.core.arena.marker.MarkerScanner;
import com.readutf.inari.core.arena.meta.ArenaMeta;
import com.readutf.inari.core.arena.stores.schematic.SchematicArenaManager;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.logging.Logger;
import com.readutf.inari.core.logging.LoggerFactory;
import com.readutf.inari.core.utils.ChunkCopy;
import com.readutf.inari.core.utils.Cuboid;
import com.readutf.inari.core.utils.Position;
import com.readutf.inari.core.utils.WorldCuboid;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class GridArenaManager extends ArenaManager {

    private static final Logger logger = LoggerFactory.getLogger(GridArenaManager.class);
    private static final Gson gson = new Gson();
    private static final World templateArenaWorld;
    private @Getter static final World activeArenasWorld;

    static {

        WorldCreator worldCreator = new WorldCreator("template_arenas");
        worldCreator.type(WorldType.FLAT);
        worldCreator.generator(new SchematicArenaManager.VoidChunkGenerator());
        templateArenaWorld = worldCreator.createWorld();
        templateArenaWorld.setAutoSave(false);

        WorldCreator activeArenas = new WorldCreator("active_arenas");
        activeArenas.type(WorldType.FLAT);
        activeArenas.generator(new SchematicArenaManager.VoidChunkGenerator());
        activeArenasWorld = activeArenas.createWorld();
        activeArenasWorld.setAutoSave(false);
    }

    private final JavaPlugin javaPlugin;
    private final File arenasFolder;
    private final GridSettings gridSettings;
    private final GridPositionManager gridPositionManager;
    private final List<ArenaMeta> availableArenas;
    private final Map<ArenaMeta, List<ActiveArena>> activeArenaBuffer = new HashMap<>();

    @SneakyThrows
    public GridArenaManager(JavaPlugin javaPlugin, File baseDir, MarkerScanner markerScanner) {
        super(markerScanner);
        this.javaPlugin = javaPlugin;
        arenasFolder = new File(baseDir, "arenas");
        arenasFolder.mkdirs();
        this.gridPositionManager = new GridPositionManager(1000);
        this.availableArenas = loadAvailableArenas();
        File file = new File(baseDir, "grid_settings.json");

        if (file.exists()) {
            gridSettings = gson.fromJson(new FileReader(file), GridSettings.class);
        } else {
            gridSettings = new GridSettings(0, 0);
            FileWriter fileWriter = new FileWriter(file);
            gson.toJson(gridSettings, fileWriter);
            fileWriter.close();
        }

        for (ArenaMeta availableArena : availableArenas) {
            List<ActiveArena> arenas = new ArrayList<>();
            for (int i = 0; i < availableArena.getBufferSize(); i++) {
                arenas.add(load(availableArena).join());
            }
            activeArenaBuffer.put(availableArena, arenas);
        }

        File templatesFolder = new File(baseDir, "./active_arenas");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                FileUtils.deleteDirectory(templatesFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

    }

    @Override
    protected void save(WorldCuboid worldCuboid, Arena arena, Consumer<String> messageCallback) {

        World world = worldCuboid.getWorld();

        Location minLoc = worldCuboid.getMin().toLocation(worldCuboid.getWorld());
        Chunk minChunk = minLoc.getChunk();

        Position max = worldCuboid.getMax();
        Position min = worldCuboid.getMin();

        int currentX = gridSettings.getCurrentX();

        int startChunkX = min.getBlockX() >> 4;
        int startChunkZ = min.getBlockZ() >> 4;

        int endChunkX = (max.getBlockX() >> 4) + 1;
        int endChunkZ = (max.getBlockZ() >> 4) + 1;


        int startChunkMinX = (int) (min.getX() - (minChunk.getX() * 16));
        int startChunkMinZ = (int) (min.getZ() - (minChunk.getZ() * 16));

        for (int x = startChunkX; x < endChunkX; x++) {
            for (int z = startChunkZ; z < endChunkZ; z++) {

                Chunk chunk = world.getChunkAt(x, z);
                if (!chunk.isLoaded()) chunk.load();

                int relChunkX = chunk.getX() - minChunk.getX();
                int relChunkZ = chunk.getZ() - minChunk.getZ();

                LevelChunkSection[] sections = ChunkCopy.copy(((LevelChunk) ((CraftChunk) chunk).getHandle(ChunkStatus.FULL)));

                ChunkCopy.paste(((CraftWorld) templateArenaWorld).getHandle(), sections, currentX + relChunkX, relChunkZ);
            }
        }

        templateArenaWorld.save();

        arena = arena.normalize();
        arena = arena.makeRelative(new Position(startChunkMinX, min.getBlockY(), startChunkMinZ));

        File arenaFolder = new File(arenasFolder, arena.getArenaMeta().getName());
        if (arenaFolder.mkdirs()) {
            logger.info("Created arena folder for " + arena.getArenaMeta().getName());
        }

        try {
            FileWriter writer = new FileWriter(new File(arenaFolder, "arena.json"));
            Game.getGson().toJson(arena, writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        availableArenas.add(arena.getArenaMeta());

    }

    @Override
    public CompletableFuture<ActiveArena> load(ArenaMeta arenaMeta) throws ArenaLoadException {

        List<ActiveArena> bufferedArenas = activeArenaBuffer.getOrDefault(arenaMeta, new ArrayList<>());

        if (!bufferedArenas.isEmpty()) {

            logger.info("A pre-spawned arena instance was found for " + arenaMeta.getName());

            ActiveArena buffered = bufferedArenas.get(0);
            bufferedArenas.remove(buffered);
            activeArenaBuffer.put(arenaMeta, bufferedArenas);

            CompletableFuture.runAsync(() -> {
                Bukkit.getScheduler().scheduleSyncDelayedTask(javaPlugin, () -> {
                    try {
                        spawnNewArena(arenaMeta);
                    } catch (ArenaLoadException e) {
                        e.printStackTrace();
                    }
                }, 1);

            });

            return CompletableFuture.completedFuture(buffered);
        }

        return CompletableFuture.completedFuture(spawnNewArena(arenaMeta));
    }

    @NotNull
    public ActiveArena spawnNewArena(ArenaMeta arenaMeta) throws ArenaLoadException {

        logger.info("Spawning a new arena instance for " + arenaMeta.getName());

        long start = System.currentTimeMillis();

        File arenaFolder = new File(arenasFolder, arenaMeta.getName());
        GridPositionManager.GridSpace next = gridPositionManager.next();
        System.out.println(next);

        try {
            Arena arena = Game.getGson().fromJson(new FileReader(new File(arenaFolder, "arena.json")), Arena.class);

            Cuboid bounds = arena.getBounds();
            Position max = bounds.getMax();
            Position min = bounds.getMin();
            Location minLoc = min.toLocation(templateArenaWorld);
            Chunk minChunk = minLoc.getChunk();
            int gridX = next.getX() >> 4;
            int gridZ = next.getZ() >> 4;
            int startChunkX = min.getBlockX() >> 4;
            int startChunkZ = min.getBlockZ() >> 4;
            int endChunkX = (max.getBlockX() >> 4) + 1;
            int endChunkZ = (max.getBlockZ() >> 4) + 1;
            int targetMinPosX = (gridX << 4) + ((startChunkX * 16) + min.getBlockX());
            int targetMinPosZ = (gridZ << 4) + ((startChunkZ * 16) + min.getBlockZ());

            logger.info("Creating relighter...");
            Relighter relighter = createRelighter();

            logger.info("Copying chunks...");

            //NOTE: Also adds chunks to relighter
            ArrayDeque<Chunk> chunks = enqueueRelightAndChunkPaste(startChunkX, endChunkX, startChunkZ, endChunkZ, minChunk, relighter, gridX, gridZ);


            arena = arena.normalize();
            arena = arena.makeRelative(new Position(targetMinPosX, min.getBlockY(), targetMinPosZ));


            logger.info("Awaiting for chunks to be copied...");

            Arena finalArena = arena;
            logger.info("Chunk copy for " + finalArena.getArenaMeta().getName() + " completed in " + (System.currentTimeMillis() - start) + "ms");

            for (Chunk chunk : chunks) {
                chunk.load();
            }
            Cuboid bounds1 = finalArena.getBounds();

            FaweAPI.fixLighting(BukkitAdapter.adapt(activeArenasWorld), new CuboidRegion(BlockVector3.at(bounds1.getMax().getX(), bounds1.getMax().getY(), bounds1.getMax().getZ()), BlockVector3.at(bounds1.getMin().getX(), bounds1.getMin().getY(), bounds1.getMin().getZ())), null, RelightMode.ALL);
            logger.info("Relighting: " + bounds1);


            return new ActiveArena(activeArenasWorld, finalArena, arena1 -> gridPositionManager.free(next));

        } catch (Exception e) {
            throw new ArenaLoadException(e.getMessage(), e);
        }
    }

    @NotNull
    private static Relighter createRelighter() {
        Platform platform = WorldEdit.getInstance().getPlatformManager().getPlatforms().get(0);

        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(activeArenasWorld);
        Relighter relighter = platform.getRelighterFactory().createRelighter(RelightMode.OPTIMAL, weWorld, Fawe.instance().getQueueHandler().getQueue(weWorld));
        return relighter;
    }

    @NotNull
    private static ArrayDeque<Chunk> enqueueRelightAndChunkPaste(
            int startChunkX, int endChunkX, int startChunkZ, int endChunkZ,
            Chunk minChunk, Relighter relighter, int gridX, int gridZ
    ) {
        ArrayDeque<Chunk> toPaste = new ArrayDeque<>();
        for (int x = startChunkX; x < endChunkX; x++) {
            for (int z = startChunkZ; z < endChunkZ; z++) {
                //calculates where the minimum point will be relative to the minimum point of the chunk, so we can paste it in the correct position
                //as region is unlikely to be aligned by chunk

                int relChunkX = x - minChunk.getX();
                int relChunkZ = z - minChunk.getZ();

                relighter.addChunk(x, z, null, 65535);

                ChunkCopyTask copyTask = new ChunkCopyTask(templateArenaWorld.getChunkAt(x, z), activeArenasWorld, gridX + relChunkX, gridZ + relChunkZ);
                toPaste.add(copyTask.get());
            }
        }
        return toPaste;
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
                        Arena arena = Game.getGson().fromJson(new FileReader(arenaFile), Arena.class);
                        available.add(arena.getArenaMeta());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return available;
    }

    @Override
    public void unload(Arena arena) {
    }

    @Override
    public List<ArenaMeta> findAvailableArenas(Predicate<ArenaMeta> predicate) {
        List<ArenaMeta> list = new ArrayList<>();
        for (ArenaMeta arenaMeta : availableArenas) {
            if (predicate.test(arenaMeta)) {
                list.add(arenaMeta);
            }
        }
        return list;
    }
}
