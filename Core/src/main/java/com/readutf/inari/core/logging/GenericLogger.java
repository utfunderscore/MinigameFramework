package com.readutf.inari.core.logging;

public class GenericLogger implements Logger {

    private final org.slf4j.Logger logger;

    public GenericLogger(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        logger.info(message);
        for (GameLoggerFactory factory : GameLoggerFactory.getFactories()) {
            factory.getLogger("Global").info(message);
        }
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
        for (GameLoggerFactory factory : GameLoggerFactory.getFactories()) {
            factory.getLogger("Global").warn(message);
        }
    }

    @Override
    public void error(String message) {
        logger.error(message);
        for (GameLoggerFactory factory : GameLoggerFactory.getFactories()) {
            factory.getLogger("Global").error(message);
        }
    }

    @Override
    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
        for (GameLoggerFactory factory : GameLoggerFactory.getFactories()) {
            factory.getLogger("Global").error(message, throwable);
        }
    }

    @Override
    public void debug(String message) {
        logger.debug(message);
        for (GameLoggerFactory factory : GameLoggerFactory.getFactories()) {
            factory.getLogger("Global").debug(message);
        }
    }
}
