package com.readutf.inari.core.event.testlistener;

import com.readutf.inari.core.event.GameEventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TestListener {

    @GameEventHandler
    public void onTest(AsyncPlayerChatEvent event) {

        event.getPlayer().sendMessage("hello");

    }

}
