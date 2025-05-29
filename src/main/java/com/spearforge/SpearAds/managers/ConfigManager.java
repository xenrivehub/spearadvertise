package com.spearforge.sIslandAd.managers;

import com.spearforge.sIslandAd.SpearAds;
import lombok.Data;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Data
public class ConfigManager {

    private final Plugin plugin;
    private File file;
    private FileConfiguration config;
    private final String fileName;
    private final String folderPath;


    public ConfigManager(Plugin plugin, String fileName, String folderPath) {
        this.plugin = plugin;
        this.fileName = fileName.endsWith(".yml") ? fileName : fileName + ".yml";
        this.folderPath = folderPath;
        setup();
    }

    public ConfigManager(Plugin plugin, String fileName){
        this(plugin, fileName, null);
    }

    private void setup() {
        File folder = (folderPath != null) ?
                new File(plugin.getDataFolder(), folderPath) :
                plugin.getDataFolder();

        if (!folder.exists()) folder.mkdirs();

        file = new File(folder, fileName);

        if (!file.exists()) {
            try {
                file.createNewFile();
                plugin.getLogger().info("Created config file with name: " + file.getName());
            } catch (IOException e) {
                plugin.getLogger().severe("Can't create config file with name: " + file.getName());
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Can't save config with name: " + file.getName());
            e.printStackTrace();
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void createCategories() {
        if (!SpearAds.getPlugin().getConfig().getStringList("settings.categories").isEmpty()){
            List<String> categories = SpearAds.getPlugin().getConfig().getStringList("settings.categories");
            if (config.getString("ads") == null){
                for (String category : categories){
                    config.createSection("ads." + category);
                }

                try {
                    config.save(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            SpearAds.getPlugin().getLogger().warning("No categories found in the config file. Please add categories to the 'settings' section.");
        }
    }

}
