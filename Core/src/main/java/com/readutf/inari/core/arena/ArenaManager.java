package com.readutf.inari.core.arena;

import com.readutf.inari.core.arena.exceptions.ArenaLoadException;
import com.readutf.inari.core.arena.exceptions.ArenaStoreException;
import com.readutf.inari.core.arena.marker.Marker;
import com.readutf.inari.core.arena.marker.MarkerScanner;
import com.readutf.inari.core.arena.meta.ArenaMeta;
import com.readutf.inari.core.utils.Position;
import com.readutf.inari.core.utils.WorldCuboid;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ArenaManager {

    private final MarkerScanner markerScanner;

    public ArenaManager(MarkerScanner markerScanner) {
        this.markerScanner = markerScanner;
    }

    /**
     * Creates an Arena object that represents the provided cuboid
     * Scans the region for markers to be added to the arena, and removes them
     * Physical loading, i.e. pasting the schematic, should be handled by the implementation
     * @param name The name of the arena
     * @param cuboid The cuboid to create the arena from
     * @return The metadata of the created arena
     * @throws ArenaStoreException If the arena could not be saved
     */
    @SuppressWarnings( "deprecation" )
    public ArenaMeta createArena(String name, WorldCuboid cuboid, Consumer<String> messageCallback) throws ArenaStoreException {
        List<Marker> markers = markerScanner.scan(cuboid);

        ArenaMeta arenaMeta = new ArenaMeta(name, "test", new MaterialData(Material.PAPER), 10, markers.size());
        Arena arena = new Arena(name, cuboid.toCuboid(), arenaMeta, markers);

        save(cuboid, arena, messageCallback);

        return arenaMeta;
    }

    /**
     * Save all blocks within the cuboid to a file
     * The implementing class must do the following:
     * 1. Normalise the arena to its minimum point ensuring that markers can be aligned correctly when loading
     * 2. Save the blocks within the cuboid, either to a file, or using your own method
     * 3. Save the data required to load the arena, such as the world, the cuboid, and any other data
     *
     * @param worldCuboid     The cuboid to save
     * @param arena           The arena object to save
     * @param messageCallback
     * @throws ArenaStoreException If the arena could not be saved
     */
    protected abstract void save(WorldCuboid worldCuboid, Arena arena, Consumer<String> messageCallback) throws ArenaStoreException;

    /**
     * Load the arena from the provided ArenaMeta
     * The implementing class must do the following:
     * 1. Load the blocks into a new world
     * 2. Ensure that the arena is correctly aligned using {@link Arena#makeRelative(Position)}
     * @param arenaMeta The metadata of the arena to load
     * @throws ArenaLoadException If the arena could not be loaded
     */
    public abstract CompletableFuture<ActiveArena> load(ArenaMeta arenaMeta) throws ArenaLoadException;

    /**
     * Unload an active arena to free up space and memory
     * @param arena The arena to unload
     */
    public abstract void unload(Arena arena);

    public void shutdown() {
    }

    public abstract List<ArenaMeta> findAvailableArenas(Predicate<ArenaMeta> predicate);


}
