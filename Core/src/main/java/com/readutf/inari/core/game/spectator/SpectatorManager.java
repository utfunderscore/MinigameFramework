package com.readutf.inari.core.game.spectator;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.exception.GameException;
import com.readutf.inari.core.game.spawning.SpawnFinder;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SpectatorManager {

    private final Game game;
    private final Map<UUID, SpectatorData> spectatorData;
    private final List<UUID> awaitingRejoin = new ArrayList<>();
    private final SpectatorTask spectatorTask;

    public SpectatorManager(Game game) {
        this.spectatorData = new HashMap<>();
        this.game = game;
        (spectatorTask = new SpectatorTask(game)).runTaskTimer(game.getJavaPlugin(), 0, 1);
    }

    public void setSpectator(UUID playerId, SpectatorData data) {
        spectatorData.put(playerId, data);

        if (data.isRespawn() && data.getRespawnAt() <= System.currentTimeMillis()) {
            respawnPlayer(playerId);
            return;
        }

        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {

            Location spawn;
            try {
                spawn = game.getSpectatorSpawnFinder().findSpawn(game, player);
            } catch (GameException e) {
                e.printStackTrace();
                player.sendMessage(ChatColor.RED + "Failed to find a spawn location for you.");
                return;
            }

            applyState(data, player, spawn);

            for (Component c : game.getLang().getSpectateMessage(player, data)) {
                player.sendMessage(c);

            }
        }


    }

    public void respawnPlayer(UUID playerId) {

        if (!spectatorData.containsKey(playerId)) return;

        spectatorData.remove(playerId);

        SpawnFinder spawnFinder = game.getPlayerSpawnFinder();

        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {

            //spawn player
            Location spawn = null;
            try {
                spawn = spawnFinder.findSpawn(game, player);
            } catch (GameException e) {
                e.printStackTrace();
                player.sendMessage(ChatColor.RED + "Failed to find a spawn location for you.");
                return;
            }
            player.teleport(spawn);

            //reset player state
            revertState(player);


            player.sendMessage(ChatColor.GREEN + "You have respawned!");
        } else {
            awaitingRejoin.add(playerId);
        }

    }

    public void revertState(Player player) {
        player.clearActivePotionEffects();
        player.setAllowFlight(false);
        player.setFlying(false);
        for (Player alivePlayer : game.getOnlineAndAlivePlayers()) {
            alivePlayer.showPlayer(player);
        }
    }

    public void applyState(SpectatorData data, Player player, Location spawn) {
        player.teleport(spawn);
        player.setAllowFlight(data.isCanFly());
        player.setFlying(true);
        for (Player alivePlayer : game.getOnlineAndAlivePlayers()) {
            alivePlayer.hidePlayer(player);
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false, false));
    }

    public void shutdown() {
        spectatorTask.cancel();
    }

    public Collection<UUID> getSpectators() {
        return spectatorData.keySet();
    }

    public @Nullable SpectatorData getSpectatorData(UUID playerId) {
        return spectatorData.get(playerId);
    }


    public boolean isSpectator(UUID uniqueId) {
        return spectatorData.containsKey(uniqueId);
    }
}
