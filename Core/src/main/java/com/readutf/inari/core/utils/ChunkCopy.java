package com.readutf.inari.core.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;

public class ChunkCopy {

    public static LevelChunkSection[] copy(LevelChunk chunk) {
        LevelChunkSection[] sections = chunk.getSections().clone();


         try {
             for (int i = 0; i < sections.length; i++) {

                 LevelChunkSection section = sections[i];
                 if (section == null) continue;

                 PalettedContainer<BlockState> states = section.getStates().copy();
                 PalettedContainer<Holder<Biome>> biomes = section.getBiomes().recreate();

                 sections[i] = new LevelChunkSection(states, biomes);
             }

         } catch (Throwable e) {
             e.printStackTrace();
         }

        return sections;
    }

    public static void paste(ServerLevel level, LevelChunkSection[] sections, int x, int z) {
        LevelChunk chunk = level.getChunk(x, z);

        for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
            LevelChunkSection copiedSection = sections[sectionIndex];
            if (copiedSection == null) {
                chunk.getSections()[sectionIndex] = new LevelChunkSection(BIOME_REGISTRY, level, new ChunkPos(x, z), sectionIndex);
            } else {
                chunk.getSections()[sectionIndex] = new LevelChunkSection(copiedSection.getStates().copy(), copiedSection.getBiomes().recreate());
            }
        }
    }

    public static final Registry<Biome> BIOME_REGISTRY = MinecraftServer.getServer().registryAccess().registryOrThrow(Registries.BIOME);

    public static void emptyChunk(World world, int x, int z) {
        ServerLevel nmsWorld = ((CraftWorld) world).getHandle();
        LevelChunk nmsChunk = nmsWorld.getChunk(x, z);

        for (int i = 0; i < nmsChunk.getSections().length; i++) {
            nmsChunk.getSections()[i] = new LevelChunkSection(BIOME_REGISTRY, nmsWorld, new ChunkPos(x, z), i);
        }
    }

}