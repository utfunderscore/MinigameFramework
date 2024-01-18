package com.readutf.inari.core.game.stage;

public interface Round {
    

    void init();

    void roundStart();

    void roundEnd();

    boolean hasRoundEnded();

}
