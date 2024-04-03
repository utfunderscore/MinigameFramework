package com.readutf.inari.test.games.bedwars.generator;

import com.readutf.inari.core.event.GameEventHandler;
import com.readutf.inari.core.game.Game;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import java.util.*;

public class GeneratorListeners {

    private final Game game;
    private final GeneratorManager generatorManager;
    private final Map<Material, HashSet<UUID>> pickupHistory;

    public GeneratorListeners(GeneratorManager generatorManager, Game game) {
        this.game = game;
        this.generatorManager = generatorManager;
        this.pickupHistory = new HashMap<>();
    }

    @GameEventHandler
    public void onItemPickup(EntityPickupItemEvent e) {

        Generator generator = generatorManager.getNearbyGenerator(e.getItem().getItemStack(), e.getItem().getLocation());

        if (generator != null) {

            System.out.println("Item pickup " + e.getItem().getItemStack().getAmount());

            e.setCancelled(true);
            ItemStack itemStack = e.getItem().getItemStack();
            e.getItem().remove();

            List<Player> nearbyPlayers = new ArrayList<>();
            for (Entity nearbyEntity : generator.getLocation().getNearbyEntities(3, 3, 3)) {
                if (nearbyEntity instanceof Player player) nearbyPlayers.add(player);
            }

            // Give single item to a player who picked up the longest time ago
            if (itemStack.getAmount() <= 1) {

                HashSet<UUID> history = pickupHistory.getOrDefault(itemStack.getType(), new HashSet<>());
                List<Player> pickupPlayers = new ArrayList<>(nearbyPlayers);
                pickupPlayers.removeIf(player -> history.contains(player.getUniqueId()));
                if (pickupPlayers.isEmpty()) {
                    history.clear();
                    pickupPlayers = new ArrayList<>(nearbyPlayers);
                }

                Player pickedPlayer = pickupPlayers.getFirst();
                pickedPlayer.getInventory().addItem(itemStack);
                history.add(pickedPlayer.getUniqueId());
                pickupHistory.put(itemStack.getType(), history);

                pickedPlayer.playSound(pickedPlayer, Sound.ENTITY_ITEM_PICKUP, 1, 2);

            } else {

                List<Integer> amounts = getItemAmounts(itemStack.getAmount(), nearbyPlayers.size());
                for (int i = 0; i < amounts.size(); i++) {
                    Player player = nearbyPlayers.get(i);
                    int amount = amounts.get(i);

                    itemStack.setAmount(amount);
                    player.getInventory().addItem(itemStack);
                    player.playSound(player, Sound.ENTITY_ITEM_PICKUP, 1, 5);
                }
            }
        }
    }

    private List<Integer> getItemAmounts(int stackSize, int numOfPeople) {

        int amount = Math.floorDiv(stackSize, numOfPeople);

        int d = stackSize / numOfPeople;
        int r = stackSize % numOfPeople; // remainder

        List<Integer> sizes = new ArrayList<>();

        for (int i = 0; i < numOfPeople; i++) {
            sizes.add(d + (i < r ? 1 : 0));
        }

        return sizes;
    }


}
