package com.readutf.inari.test.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftChunk;
import org.bukkit.entity.Player;

public class CloneChunkCommand extends BaseCommand {

    @CommandAlias( "copychunk" )
    public void copyChunk(Player player) {

        Location location = player.getLocation();

        Chunk chunk = location.getChunk();
        Chunk targetChunk = location.getWorld().getChunkAt(chunk.getX(), chunk.getZ() + 1);

        ChunkAccess handle = ((CraftChunk) chunk).getHandle(ChunkStatus.FULL);

        for (LevelChunkSection section : handle.getSections()) {

        }


    }

}
