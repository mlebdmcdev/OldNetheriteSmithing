package ru.lebedinets.mc.oldnetheritesmithing;

import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/*
 * There used to be a lot of code here...
 * Initially, I simply listen for event when a player put items into smithing table and give result item to him.
 * Then I automatically inserted upgrade templates into slot and didn't let the player take them.
 * In the end, it turned out to be so simple that I'm even ashamed.
 * I found already existing plugin https://github.com/TrollsterCooleg/NoNetheriteTemplate,
 * and there was a proper implementation that simply adds additional recipes to the smithing table.
 * That plugin implementation doesn't have the option to set permissions because it simply globally registers new recipes.
 * So, use this plugin only if you need permissions; otherwise, use the one that was provided earlier.
 */

public final class OldNetheriteSmithing extends JavaPlugin implements Listener {

    private static final Map<Material, Material> resultMap = new HashMap<>();
    private final Set<UUID> currentlySmithingPlayers = new HashSet<>();
    private final UpdateChecker updateChecker = new UpdateChecker(this.getDescription(), this.getServer());

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

        int pluginId = 19782;
        Metrics metrics = new Metrics(this, pluginId);

        updateChecker.checkForUpdates();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("OldNetheriteSmithing has been stopped!");
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory closedInventory = event.getInventory();
        Player player = (Player) event.getPlayer();
        if (closedInventory.getType() == InventoryType.SMITHING &&
                closedInventory.getItem(0) != null &&
                Objects.requireNonNull(closedInventory.getItem(0)).getType() == Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE &&
                currentlySmithingPlayers.contains(player.getUniqueId())
        ) {
            ItemStack item1 = closedInventory.getItem(1);
            ItemStack item2 = closedInventory.getItem(2);
            if (item1 != null) {
                player.getInventory().addItem(item1);
            }
            if (item2 != null) {
                player.getInventory().addItem(item2);
            }
            closedInventory.clear();
        }
        currentlySmithingPlayers.remove(player.getUniqueId());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getType() == InventoryType.SMITHING && currentlySmithingPlayers.contains(event.getWhoClicked().getUniqueId())) {
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE) {
                event.setCancelled(true);
            }
            if (event.getRawSlot() == event.getInventory().getSize() - 1) {
                currentlySmithingPlayers.remove(event.getWhoClicked().getUniqueId());
            }
        }
    }

    @EventHandler
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        ItemStack[] inputItems = event.getInventory().getContents();
        Player player = (Player) event.getView().getPlayer();

        if (player.isPermissionSet("oldnetheritesmithing.smith") && !player.hasPermission("oldnetheritesmithing.smith")) {
            return;
        }

        if ((inputItems[1] == null || inputItems[2] == null) && currentlySmithingPlayers.contains(player.getUniqueId())) {
            event.getInventory().removeItem(new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
            currentlySmithingPlayers.remove(player.getUniqueId());
        }

        if (inputItems.length >= 3 && inputItems[1] != null && inputItems[2] != null) {
            Material item1 = inputItems[1].getType();
            Material item2 = inputItems[2].getType();
            if (item2 == Material.NETHERITE_INGOT && resultMap.containsKey(item1)) {
                currentlySmithingPlayers.add(player.getUniqueId());

                ItemStack smithingTemplate = new ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
                event.getInventory().addItem(smithingTemplate);
            }
        }
    }
}
