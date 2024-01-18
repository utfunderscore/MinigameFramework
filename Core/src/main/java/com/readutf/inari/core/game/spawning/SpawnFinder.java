package com.readutf.inari.core.game.spawning;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.team.Team;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface SpawnFinder {

    @NotNull Location findSpawn(Game game, UUID uuid);

}
