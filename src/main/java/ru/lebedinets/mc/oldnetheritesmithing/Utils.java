package ru.lebedinets.mc.oldnetheritesmithing;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Utils {
    public void decreaseItem(PrepareSmithingEvent event, int invId, ItemStack itemStack, int amountToDecrease) {
        if (itemStack.getAmount() <= amountToDecrease) {
            event.getInventory().setItem(invId, null);
            return;
        }
        itemStack.setAmount(itemStack.getAmount() - amountToDecrease);
    }

    public boolean isInventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public void giveItemToPlayer(Player player, ItemStack itemStack) {
        if (isInventoryFull(player)) {
            Location playerLocation = player.getLocation();
            Item itemEntity = (Item) playerLocation.getWorld().spawnEntity(playerLocation, EntityType.DROPPED_ITEM);
            itemEntity.setItemStack(itemStack);
            itemEntity.setVelocity(new Vector(0.0, 0.1, 0.0));
            return;
        }
        player.getInventory().addItem(itemStack);
    }
}
