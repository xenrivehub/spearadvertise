package com.spearforge.sIslandAd.listeners;

import com.spearforge.sIslandAd.SpearAds;
import com.spearforge.sIslandAd.gui.MainGUI;
import com.spearforge.sIslandAd.models.Category;
import com.spearforge.sIslandAd.utils.TextUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MainClickListener implements Listener {

    @EventHandler
    public void onMainClick(InventoryClickEvent e){
        String title = e.getView().getTitle();

        if (!title.equalsIgnoreCase(TextUtils.color(SpearAds.getPlugin().getConfig().getString("gui.titles.main")))) return;
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        e.setCancelled(true);

        if (e.getCurrentItem().getType().equals(Material.valueOf(SpearAds.getPlugin().getConfig().getString("settings.fill-item")))) return;

        ConfigurationSection categories = SpearAds.getPlugin().getConfig().getConfigurationSection("gui.categories");
        String categoryName = "";
        for (String category : categories.getKeys(false)){
            int slot = categories.getInt(category + ".slot");
            if (slot == e.getSlot()){
                categoryName = category;
                break;
            }
        }
        if (SpearAds.getConfigManager().getConfig().getConfigurationSection("ads." + categoryName) == null) {
            e.getWhoClicked().sendMessage(TextUtils.getMessage("category-not-found").replace("%category%", categoryName));
            return;
        }
        e.getWhoClicked().closeInventory();
        MainGUI.openInsideCategory(categoryName, (Player) e.getWhoClicked());

    }
}
