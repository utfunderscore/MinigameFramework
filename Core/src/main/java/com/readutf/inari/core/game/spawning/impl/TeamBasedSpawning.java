package com.readutf.inari.core.game.spawning.impl;

import com.readutf.inari.core.arena.ActiveArena;
import com.readutf.inari.core.arena.marker.Marker;
import com.readutf.inari.core.game.Game;
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

    private final Map<Integer, List<Location>> teamSpawns;

    public TeamBasedSpawning(Map<Integer, List<Location>> teamSpawns) {
        if(teamSpawns == null) throw new IllegalArgumentException("Team spawns cannot be null");
        if(teamSpawns.isEmpty()) throw new IllegalArgumentException("Team spawns cannot be empty");
        this.teamSpawns = teamSpawns;
    }

    @Override
    public @NotNull Location findSpawn(Game game, UUID uuid) {
        List<Location> spawnLocations = teamSpawns.getOrDefault(game.getTeamIndex(uuid)+1, new ArrayList<>());

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

    public static TeamBasedSpawning fromArena(ActiveArena activeArena, String prefix) {
        List<Marker> markers = activeArena.getMarkers(prefix);
        HashMap<Integer, List<Location>> teamSpawns = new HashMap<>();
        System.out.println("found markers: " + markers);

        for (Marker marker : markers) {
            String id = marker.getName().split(":")[1];
            Integer teamId = NumberUtils.parseInt(id);
            if(teamId == null) {
                logger.warn("Invalid team id: " + id);
                continue;
            }
            List<Location> existing = teamSpawns.getOrDefault(teamId, new ArrayList<>());
            existing.add(marker.getPosition().toLocation(activeArena.getWorld()));
            teamSpawns.put(teamId, existing);
        }
        System.out.println(teamSpawns);

        return new TeamBasedSpawning(teamSpawns);
    }

}
