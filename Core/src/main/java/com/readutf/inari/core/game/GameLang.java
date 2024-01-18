package com.readutf.inari.core.game;

import com.readutf.inari.core.game.spectator.SpectatorData;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public interface GameLang {

    Collection<String> getSpectateMessage(Player player, SpectatorData data);

    Collection<String> getRespawnIntervalMessage(Player player, int interval);

}
