package com.readutf.inari.core.logging;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.logging.impl.GameLogger;
import com.readutf.inari.core.logging.store.LogStore;
import com.readutf.inari.core.logging.store.LogStoreFactory;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameLoggerFactory {

    private @Getter static final List<GameLoggerFactory> factories = new ArrayList<>();

    private final Game game;
    private final LogStoreFactory logStoreFactory;
    private Map<String, Logger> loggers;
    private final LogStore logStore;

    public GameLoggerFactory(Game game, LogStoreFactory logStoreFactory) {
        factories.add(this);
        this.game = game;
        this.logStoreFactory = logStoreFactory;
        this.logStore = logStoreFactory.createLogStore(game.getGameId());
        this.loggers = new HashMap<>();
    }

    public Logger getLogger(String name) {
        return loggers.getOrDefault(name, new GameLogger(game, name, logStore));
    }

    public Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getSimpleName());
    }

    public void shutdown() {
        logStore.shutdown();
        factories.remove(this);
    }

}
