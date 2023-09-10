package ru.lebedinets.mc.oldnetheritesmithing;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class OldNetheriteSmithing extends JavaPlugin implements Listener {

    private static final Map<Material, Material> resultMap = new HashMap<>();
    private final Utils utils = new Utils();
    private final UnsmithCommand unsmithCommand = new UnsmithCommand(this);
    private final TrackableItemManager trm = new TrackableItemManager(this);

    private void populateResultMap() {
        resultMap.put(Material.DIAMOND_AXE, Material.NETHERITE_AXE);
        resultMap.put(Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE);
        resultMap.put(Material.DIAMOND_HOE, Material.NETHERITE_HOE);
        resultMap.put(Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL);
        resultMap.put(Material.DIAMOND_SWORD, Material.NETHERITE_SWORD);
        resultMap.put(Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS);
        resultMap.put(Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE);
        resultMap.put(Material.DIAMOND_HELMET, Material.NETHERITE_HELMET);
        resultMap.put(Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("OldNetheriteSmithing has been started!");

        populateResultMap();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("unsmith").setExecutor(unsmithCommand);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("OldNetheriteSmithing has been stopped!");
    }

    @EventHandler
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        ItemStack[] inputItems = event.getInventory().getContents();
        Player player = (Player) event.getView().getPlayer();
        long currentTime = System.currentTimeMillis();
        if (inputItems.length >= 3 && inputItems[1] != null && inputItems[2] != null) {
            Material item1 = inputItems[1].getType();
            Material item2 = inputItems[2].getType();
            if (item2 == Material.NETHERITE_INGOT && resultMap.containsKey(item1)) {
                UUID newItemUuid = UUID.randomUUID();
                ItemStack itemStack = trm.createTrackableItem(resultMap.get(item1), "smid", newItemUuid.toString());

                LastSmithEntry lastSmithEntry = new LastSmithEntry(resultMap.get(item1), item1, newItemUuid, currentTime);

                utils.decreaseItem(event, 1, inputItems[1], 1);
                utils.decreaseItem(event, 2, inputItems[2], 1);

                // small delay to prevent item deletion
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        utils.giveItemToPlayer(player, itemStack);
                        UnsmithCommand.lastSmithMap.put(player.getUniqueId(), lastSmithEntry);
                    }
                }.runTaskLater(this, 10);
            }
        }
    }
}
