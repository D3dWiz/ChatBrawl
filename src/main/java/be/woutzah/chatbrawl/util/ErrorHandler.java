package be.woutzah.chatbrawl.util;

import be.woutzah.chatbrawl.ChatBrawl;
import org.bukkit.Bukkit;

public class ErrorHandler {

    public static void error(String error) {
        Printer.printConsole("&7[&6ChatBrawl Error&7]: " + error);
        ChatBrawl plugin = ChatBrawl.getInstance();
        Bukkit.getPluginManager().disablePlugin(plugin);
    }
}
