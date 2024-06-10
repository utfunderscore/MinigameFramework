package com.readutf.inari.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.arena.exceptions.ArenaStoreException;
import com.readutf.inari.core.arena.meta.ArenaMeta;
import com.readutf.inari.core.arena.selection.SelectionManager;
import com.readutf.inari.core.utils.ColorUtils;
import com.readutf.inari.core.utils.WorldCuboid;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@CommandAlias( "arena" )
@AllArgsConstructor
public class ArenaCommands extends BaseCommand {

    private final JavaPlugin javaPlugin;
    private final SelectionManager selectionManager;
    private final ArenaManager arenaManager;

    @Subcommand( "create" )
    public void createArena(Player player, String name) {

        WorldCuboid selection = selectionManager.getSelection(player);
        if (selection == null) {
            player.sendMessage(ChatColor.RED + "You must make a selection first.");
            return;
        }
        try {
            ArenaMeta arena = arenaManager.createArena(name, selection);

            player.sendMessage(ColorUtils.color("&aCreated arena " + arena.getName() + " with " + arena.getNumOfMarkers() + " markers."));
        } catch (ArenaStoreException e) {
            e.printStackTrace();
            player.sendMessage(ColorUtils.color("&cCould not create arena: " + e.getLocalizedMessage()));
        }


    }

    @Subcommand( "list" )
    public void listArenas(Player player) {
        List<ArenaMeta> allArenas = arenaManager.findAvailableArenas(arenaMeta -> true);

        player.sendMessage(ColorUtils.color("&aAvailable Arenas:"));
        for (ArenaMeta allArena : allArenas) {
            player.sendMessage(ColorUtils.color(" &8* &f%s &7(%s markers)".formatted(allArena.getName(), allArena.getNumOfMarkers())));
        }

    }

}
