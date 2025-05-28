package com.spearforge.sIslandAd;

import com.spearforge.sIslandAd.managers.ConfigManager;
import lombok.Data;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class SIslandAd extends JavaPlugin {

    @Getter
    private static SIslandAd plugin;
    @Getter
    private static ConfigManager configManager;

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("SIslandAd has been enabled!");
        saveDefaultConfig();
        configManager = new ConfigManager(this, "players.yml");

    }

}
