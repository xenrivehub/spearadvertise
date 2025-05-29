package com.spearforge.sIslandAd.models;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class Category implements InventoryHolder {

    private final String category;

    public Category(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public Inventory getInventory() {
        return null; // kullanılmayacak
    }

}
