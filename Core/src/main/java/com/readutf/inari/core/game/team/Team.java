package com.readutf.inari.core.game.team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor @Getter
public class Team {

    private final String teamName;
    private final ChatColor color;
    private final List<UUID> players;

}
