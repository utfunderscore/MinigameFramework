package com.readutf.inari.test.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import com.readutf.inari.core.arena.ActiveArena;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.arena.exceptions.ArenaLoadException;
import com.readutf.inari.core.arena.meta.ArenaMeta;
import com.readutf.inari.core.event.GameEventManager;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameManager;
import com.readutf.inari.core.game.exception.MatchException;
import com.readutf.inari.core.game.spawning.impl.TeamBasedSpawning;
import com.readutf.inari.core.game.team.Team;
import com.readutf.inari.test.InariDemo;
import com.readutf.inari.test.payload.PayloadStage;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class DevCommand extends BaseCommand {

    private final GameManager gameManager;
    private final ArenaManager arenaManager;
    private final GameEventManager eventManager;

    @CommandAlias("dev")
    public void dev(Player player) {

        Optional<ArenaMeta> first = arenaManager.findAvailableArenas(arenaMeta -> true).stream().findFirst();
        first.ifPresentOrElse(arenaMeta -> {
            try {
                ActiveArena load = arenaManager.load(arenaMeta);

                Team team = new Team("test", ChatColor.BLUE, List.of(player.getUniqueId()));

                Game game = Game.builder(InariDemo.getInstance(), load, eventManager, List.of(team), (game1, previousRound) -> new PayloadStage())
                        .setPlayerSpawnHandler(TeamBasedSpawning.fromArena(load, "spawn"))
                        .setSpectatorSpawnHandler(TeamBasedSpawning.fromArena(load, "spawn"))
                        .build();

                gameManager.startGame(game);


            } catch (ArenaLoadException | MatchException e) {
               e.printStackTrace();
            }

        }, () -> {
            player.sendMessage("No arenas found");
        });


    }
}
