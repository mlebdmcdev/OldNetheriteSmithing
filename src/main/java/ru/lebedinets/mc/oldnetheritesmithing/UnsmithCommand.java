package ru.lebedinets.mc.oldnetheritesmithing;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class UnsmithCommand implements CommandExecutor {
    private final Utils utils = new Utils();
    public static final Map<UUID, ArrayList> lastSmithMap = new HashMap<>();
    private final int timeToUnsmith = 30;
    private final TrackableItemManager trm;

    public UnsmithCommand(OldNetheriteSmithing plugin) {
        this.trm = new TrackableItemManager(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        if (!sender.hasPermission("oldnetheritesmith.unsmith")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (!lastSmithMap.containsKey(uuid)) {
            sender.sendMessage(ChatColor.RED + "You have nothing to undo!");
            return true;
        }

        ArrayList mixedList = lastSmithMap.get(uuid);
        Material srcItem = (Material) mixedList.get(0);
        Material item = (Material) mixedList.get(1);
        UUID smid = (UUID) mixedList.get(2);
        long createTime = (long) mixedList.get(3);
        long currentTime = System.currentTimeMillis();

        long expireTime = createTime + (timeToUnsmith * 1000L);

        if (currentTime > expireTime) {
            sender.sendMessage(ChatColor.RED + "Undo time left! (" + timeToUnsmith + " seconds since item created)");
            return true;
        }

        ItemStack finalItem = null;
        boolean hasItem = false;
        Inventory inventory = player.getInventory();
        for (ItemStack bItem : inventory.getContents()) {
            String rSmid = trm.getValueFromItem(bItem, "smid");
            if (bItem != null && bItem.getType() == srcItem && Objects.equals(smid.toString(), rSmid)) {
                hasItem = true;
                finalItem = bItem;
                break;
            }
        }

        if (!hasItem) {
            sender.sendMessage(ChatColor.RED + "You have no smithing result item in your inventory!");
            return true;
        }

        player.getInventory().removeItem(finalItem);

        lastSmithMap.remove(uuid);

        utils.giveItemToPlayer(player, new ItemStack(item));
        utils.giveItemToPlayer(player, new ItemStack(Material.NETHERITE_INGOT));

        return true;
    }
}