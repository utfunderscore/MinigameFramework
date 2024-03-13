package com.readutf.inari.core.logging;

public class LoggerFactory {

    public static Logger getLogger(Class<?> clazz) {
        return new GenericLogger(org.slf4j.LoggerFactory.getLogger("Inari/" + clazz.getSimpleName()));
    }



}
