package com.spearforge.sIslandAd.managers;

import com.spearforge.sIslandAd.SpearAds;
import com.spearforge.sIslandAd.models.Advertise;
import com.spearforge.sIslandAd.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AdManager {

    private static final long AD_DURATION_HOURS = SpearAds.getPlugin().getConfig().getLong("settings.duration");
    private static FileConfiguration adsConfig = SpearAds.getConfigManager().getConfig();

    public static void createAd(Player player, Advertise advertise) {
        ConfigurationSection adsSection = adsConfig.getConfigurationSection("ads");
        if (adsSection.getKeys(false).contains(advertise.getCategory())){
            ConfigurationSection categorySection = adsSection.getConfigurationSection(advertise.getCategory());
            if (categorySection != null){
                if (categorySection.getKeys(false).size() < SpearAds.getPlugin().getConfig().getInt("settings.max-ads-per-category")){
                    // ads.normal.slot.player
                    String path = "ads." + advertise.getCategory() + "." + advertise.getSlot() + ".";
                    adsConfig.set(path + "player", advertise.getPlayerName());
                    adsConfig.set(path + "ad-content", advertise.getAdContent());
                    adsConfig.set(path + "slot", advertise.getSlot());
                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDateTime = now.format(formatter);
                    adsConfig.set(path + "created-at", formattedDateTime);

                    try {
                        adsConfig.save(SpearAds.getConfigManager().getFile());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    player.sendMessage(TextUtils.getMessage("max-ads-reached"));
                }
            } else {
                player.sendMessage(TextUtils.getMessage("category-not-found"));
            }
        } else {
            player.sendMessage(TextUtils.getMessage("category-not-found"));
        }
    }

    public static String getRemainingAdTime(FileConfiguration config, Advertise advertise) {
        String path = "ads." + advertise.getCategory() + "." + advertise.getSlot() + ".created-at";
        String createdAtString = config.getString(path);

        if (createdAtString == null) {
            return TextUtils.getMessage("ad-not-found");
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime createdAt = LocalDateTime.parse(createdAtString, formatter);
            LocalDateTime now = LocalDateTime.now();

            Duration duration = Duration.between(createdAt, now);

            long hoursPassed = duration.toHours();
            long minutesPassed = duration.toMinutes();

            if (hoursPassed >= AD_DURATION_HOURS) {
                return "expired";
            }

            long minutesLeft = (AD_DURATION_HOURS * 60) - minutesPassed;
            long leftHours = minutesLeft / 60;
            long leftMinutes = minutesLeft % 60;

            return TextUtils.getMessage("remaining-ad-time").replace("%hours%", String.valueOf(leftHours)).replace("%minutes%", String.valueOf(leftMinutes));
        } catch (Exception e) {
            e.printStackTrace();
            return TextUtils.getMessage("ad-time-error");
        }
    }


    public static boolean hasAdvertisement(String category, String playerName) {
        ConfigurationSection adsSection = adsConfig.getConfigurationSection("ads." + category);
        if (adsSection != null) {
            for (String key : adsSection.getKeys(false)) {
                String player = adsSection.getString(key + ".player");
                if (player != null && player.equalsIgnoreCase(playerName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void removeAd(String category, int slot) {
        String path = "ads." + category + "." + slot;
        if (adsConfig.contains(path)) {
            adsConfig.set(path, null);
            try {
                adsConfig.save(SpearAds.getConfigManager().getFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Advertise getAd(String category, int slot) {
        String path = "ads." + category + "." + slot;
        if (adsConfig.contains(path)) {
            Advertise advertise = new Advertise();
            advertise.setCategory(category);
            advertise.setSlot(slot);
            advertise.setPlayerName(adsConfig.getString(path + ".player"));
            advertise.setAdContent(adsConfig.getString(path + ".ad-content"));
            return advertise;
        }
        return null;
    }

    public static void removeExpiredAdsAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(SpearAds.getPlugin(), () -> {
            FileConfiguration config = SpearAds.getConfigManager().getConfig();
            ConfigurationSection adsSection = config.getConfigurationSection("ads");
            if (adsSection == null) return;

            int removed = 0;
            for (String category : adsSection.getKeys(false)) {
                ConfigurationSection categorySection = adsSection.getConfigurationSection(category);
                if (categorySection == null) continue;
                List<String> toRemove = new ArrayList<>();
                for (String key : categorySection.getKeys(false)) {
                    String createdAt = categorySection.getString(key + ".created-at");
                    if (createdAt == null) continue;
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        LocalDateTime created = LocalDateTime.parse(createdAt, formatter);
                        long hours = Duration.between(created, LocalDateTime.now()).toHours();
                        if (hours >= AD_DURATION_HOURS) {
                            toRemove.add(key);
                        }
                    } catch (Exception ignored) {}
                }
                for (String key : toRemove) {
                    config.set("ads." + category + "." + key, null);
                    removed++;
                }
            }
            if (removed > 0) {
                try {
                    config.save(SpearAds.getConfigManager().getFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Bukkit.getLogger().info("[SIslandAd] " + removed + " expired ads removed.");
            }
        });
    }
}
