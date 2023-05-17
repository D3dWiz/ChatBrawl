package be.woutzah.chatbrawl.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Printer {
    public static Component parseColor(String message) {
        if (message.isEmpty()) return Component.text("");
        var mm = MiniMessage.miniMessage();
        return mm.deserialize(message);
    }

    public static TextComponent parsedMessage(List<String> textList) {
        if (textList.isEmpty()) return Component.text("");
        TextComponent.Builder message = Component.text();
        int lastIndex = textList.size() - 1;

        for (int i = 0; i < textList.size(); i++) {
            String entry = textList.get(i);

            message.append(parseColor(entry));
            if (!(i == lastIndex)) {
                message.appendNewline();
            }
        }
        return message.build();
    }

    public static void sendMultilineParsedMessage(List<String> message, Player player) {
        if (message.isEmpty()) return;
        TextComponent output = parsedMessage(message);
        player.sendMessage(output);
    }

    public static void sendMultilineParsedMessage(List<String> message, CommandSender sender) {
        if (message.isEmpty()) return;
        TextComponent output = parsedMessage(message);
        sender.sendMessage(output);
    }

    public static void sendParsedMessage(String message, Player player) {
        if (message.isEmpty()) return;
        TextComponent output = parsedMessage(Collections.singletonList(message));
        player.sendMessage(output);
    }

    public static void sendParsedMessage(String message, CommandSender sender) {
        if (message.isEmpty()) return;
        TextComponent output = parsedMessage(Collections.singletonList(message));
        sender.sendMessage(output);
    }

    public static void printConsole(String text) {
        if (text.isEmpty()) return;
        Bukkit.getConsoleSender().sendMessage(parseColor(text));
    }

    public static void broadcast(String text) {
        if (text.isEmpty()) return;
        Bukkit.getServer().broadcast(parseColor(text), "cb.default");
    }

    public static void broadcast(List<String> textList) {
        TextComponent message = parsedMessage(textList);
        Bukkit.getServer().broadcast(message, "cb.default");
    }

    public static void broadcast(TextComponent text) {
        Bukkit.getServer().broadcast(text, "cb.default");
    }

    public static String stripColors(Component message) {
        return PlainTextComponentSerializer.plainText().serialize(message);
    }

    public static String capitalize(String text) {
        StringBuilder sb = new StringBuilder();
        String[] words = text.split(" ");
        for (String word : words) {
            sb.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
            sb.append(" ");
        }
        return sb.toString();
    }
}
