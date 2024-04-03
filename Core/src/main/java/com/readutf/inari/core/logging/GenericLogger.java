package com.readutf.inari.core.logging;

import com.readutf.inari.core.logging.impl.GameLogger;
import org.apache.logging.log4j.Level;

public class GenericLogger implements Logger {

    private final org.slf4j.Logger logger;

    public GenericLogger(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        logger.info(message);
        for (GameLoggerFactory factory : GameLoggerFactory.getFactories()) {
            ((GameLogger) factory.getLogger("Global")).getLogStore().saveLog(Level.INFO, System.currentTimeMillis(), message, null);
        }
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
        for (GameLoggerFactory factory : GameLoggerFactory.getFactories()) {
            ((GameLogger) factory.getLogger("Global")).getLogStore().saveLog(Level.WARN, System.currentTimeMillis(), message, null);
        }
    }

    @Override
    public void error(String message) {
        logger.error(message);
        for (GameLoggerFactory factory : GameLoggerFactory.getFactories()) {
            ((GameLogger) factory.getLogger("Global")).getLogStore().saveLog(Level.ERROR, System.currentTimeMillis(), message, null);
        }
    }

    @Override
    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
        for (GameLoggerFactory factory : GameLoggerFactory.getFactories()) {
            ((GameLogger) factory.getLogger("Global")).getLogStore().saveLog(Level.ERROR, System.currentTimeMillis(), message, throwable);
        }
    }

    @Override
    public void debug(String message) {
        logger.debug(message);
        for (GameLoggerFactory factory : GameLoggerFactory.getFactories()) {
            ((GameLogger) factory.getLogger("Global")).getLogStore().saveLog(Level.DEBUG, System.currentTimeMillis(), message, null);
        }
    }
}
