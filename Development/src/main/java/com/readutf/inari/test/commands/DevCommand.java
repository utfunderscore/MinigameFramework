package com.readutf.inari.test.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.event.GameEventManager;
import com.readutf.inari.core.game.GameManager;
import com.readutf.inari.core.utils.ChunkCopy;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class DevCommand extends BaseCommand {

    private final GameManager gameManager;
    private final ArenaManager arenaManager;
    private final GameEventManager eventManager;


    @SneakyThrows
    @CommandAlias( "dev" )
    public void oneTeam(Player player) {

        for (World world : Bukkit.getWorlds()) {
            player.sendMessage(world.getName() + ": " + world.getLoadedChunks().length);
        }

    }


}
