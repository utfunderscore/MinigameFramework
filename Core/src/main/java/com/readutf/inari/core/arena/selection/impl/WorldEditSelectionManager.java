package com.readutf.inari.core.arena.selection.impl;

import com.readutf.inari.core.arena.selection.SelectionManager;
import com.readutf.inari.core.utils.WorldCuboid;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class WorldEditSelectionManager implements SelectionManager {

    @Override
    public @Nullable WorldCuboid getSelection(Player player) {
        BukkitPlayer adapt = BukkitAdapter.adapt(player);

        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(adapt);
        try {
            Region selection = localSession.getSelection(BukkitAdapter.adapt(player.getWorld()));
            BlockVector3 max = selection.getMaximumPoint();
            BlockVector3 min = selection.getMinimumPoint();


            return new WorldCuboid(player.getWorld(), min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
        } catch (IncompleteRegionException e) {
            return null;
        }

    }
}
