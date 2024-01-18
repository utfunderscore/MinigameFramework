package com.readutf.inari.core.logging;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class LoggerManager {

    public @Getter static LoggerManager instance = new LoggerManager();

    private final Map<String, Logger> loggerCache = new HashMap<>();

    public Logger getLogger(String name) {
        Logger logger = loggerCache.get(name);
        if(logger != null) return logger;
        return new Logger(name, true, this);
    }

    public Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getSimpleName());
    }

}
