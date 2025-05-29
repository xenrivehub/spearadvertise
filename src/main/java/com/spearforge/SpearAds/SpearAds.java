package com.spearforge.sIslandAd;

import com.spearforge.sIslandAd.commands.MainCommand;
import com.spearforge.sIslandAd.listeners.ChatListener;
import com.spearforge.sIslandAd.listeners.InventoryClickListener;
import com.spearforge.sIslandAd.listeners.MainClickListener;
import com.spearforge.sIslandAd.managers.AdManager;
import com.spearforge.sIslandAd.managers.ConfigManager;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpearAds extends JavaPlugin {

    @Getter
    private static SpearAds plugin;
    @Getter
    private static ConfigManager configManager;
    @Getter
    private static Economy econ = null;

    @Override
    public void onEnable() {
        plugin = this;
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        saveDefaultConfig();
        configManager = new ConfigManager(this, "ads.yml");
        configManager.createCategories();
        getLogger().info("Spear ADS enabled successfully!");
        getCommand("ads").setExecutor(new MainCommand());
        registerListeners();
        AdManager.removeExpiredAdsAsync();

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new MainClickListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
    }

}
