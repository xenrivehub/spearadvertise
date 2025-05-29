package com.spearforge.sIslandAd.listeners;

import com.spearforge.sIslandAd.SpearAds;
import com.spearforge.sIslandAd.gui.MainGUI;
import com.spearforge.sIslandAd.managers.AdManager;
import com.spearforge.sIslandAd.models.Advertise;
import com.spearforge.sIslandAd.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {


    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e){
        Player player = e.getPlayer();
        StringBuilder content = new StringBuilder();

        if (InventoryClickListener.getCreatingAd().containsKey(player.getUniqueId())){
            Advertise advertise = InventoryClickListener.getCreatingAd().get(e.getPlayer().getUniqueId());

            e.setCancelled(true);
            if (e.getMessage().equalsIgnoreCase(SpearAds.getPlugin().getConfig().getString("messages.cancel-word"))) {
                e.getPlayer().sendMessage(TextUtils.getMessage("ad-creation-cancelled"));
                InventoryClickListener.getCreatingAd().remove(e.getPlayer().getUniqueId());
                return;
            }
            for (String word : e.getMessage().split(" ")) {
                if (content.length() + word.length() + 1 <= SpearAds.getPlugin().getConfig().getInt("settings.max-length")) {
                    content.append(word).append(" ");
                } else {
                    player.sendMessage(TextUtils.getMessage("max-length-exceeded").replace("%max_length%", String.valueOf(SpearAds.getPlugin().getConfig().getInt("settings.max-length"))));
                    InventoryClickListener.getCreatingAd().remove(e.getPlayer().getUniqueId());
                    return;
                }
            }

            advertise.setAdContent(content.toString().trim());
            Bukkit.getScheduler().runTask(SpearAds.getPlugin(), () -> {
                AdManager.createAd(e.getPlayer(), advertise);
                InventoryClickListener.getCreatingAd().remove(player.getUniqueId());
                MainGUI.openInsideCategory(advertise.getCategory(), player);
            });

            if (SpearAds.getPlugin().getConfig().getBoolean("settings.announce")){
                Bukkit.getScheduler().runTaskAsynchronously(SpearAds.getPlugin(), () -> {
                    for (Player target : Bukkit.getOnlinePlayers()){
                        String message = TextUtils.getMessage("ad-announcement")
                                .replace("%player_name%", player.getName())
                                .replace("%category%", advertise.getCategory());
                        target.sendMessage(TextUtils.color(message));
                    }
                });
            }
        }

    }

}
