package com.readutf.inari.test.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.event.GameEventManager;
import com.readutf.inari.core.game.GameManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class DevCommand extends BaseCommand {

    private final GameManager gameManager;
    private final ArenaManager arenaManager;
    private final GameEventManager eventManager;

//    RawDataLoader rawDataLoader;
//
//    {
//        try {
//            rawDataLoader = new RawDataLoader(null);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }


    @SneakyThrows
    @CommandAlias("dev")
    public void oneTeam(Player player) {

        for (World world : Bukkit.getWorlds()) {
            player.sendMessage(world.getName() + ": " + world.getLoadedChunks().length);
        }

    }


}
