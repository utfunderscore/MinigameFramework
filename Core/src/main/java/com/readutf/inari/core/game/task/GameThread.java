package com.readutf.inari.core.game.task;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameState;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.MinecraftServer;

import java.util.*;

public class GameThread extends TimerTask {

    private final Game game;
    private final int startTick;

    private int lastTick = 0;

    private final Map<GameTask, TaskInfo> gameTasks;

    public GameThread(Game game) {
        this.game = game;
        this.gameTasks = new HashMap<>();
        this.startTick = MinecraftServer.currentTick;
    }

    @Override
    public void run() {
        if(MinecraftServer.currentTick == lastTick) return;
        lastTick = MinecraftServer.currentTick;

        if(game == null || game.getGameState() == GameState.ENDED) {
            cancel();
            return;
        }

        int sinceFirstTick = MinecraftServer.currentTick - startTick;


        for (Map.Entry<GameTask, TaskInfo> entry : gameTasks.entrySet()) {
            GameTask gameTask = entry.getKey();
            TaskInfo taskInfo = entry.getValue();
            if(gameTask.isCancelled()) {
                gameTasks.remove(gameTask);
                continue;
            }

            if (taskInfo.isShouldRun() || MinecraftServer.currentTick - taskInfo.getStartTick() > taskInfo.getDelay()) {
                if (taskInfo.isRepeating()) {
                    if (MinecraftServer.currentTick - taskInfo.getStartTick() > sinceFirstTick % taskInfo.getInterval()) {
                        gameTask.run();
                    }
                } else {
                    gameTask.run();
                    gameTasks.remove(gameTask);
                }
            }

        }

    }

    public void submitRepeatingTask(GameTask GameTask, int delay, int interval) {
        gameTasks.put(GameTask, TaskInfo.repeating(delay, interval));
    }

    public void submitTask(GameTask GameTask) {
        gameTasks.put(GameTask, TaskInfo.single(0));
    }

    public void submitTask(GameTask GameTask, int interval) {
        gameTasks.put(GameTask, TaskInfo.single(interval));
    }

    @Getter
    public static class TaskInfo {

        private final boolean isRepeating;
        private final int delay;
        private final int interval;
        private final int startTick = MinecraftServer.currentTick;
        private @Setter boolean shouldRun = false;

        private TaskInfo(boolean isRepeating, int delay, int interval) {
            this.isRepeating = isRepeating;
            this.delay = delay;
            this.interval = interval;
        }

        public static TaskInfo repeating(int delay, int interval) {
            return new TaskInfo(true, delay, interval);
        }

        public static TaskInfo single(int delay) {
            return new TaskInfo(false, delay, 0);
        }



    }

}
