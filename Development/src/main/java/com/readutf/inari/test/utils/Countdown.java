package com.readutf.inari.test.utils;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.task.GameTask;
import net.minecraft.server.MinecraftServer;

import java.util.function.Consumer;

public class Countdown extends GameTask {

    private final Game game;
    private final int duration;
    private final Consumer<Integer> timeConsumer;

    /**
     * Create a new countdown that's automatically submitted to the game thread
     * @param game the game
     * @param duration the duration in seconds
     * @param timeConsumer the consumer that will be called every second
     */
    public Countdown(Game game, int duration, Consumer<Integer> timeConsumer) {
        this.game = game;
        this.duration = duration;
        this.timeConsumer = timeConsumer;
        game.getGameThread().submitRepeatingTask(this, 0, 20);
    }

    private int startTime = MinecraftServer.currentTick;

    @Override
    public void run() {
        int sinceStart = MinecraftServer.currentTick - startTime;

        if(sinceStart % 20 == 0) {
            timeConsumer.accept((duration - (sinceStart / 20)));
        }

        if(sinceStart > duration * 20) {
            cancel();
        }
    }

    public boolean isActive() {
        return !isCancelled();
    }

}
