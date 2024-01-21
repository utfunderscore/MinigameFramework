package com.readutf.inari.test.payload;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameEndReason;
import com.readutf.inari.core.game.stage.Round;
import com.readutf.inari.test.InariDemo;
import net.kyori.adventure.text.Component;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;

public class PayloadRound implements Round {

    private final Game game;

    public PayloadRound(Game game) {
        this.game = game;
    }

    @Override
    public void roundStart() {

        final int[] timeLeft = {15};

        new BukkitRunnable() {
            @Override
            public void run() {


                timeLeft[0]--;

                if(timeLeft[0] == 0) {

                    game.endGame(game.getPlayerTeams().getFirst(), GameEndReason.TIME);

                    cancel();
                } else {

                    game.messageAlive("Time left: " + timeLeft[0] + " seconds");

                }

            }
        }.runTaskTimer(InariDemo.getInstance(), 0, 20);

    }

    @Override
    public void roundEnd() {

    }

    @Override
    public boolean hasRoundEnded() {
        return false;
    }
}
