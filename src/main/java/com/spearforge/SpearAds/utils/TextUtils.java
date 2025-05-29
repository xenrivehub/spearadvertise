package com.spearforge.sIslandAd.utils;

import com.spearforge.sIslandAd.SpearAds;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {

    public static String color(String message) {
        if (message == null) return "";

        message = message.replaceAll("(?i)(?<!&)#([A-Fa-f0-9]{6})", "&#$1");

        Pattern hexPattern = Pattern.compile("(?i)&#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            StringBuilder minecraftColor = new StringBuilder("§x");

            for (char c : hexCode.toCharArray()) {
                minecraftColor.append("§").append(c);
            }

            matcher.appendReplacement(buffer, minecraftColor.toString());
        }
        matcher.appendTail(buffer);
        message = buffer.toString();

        message = ChatColor.translateAlternateColorCodes('&', message);

        return message;
    }

    public static String getMessage(String path){
        String message = SpearAds.getPlugin().getConfig().getString("messages." + path);
        if (message != null){
            return color(message).replace("%prefix%", color(SpearAds.getPlugin().getConfig().getString("prefix")));
        } else {
            return "Message not found for path: " + path;
        }
    }
}
