package com.readutf.inari.test.utils;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameState;
import com.readutf.inari.core.game.task.GameTask;
import com.readutf.inari.core.logging.GameLoggerFactory;
import com.readutf.inari.core.logging.Logger;
import net.minecraft.server.MinecraftServer;

import java.time.Duration;
import java.util.function.Consumer;

public class Countdown extends GameTask {

    private final Game game;
    private final long duration;
    private final CancellableTask<Integer> timeConsumer;
    private final Logger logger;

    private final int startTime = MinecraftServer.currentTick;

    public static Countdown startCountdown(Game game, long durationSeconds, CancellableTask<Integer> timeConsumer) {
        return new Countdown(game, durationSeconds, timeConsumer);
    }

    public static Countdown startCountdown(Game game, Duration duration, CancellableTask<Integer> timeConsumer) {
        return new Countdown(game, duration.toSeconds(), timeConsumer);
    }

    public static Countdown startCountdown(Game game, Duration duration, Consumer<Integer> timeConsumer) {
        return new Countdown(game, duration.toSeconds(), new CancellableTask<>() {
            @Override
            public void run(Integer integer) {
                timeConsumer.accept(integer);
            }
        });
    }

    public static Countdown startCountdown(Game game, Duration duration, Runnable onFinish) {
        return new Countdown(game, duration.toSeconds(), new CancellableTask<>() {
            @Override
            public void run(Integer integer) {
                if (integer == 0) {
                    onFinish.run();
                }
            }
        });
    }



    /**
     * Create a new countdown that's automatically submitted to the game thread
     * @param game the game
     * @param duration the duration in seconds
     * @param timeConsumer the consumer that will be called every second
     */
    private Countdown(Game game, long duration, CancellableTask<Integer> timeConsumer) {
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
            timeConsumer.run((int) (duration - (sinceStart / 20)));
        }

        if (sinceStart > duration * 20) {
            cancel();
        }
    }

    public boolean isActive() {
        return !isCancelled();
    }

}
