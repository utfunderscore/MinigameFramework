package com.readutf.inari.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import com.readutf.inari.core.event.GameEventManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

@RequiredArgsConstructor
public class EventDebugCommand extends BaseCommand {

    private final GameEventManager eventManager;

    @CommandAlias("eventdebug")
    public void onEventDebug(Player player, String event) {
        try {
            Class<?> aClass = getClass().getClassLoader().loadClass(event);

            eventManager.getActiveDebugs().add((Class<? extends Event>) aClass);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
