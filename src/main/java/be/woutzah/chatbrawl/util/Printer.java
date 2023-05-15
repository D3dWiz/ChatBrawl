package be.woutzah.chatbrawl.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Printer {
    private final static int CENTER_PX = 154;

    public static String parseColor(String message) {
        Component parsedMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(message);
        return LegacyComponentSerializer.legacySection().serialize(parsedMessage);
    }

    public static TextComponent parsedMessage(List<String> textList) {
        TextComponent.Builder message = Component.text();
        int lastIndex = textList.size() - 1;

        for (int i = 0; i < textList.size(); i++) {
            String entry = textList.get(i);

            message.append(Component.text(parseColor(entry)));
            if (!(i == lastIndex)) {
                message.appendNewline();
            }
        }
        return message.build();
    }

    public static String centerMessage(List<String> textList) {
        if (textList.isEmpty()) return "";
        TextComponent message = parsedMessage(textList);

        return message.content();
        // TODO fix this
//        StringBuilder message = new StringBuilder();
//        String[] lines = ChatColor.translateAlternateColorCodes('&', message.toString()).split("\n", 40);
//        StringBuilder resultSb = new StringBuilder();
//
//        for (String line : lines) {
//            int messagePxSize = 0;
//            boolean previousCode = false;
//            boolean isBold = false;
//
//            for (char c : line.toCharArray()) {
//                if (c == '§') {
//                    previousCode = true;
//                } else if (previousCode) {
//                    previousCode = false;
//                    isBold = c == 'l';
//                } else {
//                    DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
//                    messagePxSize = isBold ? messagePxSize + dFI.getBoldLength() : messagePxSize + dFI.getLength();
//                    messagePxSize++;
//                }
//            }
//            int toCompensate = CENTER_PX - messagePxSize / 2;
//            int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
//            int compensated = 0;
//            StringBuilder sb = new StringBuilder();
//            while (compensated < toCompensate) {
//                sb.append(" ");
//                compensated += spaceLength;
//            }
//            resultSb.append(sb).append(line).append("\n");
//        }
//        return resultSb.toString();
    }

    public static void sendParsedMessage(String message, Player player) {
        player.sendMessage(Component.text(parseColor(message)));
    }

    public static void sendParsedMessage(String message, CommandSender sender) {
        sender.sendMessage(Component.text(parseColor(message)));
    }

    public static void sendParsedMessage(List<String> textList, Player player) {
        if (textList.isEmpty()) return;
        TextComponent message = parsedMessage(textList);
        player.sendMessage(message);
    }

    public static void sendParsedMessage(List<String> textList, CommandSender sender) {
        if (textList.isEmpty()) return;
        TextComponent message = parsedMessage(textList);
        sender.sendMessage(message);
    }

    public static void printConsole(String text) {
        if (text.isEmpty()) return;
        Bukkit.getConsoleSender().sendMessage(Component.text(parseColor(text)));
    }

    public static void broadcast(String text) {
        if (text.isEmpty()) return;
        Bukkit.getServer().broadcast(Component.text(parseColor(text)), "cb.default");
    }

    public static void broadcast(List<String> textList) {
        TextComponent message = parsedMessage(textList);
        Bukkit.getServer().broadcast(message, "cb.default");
    }

    public static String stripColors(Component message) {
        return PlainTextComponentSerializer.plainText().serialize(message);
    }

    public static String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
