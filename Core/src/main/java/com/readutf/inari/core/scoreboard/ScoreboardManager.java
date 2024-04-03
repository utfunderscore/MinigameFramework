package com.readutf.inari.core.scoreboard;

import com.readutf.inari.core.utils.ColorUtils;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ScoreboardManager extends BukkitRunnable implements Listener {

    private final Map<UUID, FastBoard> playerBoards;
    private final Map<UUID, ScoreboardProvider> playerProviders;

    public ScoreboardManager(JavaPlugin plugin) {
        this.playerBoards = new HashMap<>();
        this.playerProviders = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        runTaskTimer(plugin, 0, 5);
    }

    @Override
    public void run() {

        playerProviders.forEach((uuid, scoreboardProvider) -> {

            try {

                Player player = Bukkit.getPlayer(uuid);
                if(player == null) return;

                FastBoard fastBoard = playerBoards.get(uuid);
                Component currentTitle = fastBoard.getTitle();
                Component newTitle = ColorUtils.color(scoreboardProvider.getTitle(player));
                if (!currentTitle.equals(newTitle)) {
                    System.out.println("Updating title " + player.getName());
                    fastBoard.updateTitle(newTitle);
                }

                List<Component> currentLines = fastBoard.getLines();
                List<Component> newLines = scoreboardProvider.getLines(player).stream().map(ColorUtils::color).collect(Collectors.toList());


                fastBoard.updateLines(newLines);
            } catch (Exception e) {
                e.printStackTrace();
            }


        });

    }

    public void setPlayerBoard(Player player, ScoreboardProvider newProvider) {

        player.sendMessage("updating scoreboard");

        if(!playerBoards.containsKey(player.getUniqueId())) {
            FastBoard board = new FastBoard(player);
            playerBoards.put(player.getUniqueId(), board);
        }
        ScoreboardProvider currProvider = playerProviders.get(player.getUniqueId());
        if(currProvider != newProvider) {
            playerProviders.put(player.getUniqueId(), newProvider);
            run();
        }


    }

    public void clearPlayerBoard(Player player) {
        FastBoard remove = playerBoards.remove(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        playerProviders.remove(player.getUniqueId());
    }

    public void onQuit(PlayerQuitEvent e) {
        playerBoards.remove(e.getPlayer().getUniqueId());
        playerProviders.remove(e.getPlayer().getUniqueId());
    }

}
