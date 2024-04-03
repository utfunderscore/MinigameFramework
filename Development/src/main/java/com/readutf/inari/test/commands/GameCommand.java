package com.readutf.inari.test.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.event.GameEventManager;
import com.readutf.inari.core.game.GameManager;
import com.readutf.inari.core.game.team.Team;
import com.readutf.inari.test.games.GameStarter;
import com.readutf.inari.test.games.GameStarterManager;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@CommandAlias("game")
public class GameCommand extends BaseCommand {


    private final GameStarterManager gameStarterManager;

    public GameCommand(GameStarterManager gameStarterManager) {
        this.gameStarterManager = gameStarterManager;
    }

    @SneakyThrows
    @Subcommand("start") @CommandCompletion("@games @players @players")
    public void start(Player player, String gameName, String player1Name, String player2Name) {
        OfflinePlayer player1 = Bukkit.getOfflinePlayer(player1Name);
        OfflinePlayer player2 = Bukkit.getOfflinePlayer(player2Name);


        GameStarter gameStarter = gameStarterManager.getStarter(gameName.toLowerCase());
        if(gameStarter == null) {
            player.sendMessage("Game not found");
            return;
        }

        gameStarter.startGame(List.of(
                Collections.singletonList(player1.getUniqueId()),
                Collections.singletonList(player2.getUniqueId())
        ));

    }

}
