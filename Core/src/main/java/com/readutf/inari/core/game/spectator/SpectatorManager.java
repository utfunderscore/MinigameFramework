package com.readutf.inari.core.game.spectator;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.spawning.SpawnFinder;
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
        Location spawn = game.getSpectatorSpawnFinder().findSpawn(game, playerId);

        if(data.isRespawn() && data.getRespawnAt() <= System.currentTimeMillis()) {
            respawnPlayer(playerId);
            return;
        }

        Player player = Bukkit.getPlayer(playerId);
        if(player != null) {

            player.teleport(spawn);
            player.setAllowFlight(data.isCanFly());
            for (String s : game.getLang().getSpectateMessage(player, data)) {
                player.sendMessage(s);
            }

            for (Player alivePlayer : game.getOnlineAndAlivePlayers()) {
                alivePlayer.hidePlayer(player);
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false, false));
        }


    }

    public void respawnPlayer(UUID playerId) {

        if(!spectatorData.containsKey(playerId)) return;

        spectatorData.remove(playerId);

        SpawnFinder spawnFinder = game.getPlayerSpawnFinder();

        Player player = Bukkit.getPlayer(playerId);
        if(player != null) {

            //spawn player
            Location spawn = spawnFinder.findSpawn(game, playerId);
            player.teleport(spawn);

            //reset player state
            player.clearActivePotionEffects();
            for (Player onlinePlayer : game.getOnlinePlayers()) {
                onlinePlayer.showPlayer(player);
            }


            player.sendMessage(ChatColor.GREEN + "You have respawned!");
        } else {
            awaitingRejoin.add(playerId);
        }

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
