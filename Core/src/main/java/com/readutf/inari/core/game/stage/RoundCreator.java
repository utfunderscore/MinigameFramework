package com.readutf.inari.core.game.stage;

import com.readutf.inari.core.game.Game;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RoundCreator {

    Round createRound(@NotNull Game game, @Nullable Round previousRound);

}
