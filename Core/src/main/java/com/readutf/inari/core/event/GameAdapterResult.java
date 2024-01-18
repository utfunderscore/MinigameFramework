package com.readutf.inari.core.event;

import com.readutf.inari.core.game.Game;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class GameAdapterResult {

    private @Nullable final Game game;
    private @Nullable final String failReason;

    public GameAdapterResult(@NotNull Game game) {
        this.game = game;
        this.failReason = null;
    }

    public GameAdapterResult(@NotNull String failReason) {
        this.failReason = failReason;
        this.game = null;
    }

    public boolean isSuccessful() {
        return game != null;
    }

    @Override
    public String toString() {
        return "GameAdapterResult{" +
                "game=" + game +
                ", failReason='" + failReason + '\'' +
                '}';
    }
}
