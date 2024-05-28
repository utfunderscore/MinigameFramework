package com.readutf.inari.core.arena.stores.gridworld;

import com.readutf.inari.core.utils.ChunkCopy;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class ChunkCopyTask implements Supplier<Chunk> {

    private final Chunk chunkToCopy;
    private final World targetWorld;
    private final int targetX, targetZ;

    @Override
    public Chunk get() {
        if (!chunkToCopy.isLoaded()) chunkToCopy.load();
        LevelChunkSection[] sections = ChunkCopy.copy(((LevelChunk) ((CraftChunk) chunkToCopy).getHandle(ChunkStatus.FULL)));
        if (sections.length != 0) ChunkCopy.paste(((CraftWorld) targetWorld).getHandle(), sections, targetX, targetZ);
        return chunkToCopy;
    }
}
