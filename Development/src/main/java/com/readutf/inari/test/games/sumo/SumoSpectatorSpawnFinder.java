package com.readutf.inari.test.games.sumo;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.exception.GameException;
import com.readutf.inari.core.game.spawning.SpawnFinder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class SumoSpectatorSpawnFinder implements SpawnFinder {

    @Override
    public @NotNull Location findSpawn(Game game, Player player) throws GameException {
        UUID lastDamager = game.getDeathManager().getLastDamager(player.getUniqueId());
        Player lastDamagerPlayer = lastDamager == null ? null : Bukkit.getPlayer(lastDamager);
        return Objects.requireNonNullElse(lastDamagerPlayer, player).getLocation().add(0, 2, 0);

    }
}
