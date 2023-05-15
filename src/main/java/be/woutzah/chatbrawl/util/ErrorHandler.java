package be.woutzah.chatbrawl.util;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import org.bukkit.Bukkit;

public class ErrorHandler {

    public static void error(String error) {
        Printer.printConsole(GeneralSetting.PLUGIN_PREFIX + error);
        ChatBrawl plugin = ChatBrawl.getInstance();
        Bukkit.getPluginManager().disablePlugin(plugin);
    }
}
