package com.readutf.inari.test.games.shared;

import com.readutf.inari.core.event.GameEventHandler;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameEndReason;
import com.readutf.inari.core.game.events.GameRejoinEvent;
import com.readutf.inari.core.game.exception.GameException;
import com.readutf.inari.core.game.stage.Round;
import com.readutf.inari.core.game.team.Team;
import com.readutf.inari.core.logging.Logger;
import com.readutf.inari.core.utils.ColorUtils;
import com.readutf.inari.test.utils.CancellableTask;
import com.readutf.inari.test.utils.Countdown;
import com.readutf.inari.core.utils.ThreadUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.Nullable;

import static com.readutf.inari.test.utils.Countdown.startCountdown;

public class AwaitingPlayersStage implements Round {


    private final Game game;
    private final Logger logger;
    private final int targetPlayers, gameExpireTimeSeconds;

    public AwaitingPlayersStage(Game game, int targetPlayers, int gameExpireTimeSeconds) {
        this.game = game;
        this.logger = game.getLoggerFactory().getLogger(AwaitingPlayersStage.class);
        this.targetPlayers = targetPlayers;
        this.gameExpireTimeSeconds = gameExpireTimeSeconds;
        game.registerListeners(this);
    }

    @Override
    public void roundStart() {

        startCountdown(game, gameExpireTimeSeconds, new CancellableTask<>() {
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

                if (integer == 0) {
                    cancel();
                    ThreadUtils.ensureSync(game.getJavaPlugin(), () -> game.endGame(null, GameEndReason.CANCELLED));
                }

            }


        });

    }

    @GameEventHandler
    public void onRejoin(GameRejoinEvent e) {

        Player player = e.getPlayer();
        try {
            player.teleport(game.getPlayerSpawnFinder().findSpawn(player));
        } catch (GameException ex) {
            ex.printStackTrace();
        }

        int online = game.getOnlinePlayers().size();
        game.messageAll(ColorUtils.color("&a%s &7has joined the game. &e(%s/%s) ".formatted(player.getName(), online, targetPlayers)));
    }

    private boolean checkForValidPlayers(int online) {
        if (online >= targetPlayers) {
            ThreadUtils.ensureSync(game.getJavaPlugin(), () -> game.endRound(null));
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
