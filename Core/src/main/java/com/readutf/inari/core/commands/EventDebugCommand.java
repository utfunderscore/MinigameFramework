package com.readutf.inari.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import com.readutf.inari.core.event.GameEventManager;
import com.readutf.inari.core.utils.ColorUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class EventDebugCommand extends BaseCommand {

    private final GameEventManager eventManager;

    @CommandAlias("eventdebug")
    public void onEventDebug(Player player, String event) {

        Class<? extends Event> eventClass = findEventClass(event);
        if (eventClass == null) {
            player.sendMessage(ColorUtils.color("&cEvent not found, make sure to copy the exact class reference"));
            return;
        }

        if (eventManager.getActiveDebugs().remove(eventClass)) {
            player.sendMessage(ColorUtils.color("&aDisabled debug for " + eventClass.getSimpleName()));
            return;
        } else {
            eventManager.getActiveDebugs().add(eventClass);
            player.sendMessage(ColorUtils.color("&aEnabled debug for " + eventClass.getSimpleName()));
            return;
        }

    }

    public @Nullable Class<? extends Event> findEventClass(String reference) {

        try {
            Class<?> aClass = getClass().getClassLoader().loadClass(reference);

            return aClass.asSubclass(Event.class);
        } catch (Exception e) {
            return null;
        }

    }

}
