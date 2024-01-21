package com.readutf.inari.core.game.spectator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class SpectatorData {

    private boolean respawn;
    private long durationMillis;
    private long respawnAt;
    private boolean canFly;
    private List<Integer> messageIntervals;

    public SpectatorData(boolean respawn, long durationMillis, boolean canFly, List<Integer> messageIntervals) {
        this.respawn = respawn;
        this.durationMillis = durationMillis;
        this.respawnAt = System.currentTimeMillis() + durationMillis;
        this.canFly = canFly;
        this.messageIntervals = new ArrayList<>(messageIntervals);
    }
}
