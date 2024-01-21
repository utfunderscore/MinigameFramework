package com.readutf.inari.core.game.lang;

import com.readutf.inari.core.game.GameLang;
import com.readutf.inari.core.game.spectator.SpectatorData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class DefaultGameLang implements GameLang {

    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacy('&');


    @Override
    public Collection<Component> getGameSummaryMessage(Player player) {
        return List.of(serializer.deserialize("&aMatch Complete"));
    }

    @Override
    public List<Component> getSpectateMessage(Player player, SpectatorData data) {
        if (data.isRespawn()) {
            return List.of(serializer.deserialize("&7You died! You will respawn in &c" + (data.getDurationMillis() / 1000) + " &7seconds."));
        } else {
            return List.of(serializer.deserialize("&7You died! You will not respawn."));
        }
    }

    @Override
    public Collection<Component> getRespawnIntervalMessage(Player player, int interval) {
        return List.of(serializer.deserialize("&7You will respawn in &c" + interval + " &7seconds."));
    }
}
