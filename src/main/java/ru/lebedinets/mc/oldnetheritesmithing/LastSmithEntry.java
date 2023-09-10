package ru.lebedinets.mc.oldnetheritesmithing;

import org.bukkit.Material;

import java.util.UUID;

public class LastSmithEntry {
    public Material srcItem;
    public Material item;
    public UUID smid;
    public long createTime;

    LastSmithEntry(Material srcItem, Material item, UUID smid, long createTime) {
        this.srcItem = srcItem;
        this.item = item;
        this.smid = smid;
        this.createTime = createTime;
    }
}
