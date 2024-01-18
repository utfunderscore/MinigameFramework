package com.readutf.inari.core.game.lang;

import com.readutf.inari.core.game.GameLang;
import com.readutf.inari.core.game.spectator.SpectatorData;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class DefaultGameLang implements GameLang {

    @Override
    public List<String> getSpectateMessage(Player player, SpectatorData data) {
        if (data.isRespawn()) {
            return List.of(ChatColor.RED + "You died! You will respawn in " + (data.getDurationMillis() / 1000) + " seconds");
        } else {
            return List.of(ChatColor.RED + "You died! You will not respawn.");
        }
    }

    @Override
    public Collection<String> getRespawnIntervalMessage(Player player, int interval) {
        return List.of("&7You will respawn in &c" + interval + " &7seconds.");
    }
}
