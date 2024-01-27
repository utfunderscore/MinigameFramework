package com.readutf.inari.core.game.spectator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SpectatorData {

    private final long startTime = System.currentTimeMillis();
    private @Setter boolean respawn;
    private @Setter boolean canFly;
    private @Setter List<Integer> messageIntervals;
    private long durationMillis;
    private long respawnAt;

    public SpectatorData(boolean respawn, long durationMillis, boolean canFly, List<Integer> messageIntervals) {
        this.respawn = respawn;
        this.durationMillis = durationMillis;
        this.respawnAt = System.currentTimeMillis() + durationMillis;
        this.canFly = canFly;
        this.messageIntervals = new ArrayList<>(messageIntervals);
    }

    public void setDuration(int durationMillis) {
        this.durationMillis = durationMillis;
        this.respawnAt = startTime + durationMillis;
    }

}
