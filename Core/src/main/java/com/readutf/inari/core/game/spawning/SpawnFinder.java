package com.readutf.inari.core.game.spawning;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.exception.GameException;
import com.readutf.inari.core.game.team.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface SpawnFinder {

    @NotNull Location findSpawn(Player player) throws GameException;

}
