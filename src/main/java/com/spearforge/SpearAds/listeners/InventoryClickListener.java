package com.spearforge.sIslandAd.listeners;

import com.spearforge.sIslandAd.SpearAds;
import com.spearforge.sIslandAd.managers.AdManager;
import com.spearforge.sIslandAd.models.Advertise;
import com.spearforge.sIslandAd.models.Category;
import com.spearforge.sIslandAd.utils.TextUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

public class InventoryClickListener implements Listener {

    @Getter
    private static HashMap<UUID, Advertise> creatingAd = new HashMap();


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        // normal ADS - normal, ADS
        if (e.getClick().isLeftClick()) {
            if (e.getInventory().getHolder() instanceof Category categoryHolder) {
                e.setCancelled(true);
                String categoryName = categoryHolder.getCategory();
                if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
                ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
                if (itemMeta == null) return;
                if (!itemMeta.hasDisplayName()) return;
                String displayName = itemMeta.getDisplayName();

                if (e.getSlot() == 4) return;


                if (!displayName.equalsIgnoreCase(TextUtils.color(SpearAds.getPlugin().getConfig().getString("gui.items.empty-slot.display-name")))) {
                    e.getWhoClicked().sendMessage(TextUtils.getMessage("not-empty-slot"));
                    return;
                }
                Player player = (Player) e.getWhoClicked();
                if (e.getWhoClicked().hasPermission("spearadvertise.ban") && !player.isOp()) {
                    e.getWhoClicked().sendMessage(TextUtils.getMessage("ban-permission"));
                    return;
                }
                if (AdManager.hasAdvertisement(categoryName, player.getName())) {
                    e.getWhoClicked().sendMessage(TextUtils.getMessage("already-has-advertisement"));
                    return;
                }


                int adSlot = e.getSlot();
                ConfigurationSection categorySection = SpearAds.getConfigManager().getConfig().getConfigurationSection("ads." + categoryName);
                if (categorySection == null) {
                    e.getWhoClicked().sendMessage(TextUtils.getMessage("category-not-found").replace("%category%", categoryName));
                    return;
                }

                double balance = SpearAds.getEcon().getBalance(player);
                double price = SpearAds.getPlugin().getConfig().getDouble("settings.price");
                if (balance >= price) {
                    SpearAds.getEcon().withdrawPlayer(player, price);
                    player.sendMessage(TextUtils.getMessage("creating-first-step").replace("%max_length%", String.valueOf(SpearAds.getPlugin().getConfig().getInt("settings.max-length"))).replace("%cancel_word%", SpearAds.getPlugin().getConfig().getString("messages.cancel-word")));
                    Advertise advertise = new Advertise();
                    advertise.setSlot(adSlot);
                    advertise.setPlayerName(player.getName());
                    advertise.setCategory(categoryName);
                    creatingAd.put(player.getUniqueId(), advertise);
                    player.closeInventory();
                } else {
                    player.sendMessage(TextUtils.getMessage("not-enough-money").replace("%price%", String.valueOf(price)));
                    return;
                }

            }
        } else if (e.getClick().isRightClick()) {
            if (e.getInventory().getHolder() instanceof Category categoryHolder) {
                e.setCancelled(true);
                String categoryName = categoryHolder.getCategory();
                e.setCancelled(true);
                Advertise advertise = AdManager.getAd(categoryName, e.getSlot());
                Player player = (Player) e.getWhoClicked();
                if (advertise != null) {
                    if (advertise.getPlayerName().equalsIgnoreCase(player.getName())) {
                        AdManager.removeAd(categoryName, e.getSlot());
                        player.closeInventory();
                        player.sendMessage(TextUtils.getMessage("ad-removed"));
                    } else {
                        player.sendMessage(TextUtils.getMessage("not-your-advertisement"));
                    }
                }
            }
        }
    }

}
