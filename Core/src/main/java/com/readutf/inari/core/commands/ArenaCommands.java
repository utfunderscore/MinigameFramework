package com.readutf.inari.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.readutf.inari.core.arena.Arena;
import com.readutf.inari.core.arena.ArenaManager;
import com.readutf.inari.core.arena.exceptions.ArenaLoadException;
import com.readutf.inari.core.arena.exceptions.ArenaStoreException;
import com.readutf.inari.core.arena.meta.ArenaMeta;
import com.readutf.inari.core.arena.selection.SelectionManager;
import com.readutf.inari.core.arena.stores.SchematicArenaManager;
import com.readutf.inari.core.utils.WorldCuboid;
import com.readutf.inari.core.utils.WorldEditTaskUtil;
import lombok.AllArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

@CommandAlias("arena")
@AllArgsConstructor
public class ArenaCommands extends BaseCommand {

    private final JavaPlugin javaPlugin;
    private final SelectionManager selectionManager;
    private final ArenaManager arenaManager;

    @Subcommand("create")
    public void createArena(Player player, String name) {

        WorldCuboid selection = selectionManager.getSelection(player);
        if (selection == null) {
            player.sendMessage(ChatColor.RED + "You must make a selection first.");
            return;
        }
        try {

            Arena arena = arenaManager.createArena(name, selection);

            player.sendMessage(ChatColor.GREEN + "Created arena " + arena.getName() + " with " + arena.getMarkers().size() + " markers.");
        } catch (ArenaStoreException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Could not create arena: " + e.getLocalizedMessage());
        }


    }

    @Subcommand("testload")
    public void testLoad(Player player) {

        try {


            List<ArenaMeta> availableArenas = arenaManager.findAvailableArenas(arenaMeta -> true);

            System.out.println(availableArenas);

            availableArenas.stream().findFirst().ifPresent(arenaMeta -> {
                try {
                    Arena arena = arenaManager.load(arenaMeta);

                    player.sendMessage(ChatColor.GREEN + "Loaded arena " + arenaMeta.getName());

                    player.teleport(arena.getBounds().getMin().toLocation(SchematicArenaManager.getWorld()));

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            arenaManager.unload(arena);
                        }
                    }.runTaskLater(javaPlugin, 20 * 5);

                } catch (ArenaLoadException e) {
                    e.getException().printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
