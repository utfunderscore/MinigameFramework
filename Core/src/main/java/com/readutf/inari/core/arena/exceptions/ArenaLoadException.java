package com.readutf.inari.core.arena.exceptions;

import lombok.Getter;

public class ArenaLoadException extends Exception {

    private @Getter Exception exception;

    public ArenaLoadException(String message, Exception exception) {
        super(message);
        this.exception = exception;
    }

    public ArenaLoadException(String message) {
        this.exception = exception;
    }

    @Override
    public void printStackTrace() {
        if(exception != null) {
            exception.printStackTrace();
        } else {
            super.printStackTrace();
        }
    }
}
