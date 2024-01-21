package com.readutf.inari.core.game.exception;

public class GameException extends Exception {

    private Exception exception;

    public GameException(String message) {
        super(message);
    }

    public GameException(String message, Exception exception) {
        super(message);
        this.exception = exception;
    }



}
