package com.readutf.inari.core.logging;

import org.apache.logging.log4j.LogManager;

public class Logger {

    private final String name;
    private final boolean enabled;
    private final LoggerManager loggerManager;

    private final org.apache.logging.log4j.Logger logger;

    public Logger(String name, boolean enabled, LoggerManager loggerManager) {
        this.name = name;
        this.enabled = enabled;
        this.loggerManager = loggerManager;
        this.logger = LogManager.getLogger("Inari/" + name);
    }

    public void debug(String message) {
        logger.info("[debug] " + message);
    }

    public void fine(String message) {
        logger.info("[fine] " + message);
    }

    public void warn(String message) {
        logger.info("[warn] " + message);
    }

    public void error(Exception e) {
        e.printStackTrace();
    }

}
