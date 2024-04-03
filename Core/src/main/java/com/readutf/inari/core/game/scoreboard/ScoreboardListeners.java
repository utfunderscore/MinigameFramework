package com.readutf.inari.core.game.scoreboard;

import com.readutf.inari.core.event.GameEventHandler;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.events.GameEndEvent;
import com.readutf.inari.core.game.events.GameRejoinEvent;
import com.readutf.inari.core.game.events.GameStartEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class ScoreboardListeners {

    private final Game game;

    @GameEventHandler
    public void onGameJoin(GameRejoinEvent e) {
        if (game.getScoreboardProvider() != null) {
            game.getScoreboardManager().setPlayerBoard(e.getPlayer(), game.getScoreboardProvider());
        }
    }


    @GameEventHandler
    public void onGameEnd(GameEndEvent e) {
        for (Player onlinePlayer : e.getGame().getOnlinePlayers()) {
            game.getScoreboardManager().clearPlayerBoard(onlinePlayer);
        }
    }

    @GameEventHandler
    public void onGameStart(GameStartEvent e) {
        if (game.getScoreboardProvider() != null) {
            for (Player onlinePlayer : e.getGame().getOnlinePlayers()) {
                game.getScoreboardManager().setPlayerBoard(onlinePlayer, game.getScoreboardProvider());
            }
        }
    }

}
