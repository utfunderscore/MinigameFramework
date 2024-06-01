package com.readutf.inari.core.arena.stores.gridworld;

import com.readutf.inari.core.logging.Logger;
import com.readutf.inari.core.logging.LoggerFactory;
import com.readutf.inari.core.utils.ChunkCopy;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.chunk.ChunkAccess;
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

    private static final Logger logger = LoggerFactory.getLogger(ChunkCopyTask.class);
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ChunkCopyTask.class);

    private final Chunk chunkToCopy;
    private final World targetWorld;
    private final int targetX, targetZ;

    @Override
    public Chunk get() {


        try {
            if (!chunkToCopy.isLoaded()) chunkToCopy.load();
            ChunkAccess handle = ((CraftChunk) chunkToCopy).getHandle(ChunkStatus.FULL);
            LevelChunkSection[] sections = ChunkCopy.copy(((LevelChunk) handle));
            if (sections.length != 0) {
                ChunkCopy.paste(((CraftWorld) targetWorld).getHandle(), sections, targetX, targetZ);
            }

        } catch (Throwable e) {
            logger.error("Failed to copy chunk at " + chunkToCopy.getX() + ", " + chunkToCopy.getZ() + " to " + targetX + ", " + targetZ + " in world " + targetWorld.getName());
            e.printStackTrace();
        }

        return chunkToCopy;
    }
}
