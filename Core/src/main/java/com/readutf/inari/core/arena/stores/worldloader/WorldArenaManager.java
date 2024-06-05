package com.readutf.inari.core.arena.stores.worldloader;

import com.google.gson.reflect.TypeToken;
import com.readutf.inari.core.arena.ActiveArena;
import com.readutf.inari.core.arena.Arena;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.arena.exceptions.ArenaLoadException;
import com.readutf.inari.core.arena.exceptions.ArenaStoreException;
import com.readutf.inari.core.arena.marker.MarkerScanner;
import com.readutf.inari.core.arena.meta.ArenaMeta;
import com.readutf.inari.core.arena.stores.schematic.SchematicArenaManager;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.logging.Logger;
import com.readutf.inari.core.logging.LoggerFactory;
import com.readutf.inari.core.utils.Position;
import com.readutf.inari.core.utils.ThreadUtils;
import com.readutf.inari.core.utils.WorldCuboid;
import com.readutf.inari.core.utils.WorldEditUtils;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class WorldArenaManager extends ArenaManager {

    private static final Logger logger = LoggerFactory.getLogger(WorldArenaManager.class);

    private static final int ARENA_COPIES = 50;
    private static final int DISTANCE_BETWEEN = 500;
    private final File storeFile;
    private final JavaPlugin javaPlugin;
    private final Map<ArenaMeta, ActiveInstance> activeInstances;

    public WorldArenaManager(JavaPlugin javaPlugin, File storeFile, MarkerScanner markerScanner) {
        super(markerScanner);
        this.javaPlugin = javaPlugin;
        this.storeFile = storeFile;
        this.activeInstances = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(new WorldEventListener(), javaPlugin);
        if (storeFile.mkdirs()) logger.info("Created arena store directory at " + storeFile.getAbsolutePath());
    }

    @SneakyThrows
    @Override
    protected void save(WorldCuboid worldCuboid, Arena arena, Consumer<String> messageCallback) {

        if (worldExists(arena.getName() + "_template")) {
            messageCallback.accept("&cArena already exists, overwriting...");
            FileUtils.deleteDirectory(new File(javaPlugin.getServer().getWorldContainer(), arena.getName() + "_template"));
        }

        World arenaTemplateWorld = generateEmptyWorld(arena.getName() + "_template");

        List<Integer> instanceIndexes = new ArrayList<>();

        try (EditSession targetSource = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(arenaTemplateWorld))) {


            for (int i = 0; i < ARENA_COPIES; i++) {

                CuboidRegion region = WorldEditUtils.toCuboidRegion(worldCuboid);
                BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

                ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                        BukkitAdapter.adapt(worldCuboid.getWorld()), region, clipboard, region.getMinimumPoint()
                );

                Operations.complete(forwardExtentCopy);

                BlockVector3 target = BlockVector3.at(i * DISTANCE_BETWEEN, 0, 0);
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(targetSource)
                        .ignoreAirBlocks(true)
                        .to(target)
                        .build();


                Operations.complete(operation);

                logger.info("Pasted arena " + arena.getName() + " at " + target);
                instanceIndexes.add(i * DISTANCE_BETWEEN);

            }
        }

        File arenaFolder = new File(storeFile, arena.getName());
        if (arenaFolder.mkdirs()) logger.info("Created arena folder");

        FileWriter writer = new FileWriter(new File(arenaFolder, "arena.json"));

        arena = arena.normalize();

        Game.getGson().toJson(arena, writer);
        writer.flush();

        writer = new FileWriter(new File(arenaFolder, "instances.json"));
        Game.getGson().toJson(instanceIndexes, writer);
        writer.flush();

        File worldFolder = arenaTemplateWorld.getWorldFolder();

        boolean unloaded = Bukkit.unloadWorld(arenaTemplateWorld, true);
        if (!unloaded) throw new ArenaStoreException("Failed to unload world");

        FileUtils.delete(new File(worldFolder, "uid.dat"));

        try {
            FileUtils.copyDirectory(worldFolder, new File(arenaFolder, arena.getName() + "_template"));
        } catch (Exception e) {
            throw new ArenaStoreException("Failed to copy world", e);
        }


    }

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @SneakyThrows
    @Override
    public CompletableFuture<ActiveArena> load(ArenaMeta arenaMeta) throws ArenaLoadException {

        CompletableFuture<ActiveArena> future = new CompletableFuture<>();

        File arenaFolder = new File(storeFile, arenaMeta.getName());
        FileReader arenaDataReader;
        try {
            arenaDataReader = new FileReader(new File(arenaFolder, "arena.json"));
        } catch (FileNotFoundException e) {
            return CompletableFuture.failedFuture(e);
        }
        Arena arena = Game.getGson().fromJson(arenaDataReader, Arena.class);

        ActiveInstance activeInstance = activeInstances.get(arenaMeta);
        if (activeInstance == null || activeInstance.isFinished()) {

            logger.info("Creating new instance for " + arenaMeta.getName());

            FileReader instancesReader;
            try {
                instancesReader = new FileReader(new File(arenaFolder, "instances.json"));
            } catch (FileNotFoundException e) {
                return CompletableFuture.failedFuture(e);
            }
            List<Integer> instances = Game.getGson().fromJson(instancesReader, new TypeToken<>() {
            });

            String instanceName = arenaMeta.getName() + ThreadLocalRandom.current().nextInt(11111, 99999);
            try {
                FileUtils.copyDirectory(new File(arenaFolder, arenaMeta.getName() + "_template"), new File(javaPlugin.getServer().getWorldContainer(), instanceName));
            } catch (IOException e) {
                return CompletableFuture.failedFuture(e);
            }

            ThreadUtils.ensureSync(javaPlugin, () -> {
                new WorldCreator(instanceName).createWorld();
            }).join();

            activeInstances.put(arenaMeta, activeInstance = new ActiveInstance(Bukkit.getWorld(instanceName), instances));

        }

        arena = arena.makeRelative(new Position(activeInstance.getCurrentIndex().incrementAndGet(), 0, 0));
        future.complete(new ActiveArena(activeInstance.getWorld(), arena, this::free));

        return future;
    }

    @Override
    public void unload(Arena arena) {

    }

    @SneakyThrows
    @Override
    public List<ArenaMeta> findAvailableArenas(Predicate<ArenaMeta> predicate) {

        if (storeFile == null || !storeFile.exists()) return List.of();

        ArrayList<ArenaMeta> arenaMetas = new ArrayList<>();
        for (File arenaFile : storeFile.listFiles()) {

            File arenaDataFile = new File(arenaFile, "arena.json");
            if (!arenaDataFile.exists()) continue;
            FileReader arenaDataReader = new FileReader(arenaDataFile);


            Arena arena = Game.getGson().fromJson(arenaDataReader, Arena.class);

            arenaMetas.add(arena.getArenaMeta());
        }


        return arenaMetas;
    }

    public World generateEmptyWorld(String name) {

        WorldCreator worldCreator = new WorldCreator(name);
        worldCreator.type(WorldType.FLAT);
        worldCreator.generator(new SchematicArenaManager.VoidChunkGenerator());
        World created = worldCreator.createWorld();
        created.setAutoSave(false);

        return created;
    }

    public boolean worldExists(String name) {
        return new File(javaPlugin.getServer().getWorldContainer(), name).exists();
    }

    public void free(ActiveArena activeArena) {

        ActiveInstance activeInstance = activeInstances.get(activeArena.getArenaMeta());
        activeInstance.removeLock(activeArena);
        if(activeInstance.canDelete()) {
            Bukkit.unloadWorld(activeInstance.getWorld(), true);
            activeInstances.remove(activeArena.getArenaMeta());
        }

    }

    @Getter
    public static class ActiveInstance {

        private final World world;
        private final List<Integer> indexes;
        private AtomicInteger currentIndex;
        private static List<ActiveArena> arenaLocks = new ArrayList<>();

        public ActiveInstance(World world, List<Integer> indexes) {
            this.world = world;
            this.indexes = indexes;
            this.currentIndex = new AtomicInteger(0);
        }

        public void addToLock(ActiveArena arena) {
            arenaLocks.add(arena);
        }

        public boolean canDelete() {
            return arenaLocks.isEmpty();
        }

        public void removeLock(ActiveArena arena) {
            arenaLocks.remove(arena);
        }

        public int getNextIndex() {
            return indexes.get(currentIndex.getAndIncrement());
        }

        public boolean isFinished() {
            return currentIndex.get() >= indexes.size();
        }

    }


}
