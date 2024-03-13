package com.readutf.inari.core.logging.store;

import java.util.UUID;

public interface LogStoreFactory {

    LogStore createLogStore(UUID gameId);

}
