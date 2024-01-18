package com.readutf.inari.core.game.spectator;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SpectatorData {

    private final boolean respawn;
    private final long durationMillis;
    private final long respawnAt;
    private final boolean canFly;
    private final List<Integer> messageIntervals;

    public SpectatorData(boolean respawn, long durationMillis, boolean canFly, List<Integer> messageIntervals) {
        this.respawn = respawn;
        this.durationMillis = durationMillis;
        this.respawnAt = System.currentTimeMillis() + durationMillis;
        this.canFly = canFly;
        this.messageIntervals = new ArrayList<>(messageIntervals);
    }
}
