package com.spearforge.sIslandAd.gui;

import com.spearforge.sIslandAd.SpearAds;
import com.spearforge.sIslandAd.managers.AdManager;
import com.spearforge.sIslandAd.models.Advertise;
import com.spearforge.sIslandAd.models.Category;
import com.spearforge.sIslandAd.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MainGUI {

    public static void openMainGUI(Player player){
        Inventory mainGUI = Bukkit.createInventory(null, 45, TextUtils.color(SpearAds.getPlugin().getConfig().getString("gui.titles.main")));
        ConfigurationSection categories = SpearAds.getPlugin().getConfig().getConfigurationSection("gui.categories");

        if (!categories.getKeys(false).isEmpty()){
            for (String key : categories.getKeys(false)){
                ItemStack item = new ItemStack(Material.valueOf(categories.getString(key + ".material")));
                String displayName = TextUtils.color(categories.getString(key + ".display-name"));
                List<String> lore = categories.getStringList(key + ".lore");
                int slot = categories.getInt(key + ".slot");
                lore.replaceAll(TextUtils::color);
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(displayName);
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
                mainGUI.setItem(slot, item);
            }
        } else {
            player.sendMessage(TextUtils.getMessage("categories-not-found"));
            return;
        }

        for (int i=0; i<mainGUI.getSize(); i++){
            if (mainGUI.getItem(i) == null || mainGUI.getItem(i).getType() == Material.AIR) {
                mainGUI.setItem(i, new ItemStack(Material.valueOf(SpearAds.getPlugin().getConfig().getString("settings.fill-item"))));
            }
        }
        player.openInventory(mainGUI);
    }


    public static void openInsideCategory(String categoryName, Player player){
        ConfigurationSection categorySection = SpearAds.getConfigManager().getConfig().getConfigurationSection("ads." + categoryName);
        Inventory categoryGUI = Bukkit.createInventory(new Category(categoryName), 54, TextUtils.color(SpearAds.getPlugin().getConfig().getString("gui.titles.category-title").replace("%category%", categoryName.substring(0, 1).toUpperCase() + categoryName.substring(1).toLowerCase())));

        if (categorySection == null) {
            Bukkit.getLogger().warning(TextUtils.getMessage("category-not-found").replace("%category%", categoryName));
            return;
        }

        // 17,18,26,27,35,36
        int emptySlots[] = {17,18,26,27,35,36};
        Set<Integer> blockedSlots = Arrays.stream(emptySlots).boxed().collect(Collectors.toSet());
        List<String> keys = new ArrayList<>(categorySection.getKeys(false));

        int currentSlot = 10;

        for (int i=0; i < SpearAds.getPlugin().getConfig().getInt("settings.max-ads-per-category"); i++){

            while (blockedSlots.contains(currentSlot)){
                currentSlot++;
            }
            Advertise advertise = new Advertise();

            if (i < keys.size()){
                String key = keys.get(i);
                advertise.setSlot(Integer.parseInt(key));
                String playerName = categorySection.getString(key + ".player");
                advertise.setPlayerName(playerName);
                String adContent = categorySection.getString(key + ".ad-content");
                advertise.setAdContent(adContent);
                advertise.setCategory(categoryName);
                int slot = categorySection.getInt(key + ".slot");
                advertise.setSlot(slot);
                String remainingTime = AdManager.getRemainingAdTime(SpearAds.getConfigManager().getConfig(), advertise);

                if (remainingTime.equalsIgnoreCase("expired")) {
                    AdManager.removeAd(advertise.getCategory(), advertise.getSlot());
                    break;
                }

                // from 10 + max 28 ads 34
                ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(TextUtils.getMessage("ads-display-name").replace("%player_name%", playerName));
                List<String> lore = new ArrayList<>();
                StringBuilder currentLine = new StringBuilder();
                int count = 0;

                for (String word : adContent.split(" ")) {
                    if (count >= 4) {
                        lore.add(TextUtils.color("&7" + currentLine.toString().trim()));
                        currentLine = new StringBuilder();
                        count = 0;
                    }
                    currentLine.append(word).append(" ");
                    count++;
                }

                if (currentLine.length() > 0) {
                    lore.add(TextUtils.color("&7" + currentLine.toString().trim()));
                }

                lore.add("");
                lore.add(TextUtils.color(remainingTime));
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
                categoryGUI.setItem(advertise.getSlot(), item);
                categoryGUI.setItem(4, createInfoItem());
            }
        }

        for (int i=10; i < 44; i++){
            if (blockedSlots.contains(i)) continue;
            if (categoryGUI.getItem(i) == null || categoryGUI.getItem(i).getType() == Material.AIR) {
                categoryGUI.setItem(i, createEmptySlot());
            }
        }
        for (int i=0; i<categoryGUI.getSize(); i++){
            if (categoryGUI.getItem(i) == null || categoryGUI.getItem(i).getType() == Material.AIR) {
                categoryGUI.setItem(i, new ItemStack(Material.valueOf(SpearAds.getPlugin().getConfig().getString("settings.fill-item"))));
            }
        }

        player.openInventory(categoryGUI);
    }

    private static ItemStack createEmptySlot() {
        int i = 0;
        i++;
        String path = "gui.items.empty-slot.";
        ItemStack item = new ItemStack(Material.valueOf(SpearAds.getPlugin().getConfig().getString(path + "material")));
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(TextUtils.color(SpearAds.getPlugin().getConfig().getString(path + ".display-name")));
        List<String> lore = SpearAds.getPlugin().getConfig().getStringList(path + ".lore");
        List<String> newLore = new ArrayList<>();
        for (String line : lore){
            newLore.add(TextUtils.color(line.replace("%price%", String.valueOf(SpearAds.getPlugin().getConfig().getDouble("settings.price")))));
        }
        itemMeta.setLore(newLore);
        item.setItemMeta(itemMeta);

        return item;
    }

    private static ItemStack createInfoItem(){
        ItemStack infoItem = new ItemStack(Material.valueOf(SpearAds.getPlugin().getConfig().getString("gui.items.info.material")));
        ItemMeta itemMeta = infoItem.getItemMeta();
        itemMeta.setDisplayName(TextUtils.color(SpearAds.getPlugin().getConfig().getString("gui.items.info.display-name")));
        List<String> lore = SpearAds.getPlugin().getConfig().getStringList("gui.items.info.lore");
        List<String> newLore = new ArrayList<>();
        for (String line : lore){
            newLore.add(TextUtils.color(line));
        }
        itemMeta.setLore(newLore);
        infoItem.setItemMeta(itemMeta);
        return infoItem;
    }

}
