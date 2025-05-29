package com.spearforge.sIslandAd.commands;

import com.spearforge.sIslandAd.SpearAds;
import com.spearforge.sIslandAd.gui.MainGUI;
import com.spearforge.sIslandAd.managers.AdManager;
import com.spearforge.sIslandAd.utils.TextUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {


        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(TextUtils.getMessage("no-console"));
            return true;
        }

        if (strings.length == 0){
            if (player.hasPermission("spearadvertise.open")){
                MainGUI.openMainGUI(player);
            } else {
                player.sendMessage(TextUtils.getMessage("no-permission"));
            }
        } else if (strings.length == 1 && strings [0].equalsIgnoreCase("reload")) {
            if (player.hasPermission("spearadvertise.reload")){
                player.sendMessage(TextUtils.getMessage("config-reloaded"));
                SpearAds.getPlugin().reloadConfig();
                SpearAds.getConfigManager().reload();
            } else {
                player.sendMessage(TextUtils.getMessage("no-permission"));
            }
        } else if (strings.length == 1 && strings[0].equalsIgnoreCase("remove")) {
            String category = strings[1];
            int slot = Integer.parseInt(strings[2]);
            // ads remove normal 11
            if (player.hasPermission("spearadvertise.remove")) {
                if (AdManager.getAd(category, slot) != null) {
                    AdManager.removeAd(category, slot);
                    player.sendMessage(TextUtils.getMessage("ad-removed"));
                } else {
                    player.sendMessage(TextUtils.getMessage("ad-not-found"));
                }
            } else {
                player.sendMessage(TextUtils.getMessage("no-permission"));
            }

        } else {
            player.sendMessage(TextUtils.getMessage("command-usage"));
        }


        return true;
    }
}
