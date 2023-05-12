package be.woutzah.chatbrawl.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Printer {
    public static final Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");
    private final static int CENTER_PX = 154;

    public static void sendMessage(String message, Player player) {
        if (message.isEmpty()) return;
        player.sendMessage(parseColor(message));
    }

    public static void sendMessage(String message, CommandSender sender) {
        if (message.isEmpty()) return;
        sender.sendMessage(parseColor(message));
    }


    public static void sendMessage(List<String> textList, Player player) {
        if (textList.isEmpty()) return;
        StringBuilder sb = new StringBuilder();
        textList.forEach(entry -> sb.append(parseColor(entry)));
        player.sendMessage(parseColor(sb.toString()));
    }

    public static void sendMessage(List<String> textList, CommandSender sender) {
        if (textList.isEmpty()) return;
        StringBuilder sb = new StringBuilder();
        textList.forEach(entry -> sb.append(parseColor(entry)));
        sender.sendMessage(parseColor(sb.toString()));
    }

    public static void printConsole(String text) {
        if (text.isEmpty()) return;
        Bukkit.getConsoleSender().sendMessage(parseColor(text));
    }

    public static void broadcast(String text) {
        if (text.isEmpty()) return;
        Bukkit.broadcast(parseColor(text), "cb.default");
    }

    public static void broadcast(List<String> textList) {
        if (textList.isEmpty()) return;
        StringBuilder sb = new StringBuilder();
        textList.forEach(entry -> sb.append(parseColor(entry)));
        Bukkit.broadcast(parseColor(sb.toString()), "cb.default");
    }

    public static String centerMessage(List<String> textList) {
        if (textList.isEmpty()) return "";
        StringBuilder message = new StringBuilder();
        for (String entry : textList) {
            message.append(entry);
        }
        String[] lines = ChatColor.translateAlternateColorCodes('&', message.toString()).split("\n", 40);
        StringBuilder resultSb = new StringBuilder();

        for (String line : lines) {
            int messagePxSize = 0;
            boolean previousCode = false;
            boolean isBold = false;

            for (char c : line.toCharArray()) {
                if (c == '§') {
                    previousCode = true;
                } else if (previousCode) {
                    previousCode = false;
                    isBold = c == 'l';
                } else {
                    DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                    messagePxSize = isBold ? messagePxSize + dFI.getBoldLength() : messagePxSize + dFI.getLength();
                    messagePxSize++;
                }
            }
            int toCompensate = CENTER_PX - messagePxSize / 2;
            int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
            int compensated = 0;
            StringBuilder sb = new StringBuilder();
            while (compensated < toCompensate) {
                sb.append(" ");
                compensated += spaceLength;
            }
            resultSb.append(sb.toString()).append(line).append("\n");
        }
        return resultSb.toString();
    }

    public static String parseColor(String text) {
        return ChatColor.translateAlternateColorCodes('&', translateHexCodes(text)).replace("\\n", "\n");
    }

    public static String translateHexCodes(String textToTranslate) {
        Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of("#" + matcher.group(1)).toString());
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());

    }

    public static String stripColors(String message) {
        return ChatColor.stripColor(message);
    }

    public static String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
