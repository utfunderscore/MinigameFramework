package com.readutf.inari.core.game.team;

import com.readutf.inari.core.utils.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor @Getter @ToString
public class Team {

    private final String teamName;
    private final TeamColor color;
    private final List<UUID> players;

    @JsonIgnore
    public List<Player> getOnlinePlayers() {
        List<Player> list = new ArrayList<>();
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                list.add(player);
            }
        }
        return list;
    }

    public List<OfflinePlayer> getAllPlayers() {
        List<OfflinePlayer> list = new ArrayList<>();
        for (UUID uuid : players) {
            list.add(Bukkit.getOfflinePlayer(uuid));
        }
        return list;
    }

}
