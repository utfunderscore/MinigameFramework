package com.readutf.inari.core.game.task;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public abstract class GameTask implements Runnable {

    private boolean cancelled = false;

    public abstract void run();

    public void cancel() {
        cancelled = true;
    }

}
