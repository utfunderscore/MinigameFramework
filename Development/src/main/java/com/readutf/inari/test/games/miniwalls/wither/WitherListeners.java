package com.readutf.inari.test.games.miniwalls.wither;


import com.readutf.inari.core.event.GameEventHandler;
import com.readutf.inari.test.games.miniwalls.MiniWallsTeam;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class WitherListeners  {

    private static final long DAMAGE_COOLDOWN = 1000;


    private final WitherManager witherManager;
    private final Map<Integer, Long> lastDamage = new HashMap<>();

    @GameEventHandler
    public void onWitherDamage(EntityDamageByEntityEvent e) {

        if(!(e.getDamager() instanceof Player player)) return;

        if(e.getEntity() instanceof Wither wither) {

            MiniWallsTeam team = witherManager.getRound().getTeam(player.getUniqueId());
            if (witherManager.getWither(team).getEntityId() == wither.getEntityId()) {
                e.setCancelled(true);
            }

            if(System.currentTimeMillis() - lastDamage.getOrDefault(wither.getEntityId(), 0L) > DAMAGE_COOLDOWN) {
                lastDamage.put(wither.getEntityId(), System.currentTimeMillis());
                wither.setHealth(wither.getHealth() - e.getDamage());
            }

        }

    }

}
