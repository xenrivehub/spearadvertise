package com.spearforge.sIslandAd.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){

        InventoryClickListener.getCreatingAd().remove(e.getPlayer().getUniqueId());
    }

}
