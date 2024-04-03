package com.readutf.inari.core.commands.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandCompletion;
import com.readutf.inari.core.game.GameManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class GameCompletions {

    @RequiredArgsConstructor
    public static class GameIdCompletion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

        private final GameManager gameManager;

        @Override
        public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
            List<String> gameIds = gameManager.getGames().stream().map(game -> game.getGameId().toString()).toList();
            return getShortestUnique(gameIds);
        }

        public List<String> getShortestUnique(List<String> list) {

            int currentLength = 0;

            for (String string : list) {
                System.out.println(string);
                for (int i = 1; i < string.length(); i++) {
                    String substring = string.substring(0, i);
                    if (list.stream().noneMatch(s -> !s.equalsIgnoreCase(string) && s.startsWith(substring))) {
                        currentLength = i;
                        break;
                    }
                }

            }

            final int finalCurrentLength = currentLength;
            return list.stream().map(string -> string.substring(0, finalCurrentLength)).toList();
        }

    }

    @RequiredArgsConstructor
    public static class GamePlayersCompletion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

        private final GameManager gameManager;

        @Override
        public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
            return gameManager.getGames().stream().flatMap(game -> game.getOnlinePlayers().stream().map(Player::getName)).toList();
        }
    }

}
