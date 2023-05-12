package be.woutzah.chatbrawl.util;

import be.woutzah.chatbrawl.ChatBrawl;

public class ErrorHandler {

    public static void error(String error){
        Printer.printConsole("&7[&6ChatBrawl Error&7]: "+ error);
        ChatBrawl plugin = ChatBrawl.getInstance();
        plugin.getPluginLoader().disablePlugin(plugin);
    }
}
