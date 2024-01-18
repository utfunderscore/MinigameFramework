package com.readutf.inari.core.game.exception;

public class MatchException extends Exception {

    private Exception exception;

    public MatchException(String message) {
        super(message);
    }

    public MatchException(String message, Exception exception) {
        super(message);
        this.exception = exception;
    }



}
