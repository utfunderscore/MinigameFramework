package com.readutf.inari.test.games.sumo;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.stage.Round;
import com.readutf.inari.core.game.team.Team;
import com.readutf.inari.core.utils.ColorUtils;
import com.readutf.inari.core.utils.Cuboid;
import com.readutf.inari.test.games.shared.BuildPreventionListeners;
import com.readutf.inari.test.utils.Countdown;
import com.readutf.inari.test.utils.ThreadUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Getter
public class SumoRound implements Round {

    private static final HashSet<Integer> countdownIntervals = new HashSet<>(Arrays.asList(15, 10, 5, 4, 3, 2, 1));
    private static final Map<Integer, Float> intervalToPitch = Map.of(
            3, 0.75f,
            2, 1f,
            1, 1.25f,
            0, 2f
    );

    private static final Map<Integer, Title> intervalToTitle = Map.of(
            5, Title.title(Component.text("5").color(TextColor.color(255, 170, 0)), Component.empty()),
            4, Title.title(Component.text("4").color(TextColor.color(255, 255, 85)), Component.empty()),
            3, Title.title(Component.text("3").color(TextColor.color(255, 255, 85)), Component.empty()),
            2, Title.title(Component.text("2").color(TextColor.color(255, 255, 85)), Component.empty()),
            1, Title.title(Component.text("1").color(TextColor.color(85, 255, 85)), Component.empty())
    );

    private final Game game;
    private final SumoRound previousRound;
    private final Map<Team, Integer> teamScores;
    private final Cuboid safeZone;

    private final int roundNumber;
    private Countdown countdown;

    public SumoRound(Game game, SumoRound previousRound) {
        this.previousRound = previousRound;
        this.game = game;
        this.roundNumber = previousRound == null ? 1 : previousRound.getRoundNumber() + 1;
        this.teamScores = previousRound == null ? new HashMap<>() : new HashMap<>(previousRound.getTeamScores());
        if(previousRound == null) {
            game.registerListeners(new SumoListeners(game));
            game.registerListeners(new BuildPreventionListeners(true, true));
            for (Team aliveTeam : game.getAliveTeams()) teamScores.put(aliveTeam, 0);
        }

        safeZone = game.getArena().getCuboid("safezone:1", "safezone:2");
    }

    @Override
    public void roundStart() {

        for (Player onlinePlayer : game.getOnlineAndAlivePlayers()) {
            onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 150, false, false, false));
            onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 150, false, false, false));
        }

        countdown = new Countdown(game, 5, timeLeft -> {

            float pitch = intervalToPitch.getOrDefault(timeLeft, -1f);
            if (pitch != -1)
                game.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, 10f, pitch));

            Title title = intervalToTitle.get(timeLeft);
            if (title != null) game.getOnlinePlayers().forEach(player -> player.showTitle(title));

            if (countdownIntervals.contains(timeLeft)) {
                game.messageAlive(ColorUtils.color("&7Round starting in &a" + timeLeft + " &7seconds"));
            }

            if (timeLeft == 0) {
                countdownEnd();
                game.messageAlive(ColorUtils.color("&7Round &a" + roundNumber + " &7has started!"));
            }
        });

    }

    public void countdownEnd() {

        ThreadUtils.ensureSync(() -> {
            for (Player onlineAndAlivePlayer : game.getOnlineAndAlivePlayers()) {
                onlineAndAlivePlayer.removePotionEffect(PotionEffectType.JUMP);
                onlineAndAlivePlayer.removePotionEffect(PotionEffectType.SLOW);

                onlineAndAlivePlayer.clearTitle();

            }
        });

    }


    @Override
    public void roundEnd(Team winner) {
        if (game.getAliveTeams().isEmpty()) return;


    }

    @Override
    public boolean hasRoundEnded() {
        return false;
    }
}
