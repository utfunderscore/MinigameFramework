package com.readutf.inari.test.utils;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameState;
import com.readutf.inari.core.game.task.GameTask;
import com.readutf.inari.core.logging.GameLoggerFactory;
import com.readutf.inari.core.logging.Logger;
import net.minecraft.server.MinecraftServer;

public class Countdown extends GameTask {

    private final Game game;
    private final int duration;
    private final CancellableTask<Integer> timeConsumer;
    private final Logger logger;

    private int startTime = MinecraftServer.currentTick;


    /**
     * Create a new countdown that's automatically submitted to the game thread
     * @param game the game
     * @param duration the duration in seconds
     * @param timeConsumer the consumer that will be called every second
     */
    public Countdown(Game game, int duration, CancellableTask<Integer> timeConsumer) {
        this.game = game;
        this.logger = game.getLoggerFactory().getLogger(Countdown.class);
        this.duration = duration + 1;
        this.timeConsumer = timeConsumer;
        timeConsumer.setCancelTaskRunnable(this::cancel);
        game.getGameThread().submitRepeatingTask(this, 0, 20);
    }

    @Override
    public void run() {
        if (game.getGameState() == GameState.ENDED) {
            logger.debug("Game ended, cancelling countdown");
            cancel();
            return;

        }

        int sinceStart = MinecraftServer.currentTick - startTime;

        if ((sinceStart - 1) % 20 == 0) {
            timeConsumer.run((duration - (sinceStart / 20)));
        }

        if (sinceStart > duration * 20) {
            cancel();
        }
    }

    public boolean isActive() {
        return !isCancelled();
    }

}
