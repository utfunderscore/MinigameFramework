package com.readutf.inari.test.games.bedwars;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.stage.Round;
import com.readutf.inari.core.game.team.Team;
import com.readutf.inari.core.logging.Logger;
import com.readutf.inari.core.utils.AngleUtils;
import com.readutf.inari.core.utils.ColorUtils;
import com.readutf.inari.test.games.bedwars.generator.*;
import lombok.Getter;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class BedwarsRound implements Round {

    private final Logger logger;

    private final Game game;
    private final GeneratorManager generatorManager;
    private final Map<Location, Team> bedToTeam;

    public BedwarsRound(Game game) {
        this.game = game;
        this.logger = game.getLoggerFactory().getLogger(getClass());
        this.generatorManager = new GeneratorManager(game);
        this.bedToTeam = new HashMap<>();
        game.setScoreboard(new BedwarsScoreboard(this));
        game.registerListeners(new BasicBedwarsListeners(this));
    }

    @Override
    public void roundStart() {

        game.registerListeners(new GeneratorListeners(generatorManager, game));

        game.getArena().getMarkers("islandgen").forEach(marker -> {

            logger.info("Creating generator at " + marker.getPosition().toString());

            Location generatorLocation = marker.getPosition().toLocation(game.getArena().getWorld()).add(0.5, 1, 0.5);
            generatorManager.createGenerator(new Generator(game, generatorLocation, List.of(), List.of(
                    new GeneratorLevel(List.of(
                            new GeneratorItem(new ItemStack(Material.IRON_INGOT), 20)
                    ), Duration.ofMinutes(5))
            )));

        });


        game.getArena().getMarkers("bed").forEach(marker -> {

            logger.info("Creating generator at " + marker.getPosition().toString());

            Location location = marker.getPosition().toLocation(game.getArena().getWorld());
            Block block = location.getBlock();
            block.setType(Material.RED_BED);
            BlockData blockData = block.getBlockData();
            if (blockData instanceof Bed bed) {
                bed.setPart(Bed.Part.HEAD);
            }
            block.setBlockData(blockData);
            block.getRelative(AngleUtils.yawToFace(marker.getYaw())).setType(Material.RED_BED);

            game.getOnlinePlayers().stream().min(Comparator.comparingDouble(value -> value.getLocation().distanceSquared(location))).ifPresent(player -> {
                Team team = game.getTeamByPlayer(player);
                bedToTeam.put(location, team);
            });

        });
    }

    public Team getTeamFromBedPosition(Location location) {
        return bedToTeam.entrySet().stream()
                .min(Comparator.comparingDouble(value -> value.getKey().distanceSquared(location)))
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    @Override
    public void roundEnd(@Nullable Team winnerTeam) {

    }

    @Override
    public boolean hasRoundEnded() {
        return false;
    }

    public void destroyBed(Player destroyer, BedwarsTeam team) {

        Team destroyerTeam = game.getTeamByPlayer(destroyer);
        if(destroyerTeam == null) {
            logger.error("Destroyers team should not be null!");
            return;
        }

        team.setHasBed(false);

        for (Team gameTeam : game.getTeams()) {


            for (Player onlinePlayer : gameTeam.getOnlinePlayers()) {
                if (gameTeam == team) {
                    onlinePlayer.showTitle(Title.title(ColorUtils.color("&cBED DESTROYED"), ColorUtils.color("&7You can no longer respawn!")));
                    onlinePlayer.playSound(onlinePlayer, Sound.ENTITY_ENDER_DRAGON_AMBIENT, 0, 1);
                }

                onlinePlayer.sendMessage("");
                onlinePlayer.sendMessage(ColorUtils.color("&f&lBED DESTRUCTION > %s%s &7Bed was destroyed by %s%s".formatted(
                        team.getColor().getColorCode(),
                        team.getTeamName(),
                        destroyerTeam.getColor().getColorCode(),
                        destroyer.getName()
                )));
                onlinePlayer.sendMessage("");

            }

        }
    }

}
