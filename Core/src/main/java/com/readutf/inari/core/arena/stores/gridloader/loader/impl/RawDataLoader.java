package com.readutf.inari.core.arena.stores.gridloader.loader.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readutf.inari.core.arena.exceptions.ArenaStoreException;
import com.readutf.inari.core.arena.stores.gridloader.SchematicArenaManager;
import com.readutf.inari.core.arena.stores.gridloader.loader.ArenaBuildLoader;
import com.readutf.inari.core.utils.Position;
import com.readutf.inari.core.utils.WorldCuboid;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPosition;
import net.minecraft.server.level.LightEngineThreaded;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.chunk.Chunk;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class RawDataLoader implements ArenaBuildLoader {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private @Getter
    final World arenaGrid;
    private final File settingsFile;
    private int currentChunkX = 0;

    public RawDataLoader(File pluginFolder) {
        this.settingsFile = new File(pluginFolder, "chunkGridSettings.txt");

        currentChunkX = loadCurrentOffset();

        if (new File(Bukkit.getWorldContainer().getParent(), "arena_grid").exists()) {
            arenaGrid = new WorldCreator("arena_grid").createWorld();
        } else {
            arenaGrid = generateArenaGrid();
        }

    }

    @NotNull
    private World generateArenaGrid() {
        final World arenaGrid;
        WorldCreator worldCreator = new WorldCreator("arena_grid");
        worldCreator.type(WorldType.FLAT);
        worldCreator.generator(new SchematicArenaManager.VoidChunkGenerator());
        arenaGrid = worldCreator.createWorld();
        arenaGrid.setAutoSave(false);
        return arenaGrid;
    }

    @Override
    public void pasteSchematic(World world, File arenaFolder, Position origin) throws IOException {

    }

    @Override
    public void saveSchematic(WorldCuboid worldCuboid, File arenaFolder) throws ArenaStoreException {

        WorldServer fromWorld = ((CraftWorld) worldCuboid.getWorld()).getHandle();
        WorldServer toWorld = ((CraftWorld) arenaGrid).getHandle();


        HashSet<String> seenChunks = new HashSet<>();

        int width = worldCuboid.getMax().getBlockX() - worldCuboid.getMin().getBlockX();

        int relativeX = 0;
        int relativeY = 0;
        int relativeZ = 0;

        for (int x = worldCuboid.getMin().getBlockX(); x < worldCuboid.getMax().getBlockX(); x++) {
            for (int z = worldCuboid.getMin().getBlockZ(); z < worldCuboid.getMax().getBlockZ(); z++) {
                if (seenChunks.isEmpty()) {
                    relativeX = x - (x >> 4);
                    relativeY = worldCuboid.getMin().getBlockY();
                    relativeZ = z - (z >> 4);
                }

                String key = (x >> 4) + "" + (z >> 4);
                if (seenChunks.contains(key)) continue;
                seenChunks.add(key);


                Chunk chunk = (Chunk) fromWorld.a(x >> 4, z >> 4, ChunkStatus.n, true);
                Chunk targetChunk = (Chunk) toWorld.a((x >> 4) + currentChunkX, z >> 4, ChunkStatus.n, true);

                try {
                    copyNativeChunk(fromWorld, toWorld, chunk, targetChunk);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                    throw new ArenaStoreException("Failed to copy chunk: " + e.getMessage());
                }

            }
        }

        new ArrayList<>(Bukkit.getOnlinePlayers()).getFirst().teleport(new Location(
                arenaGrid,
                currentChunkX * 16,
                100,
                0
        ));

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(arenaFolder, "settings.json"), Map.of(
                    "currentOffset", currentChunkX,
                    "relativeX", relativeX,
                    "relativeY", relativeY,
                    "relativeZ", relativeZ
            ));
        } catch (IOException e) {
            throw new ArenaStoreException("Failed to save settings file: " + e.getMessage());
        }

        currentChunkX += width + 5;
        saveCurrentOffset();
    }

    public int loadCurrentOffset() {
        if (settingsFile.exists()) {
            try {
                Map<String, Integer> map = objectMapper.readValue(settingsFile, new TypeReference<>() {
                });
                return map.getOrDefault("currentOffset", 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }

    public void saveCurrentOffset() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(settingsFile, Map.of("currentOffset", currentChunkX));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyNativeChunk(WorldServer fromWorld, WorldServer toWorld, Chunk fromChunk, Chunk targetChunk) throws NoSuchFieldException, IllegalAccessException {

        LightEngineThreaded fromWorldLightEngine = fromWorld.l().a();
        LightEngineThreaded toWorldLightEngine = toWorld.l().a();


        Field m = IChunkAccess.class.getDeclaredField("m");
        m.setAccessible(true);
        ChunkSection[] chunkSections = (ChunkSection[]) m.get(fromChunk);
        ChunkSection[] copiedSections = new ChunkSection[chunkSections.length];

        for (int i = 0; i < chunkSections.length; i++) {
            ChunkSection chunkSection = chunkSections[i];
            if (chunkSection == null) continue;
            copiedSections[i] = new ChunkSection(chunkSection.h(), (DataPaletteBlock<Holder<BiomeBase>>) chunkSection.i());
        }

        m.set(targetChunk, copiedSections);


        for (int i = fromWorldLightEngine.d(); i < fromWorldLightEngine.e(); ++i) {
            NibbleArray aLight = fromWorldLightEngine.a(EnumSkyBlock.a).a(SectionPosition.a(fromChunk.f(), i));
            NibbleArray bLight = fromWorldLightEngine.a(EnumSkyBlock.b).a(SectionPosition.a(fromChunk.f(), i));


            toWorldLightEngine.a(EnumSkyBlock.a, SectionPosition.a(targetChunk.f(), i), aLight);
            toWorldLightEngine.a(EnumSkyBlock.b, SectionPosition.a(targetChunk.f(), i), bLight);
        }

    }


}
