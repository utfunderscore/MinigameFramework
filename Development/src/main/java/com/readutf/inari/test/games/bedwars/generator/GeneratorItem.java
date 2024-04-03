package com.readutf.inari.test.games.bedwars.generator;

import com.readutf.inari.core.game.Game;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.BlockVector;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

@Setter @Getter
public class GeneratorItem {

    private final ItemStack item;
    private final int ticksBetweenDrop;

    private int lastDrop;

    public GeneratorItem(ItemStack item, int ticksBetweenDrop) {
        this.item = item;
        this.ticksBetweenDrop = ticksBetweenDrop;
        this.lastDrop = MinecraftServer.currentTick;
    }

    public void drop(Generator generator, Location location) {

        Collection<Item> nearbyItems = location.getNearbyEntitiesByType(Item.class, 3);
        Optional<Item> inGenerator = nearbyItems.stream()
                .filter(item1 -> item1.getItemStack().isSimilar(item) && item1.getItemStack().getAmount() < item1.getItemStack().getMaxStackSize())
                .min(Comparator.comparingInt(value -> value.getItemStack().getAmount()));

        inGenerator.ifPresentOrElse(item1 -> {
            ItemStack itemStack = item1.getItemStack();
            itemStack.setAmount(itemStack.getAmount() + item.getAmount());
            item1.setItemStack(itemStack);
        }, () -> {
            Item droppedItem = location.getWorld().dropItem(location, item);

            droppedItem.setVelocity(new BlockVector(0, 0, 0));

            droppedItem.setMetadata("generator", new FixedMetadataValue(generator.getGame().getJavaPlugin(), generator.getId()));
        });

    }

}
