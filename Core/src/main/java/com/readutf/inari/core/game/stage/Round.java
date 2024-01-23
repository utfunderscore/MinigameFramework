package com.readutf.inari.core.game.stage;

import com.readutf.inari.core.game.team.Team;
import org.jetbrains.annotations.Nullable;

public interface Round {

    void roundStart();

    void roundEnd(@Nullable Team winnerTeam);

    boolean hasRoundEnded();

}
