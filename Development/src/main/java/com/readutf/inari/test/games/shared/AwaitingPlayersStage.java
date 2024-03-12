package com.readutf.inari.test.games.shared;

import com.readutf.inari.core.event.GameEventHandler;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameEndReason;
import com.readutf.inari.core.game.events.GameRejoinEvent;
import com.readutf.inari.core.game.exception.GameException;
import com.readutf.inari.core.game.stage.Round;
import com.readutf.inari.core.game.team.Team;
import com.readutf.inari.core.logging.Logger;
import com.readutf.inari.core.logging.LoggerManager;
import com.readutf.inari.core.utils.ColorUtils;
import com.readutf.inari.test.utils.CancellableTask;
import com.readutf.inari.test.utils.Countdown;
import com.readutf.inari.test.utils.ThreadUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.Nullable;

public class AwaitingPlayersStage implements Round {

    private static final Logger logger = LoggerManager.getInstance().getLogger(AwaitingPlayersStage.class);

    private final Game game;
    private final int targetPlayers, gameExpireTimeSeconds;

    public AwaitingPlayersStage(Game game, int targetPlayers, int gameExpireTimeSeconds) {
        this.game = game;
        this.targetPlayers = targetPlayers;
        this.gameExpireTimeSeconds = gameExpireTimeSeconds;
        game.registerListeners(this);
    }

    @Override
    public void roundStart() {

        new Countdown(game, gameExpireTimeSeconds, new CancellableTask<>() {
            @Override
            public void run(Integer integer) {
                if (checkForValidPlayers(game.getOnlinePlayers().size())) {
                    cancel();
                    return;
                }

                if (integer % 10 == 0) {
                    game.messageAll(ColorUtils.color("Waiting for " + (targetPlayers - game.getOnlinePlayers().size()) + " more players to join the game."));
                }


                if (!hasRoundEnded()) {
                    game.endRound(null);
                }
            }


        });

    }

    @GameEventHandler
    public void onRejoin(GameRejoinEvent e) {

        Player player = e.getPlayer();
        try {
            player.teleport(game.getPlayerSpawnFinder().findSpawn(game, player));
        } catch (GameException ex) {
            ex.printStackTrace();
        }

        int online = game.getOnlinePlayers().size();
        game.messageAll(ColorUtils.color("&a%s &7has joined the game. &e(%s/%s) ".formatted(player.getName(), online, targetPlayers)));

        checkForValidPlayers(online);
    }

    private boolean checkForValidPlayers(int online) {
        if (online >= targetPlayers) {
            ThreadUtils.ensureSync(() -> game.endRound(null));
            return true;
        }
        return false;
    }

    @GameEventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        e.setCancelled(true);
        e.setDamage(0);
    }

    @Override
    public void roundEnd(@Nullable Team winnerTeam) {

        game.unregisterListeners(this);

    }

    @Override
    public boolean hasRoundEnded() {
        return game.getAllPlayers().size() >= targetPlayers;
    }
}
