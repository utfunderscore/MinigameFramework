package com.readutf.inari.core.logging.store;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

public interface LogStore {


    void saveLog(Level level, long timeStamp, String message, @Nullable Throwable throwable);

    void shutdown();

}
