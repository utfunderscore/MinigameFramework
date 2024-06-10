package com.readutf.inari.test.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.arena.meta.ArenaMeta;
import com.readutf.inari.test.games.GameStarter;
import com.readutf.inari.test.games.GameStarterManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@CommandAlias("game") @RequiredArgsConstructor
public class GameCommand extends BaseCommand {

    private final ArenaManager arenaManager;
    private final GameStarterManager gameStarterManager;
    
    @SneakyThrows
    @Subcommand("start") @CommandCompletion("@gametypes @arena @players")
    public void start(Player player, String gameName, String arenaName, String... players) {

        if(players.length % 2 != 0) {
            player.sendMessage("Teams must be same size");
            return;
        }

        if(players.length < 2) {
            player.sendMessage("Not enough players");
            return;
        }

        List<ArenaMeta> foundArena = arenaManager.findAvailableArenas(arenaMeta1 -> arenaMeta1.getName().equalsIgnoreCase(arenaName));
        if(foundArena.isEmpty()) {
            player.sendMessage("Arena not found");
            return;
        }

        ArenaMeta arenaMeta = foundArena.get(0);

        String[] team1 = (String[]) ArrayUtils.subarray(players, 0, players.length / 2);
        String[] team2 = (String[]) ArrayUtils.subarray(players, players.length / 2, players.length);

        List<UUID> team1Players = Arrays.stream(team1).map(s -> Bukkit.getOfflinePlayer(s).getUniqueId()).toList();
        List<UUID> team2Players = Arrays.stream(team2).map(s -> Bukkit.getOfflinePlayer(s).getUniqueId()).toList();

        GameStarter gameStarter = gameStarterManager.getStarter(gameName.toLowerCase());
        if(gameStarter == null) {
            player.sendMessage("Game not found");
            return;
        }

        gameStarter.startGame(arenaMeta, List.of(
                team1Players,
                team2Players
        ));

    }

}
