package com.readutf.inari.core.scoreboard;

import org.bukkit.entity.Player;

import java.util.List;

public interface ScoreboardProvider {

    String getTitle(Player player);

    List<String> getLines(Player player);

}
