package com.readutf.inari.core.commands.completions;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameEndReason;
import com.readutf.inari.core.game.GameManager;
import com.readutf.inari.core.utils.ColorUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

@CommandAlias( "gamemaker" )
@RequiredArgsConstructor
public class GameMakerCommand extends BaseCommand {

    private final GameManager gameManager;

    @Subcommand( "cancel" )
    public void cancel(CommandSender sender, String gameId) {

        Game gameById = gameManager.getGameById(gameId);

        if (gameId == null) {
            sender.sendMessage(ColorUtils.color("&cGame not found."));
            return;
        }

        gameById.endGame(null, GameEndReason.CANCELLED);
    }


}
