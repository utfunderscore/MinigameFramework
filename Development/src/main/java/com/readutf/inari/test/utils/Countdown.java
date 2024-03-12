package com.readutf.inari.test.utils;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameState;
import com.readutf.inari.core.game.stage.Round;
import com.readutf.inari.core.game.task.GameTask;
import net.minecraft.server.MinecraftServer;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class Countdown extends GameTask {

    private final Game game;
    private final int duration;
    private final CancellableTask<Integer> timeConsumer;

    private int startTime = MinecraftServer.currentTick;
    private Round round;


    /**
     * Create a new countdown that's automatically submitted to the game thread
     * @param game the game
     * @param duration the duration in seconds
     * @param timeConsumer the consumer that will be called every second
     */
    public Countdown(Game game, int duration, CancellableTask<Integer> timeConsumer) {
        this.game = game;
        this.round = null;
        this.duration = duration + 1;
        this.timeConsumer = timeConsumer;
        timeConsumer.setCancelTaskRunnable(this::cancel);
        game.getGameThread().submitRepeatingTask(this, 0, 20);
    }

    @Override
    public void run() {
        if (game.getGameState() == GameState.ENDED) {

            System.out.println("Game ended");
            cancel();
            return;

        } else if (round != null && game.getCurrentRound() != round) {
            System.out.println("Round ended");
            System.out.println(game.getCurrentRound());
            System.out.println(round);
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
