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
import com.readutf.inari.test.games.sumo.SumoGameStarter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@CommandAlias("game")
public class GameCommand extends BaseCommand {

    private @Getter final Map<String, GameStarter> gameStarters;

    private final ArenaManager arenaManager;
    private final GameManager gameManager;
    private final GameEventManager eventManager;

    public GameCommand(ArenaManager arenaManager, GameManager gameManager, GameEventManager eventManager) {
        this.arenaManager = arenaManager;
        this.gameManager = gameManager;
        this.eventManager = eventManager;
        this.gameStarters = Map.of(
                "sumo", new SumoGameStarter(arenaManager, gameManager, eventManager)
        );
    }

    @Subcommand("start") @CommandCompletion("@games @players @players")
    public void start(Player player, String gameName, String player1Name, String player2Name) {
        Player player1 = Bukkit.getPlayer(player1Name);
        Player player2 = Bukkit.getPlayer(player2Name);
        if(player1 == null || player2 == null) {
            player.sendMessage("Player not found");
            return;
        }


        GameStarter gameStarter = gameStarters.get(gameName.toLowerCase());
        if(gameStarter == null) {
            player.sendMessage("Game not found");
            return;
        }

        gameStarter.startGame(List.of(
                new Team("Red", ChatColor.RED, Collections.singletonList(player1.getUniqueId())),
                new Team("Blue", ChatColor.BLUE, Collections.singletonList(player2.getUniqueId()))
        ));

    }

}
