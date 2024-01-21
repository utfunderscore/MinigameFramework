package com.readutf.inari.core.game;

import com.readutf.inari.core.game.spectator.SpectatorData;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface GameLang {

    Collection<Component> getGameSummaryMessage(Player player);

    Collection<Component> getSpectateMessage(Player player, SpectatorData data);

    Collection<Component> getRespawnIntervalMessage(Player player, int interval);

}
