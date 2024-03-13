package com.readutf.inari.core.logging.impl;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.logging.Logger;
import com.readutf.inari.core.logging.store.LogStore;
import org.apache.logging.log4j.Level;
import org.slf4j.LoggerFactory;

public class GameLogger implements Logger {

    private final org.slf4j.Logger logger;
    private final LogStore logStore;

    public GameLogger(Game game, String prefix, LogStore logStore) {
        String shortId = game.getGameId().toString().substring(0, 8);
        this.logStore = logStore;
        this.logger = LoggerFactory.getLogger(shortId + "/" + prefix);
    }

    @Override
    public void info(String message) {
        logger.info(message);
        logStore.saveLog(Level.INFO, System.currentTimeMillis(), message, null);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
        logStore.saveLog(Level.WARN, System.currentTimeMillis(), message, null);
    }

    @Override
    public void error(String message) {
        logger.error(message);
        logStore.saveLog(Level.ERROR, System.currentTimeMillis(), message, null);
    }

    @Override
    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
        logStore.saveLog(Level.ERROR, System.currentTimeMillis(), message, throwable);
    }

    @Override
    public void debug(String message) {
        logger.debug(message);
        logStore.saveLog(Level.DEBUG, System.currentTimeMillis(), message, null);
    }
}
