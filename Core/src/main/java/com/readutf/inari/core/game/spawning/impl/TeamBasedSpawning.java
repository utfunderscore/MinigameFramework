package com.readutf.inari.core.game.spawning.impl;

import com.readutf.inari.core.arena.ActiveArena;
import com.readutf.inari.core.arena.marker.Marker;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.exception.GameException;
import com.readutf.inari.core.game.spawning.SpawnFinder;
import com.readutf.inari.core.logging.Logger;
import com.readutf.inari.core.logging.LoggerManager;
import com.readutf.inari.core.utils.NumberUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TeamBasedSpawning implements SpawnFinder {

    private static Logger logger = LoggerManager.getInstance().getLogger(TeamBasedSpawning.class);

    private final String prefix;

    public TeamBasedSpawning(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public @NotNull Location findSpawn(Game game, Player player) throws GameException {
        int teamId = game.getTeamIndex(player.getUniqueId()) + 1;
        List<Location> spawnLocations = getTeamSpawns(game.getArena()).getOrDefault(teamId, null);

        if(spawnLocations == null || spawnLocations.isEmpty()) {
            logger.warn("No spawn locations found for team " + teamId);
            throw new GameException("No spawn locations found for team " + teamId);
        }

        Location bestSpawn = spawnLocations.get(0);
        int nearbyBest = Integer.MAX_VALUE;
        for (Location spawnLocation : spawnLocations) {
            int nearby = spawnLocation.getNearbyEntitiesByType(Player.class, 2).size();
            if(nearby == 0) return spawnLocation;
            if(nearby < nearbyBest) {
                nearbyBest = nearby;
                bestSpawn = spawnLocation;
            }
        }

        return bestSpawn;
    }

    HashMap<ActiveArena, HashMap<Integer, List<Location>>> teamSpawnsCache = new HashMap<>();

    public HashMap<Integer, List<Location>> getTeamSpawns(ActiveArena activeArena) {

        HashMap<Integer, List<Location>> existingSpawns = teamSpawnsCache.get(activeArena);
        if(existingSpawns != null) return existingSpawns;

        logger.debug("No existing cache exists, finding spawns.");

        List<Marker> markers = activeArena.getMarkers(prefix);
        HashMap<Integer, List<Location>> teamSpawns = new HashMap<>();

        for (Marker marker : markers) {
            String id = marker.getName().split(":")[1];
            Integer teamId = NumberUtils.parseInt(id);
            if(teamId == null) {
                logger.warn("Invalid team id: " + id);
                continue;
            }
            List<Location> existing = teamSpawns.getOrDefault(teamId, new ArrayList<>());
            existing.add(marker.toLocation(activeArena.getWorld()));
            teamSpawns.put(teamId, existing);
        }

        teamSpawnsCache.put(activeArena, teamSpawns);
        return teamSpawns;
    }

}
