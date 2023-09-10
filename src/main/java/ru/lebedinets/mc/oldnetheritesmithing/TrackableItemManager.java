package ru.lebedinets.mc.oldnetheritesmithing;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class TrackableItemManager {

    private final OldNetheriteSmithing plugin;
    public TrackableItemManager(OldNetheriteSmithing plugin) {
        this.plugin = plugin;
    }

    public ItemStack createTrackableItem(Material material, String key, String value) {
        ItemStack item = new ItemStack(material); // Replace with your desired item
        ItemMeta meta = item.getItemMeta();

        // Create the custom tag
        assert meta != null;
        meta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, key),
                PersistentDataType.STRING,
                value
        );

        item.setItemMeta(meta);
        return item;
    }

    public String getValueFromItem(ItemStack item, String key) {
        if (item == null) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);

        if (!container.has(namespacedKey, PersistentDataType.STRING)) {
            return null;
        }

        return container.get(namespacedKey, PersistentDataType.STRING);
    }
}
