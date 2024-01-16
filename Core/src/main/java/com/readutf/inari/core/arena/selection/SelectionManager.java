package com.readutf.inari.core.arena.selection;

import com.readutf.inari.core.utils.Cuboid;
import com.readutf.inari.core.utils.WorldCuboid;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface SelectionManager {

    @Nullable WorldCuboid getSelection(Player player);

}
