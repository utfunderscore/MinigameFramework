package com.readutf.inari.core.event.adapters;

import com.readutf.inari.core.event.GameAdapterResult;
import com.readutf.inari.core.event.GameEventAdapter;
import com.readutf.inari.core.game.Game;
import com.readutf.inari.core.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageAdapter implements GameEventAdapter {

    private final GameManager gameManager;

    public EntityDamageAdapter(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public GameAdapterResult getGame(Event event) {

        if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
            Game game = damageByPlayer(entityDamageByEntityEvent);
            if (game != null) {
                return new GameAdapterResult(game);
            }
        } else if(event instanceof EntityDamageByBlockEvent entityDamageByBlockEvent) {
            Game game = damageByBlock(entityDamageByBlockEvent);
            if (game != null) {
                return new GameAdapterResult(game);
            }
        } else if(event instanceof EntityDamageEvent e) {
            Game game = defaultDamage(e);
            if (game != null) {
                return new GameAdapterResult(game);
            }
        }

        return new GameAdapterResult("No game found.");
    }

    public Game damageByPlayer(EntityDamageByEntityEvent event) {

        if (event.getEntity() instanceof Player player) {

            Game gameByPlayer = gameManager.getGameByPlayer(player);
            if (gameByPlayer != null) {
                return gameByPlayer;
            }

        }

        if (event.getDamager() instanceof Player player) {

            Game gameByPlayer = gameManager.getGameByPlayer(player);
            if (gameByPlayer != null) {
                return gameByPlayer;
            }
        }

        if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player player) {

            Game gameByPlayer = gameManager.getGameByPlayer(player);
            if (gameByPlayer != null) {
                return gameByPlayer;
            }
        }

        return null;
    }

    public Game damageByBlock(EntityDamageByBlockEvent event) {

        if (event.getEntity() instanceof Player player) {
            return gameManager.getGameByPlayer(player);
        }

        return null;
    }

    public Game defaultDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player player) {
            return gameManager.getGameByPlayer(player);
        }
        return null;
    }

}
