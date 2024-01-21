package com.readutf.inari.test.games.sumo;

import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.stage.Round;
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
    private final int roundNumber;
    private Countdown countdown;

    public SumoRound(Game game, SumoRound previousRound) {
        this.previousRound = previousRound;
        this.game = game;
        this.roundNumber = previousRound == null ? 1 : previousRound.getRoundNumber() + 1;
        if(previousRound == null) {
            game.registerListeners(new SumoListeners(game));
        }
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
                game.messageAlive("Round starting in " + timeLeft + " seconds");
            }

            if (timeLeft == 0) {
                countdownEnd();
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
    public void roundEnd() {

    }

    @Override
    public boolean hasRoundEnded() {
        return false;
    }
}
