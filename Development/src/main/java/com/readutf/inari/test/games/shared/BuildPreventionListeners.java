package com.readutf.inari.test.games.shared;

import com.readutf.inari.core.event.GameEventHandler;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.block.BlockBreakEvent;

@RequiredArgsConstructor
public class BuildPreventionListeners {

    private final boolean preventBuilding, preventBreaking;


    @GameEventHandler
    public void onBlockPlace(BlockBreakEvent e) {
        if(preventBuilding) e.setCancelled(true);
    }

    @GameEventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(preventBreaking) e.setCancelled(true);
    }

}
