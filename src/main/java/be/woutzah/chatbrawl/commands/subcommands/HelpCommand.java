package be.woutzah.chatbrawl.commands.subcommands;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.settings.LanguageSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.util.Printer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class HelpCommand extends SubCommand {
    private final SettingManager settingManager;

    public HelpCommand(ChatBrawl plugin) {
        super("help", "none", new ArrayList<>(), true);
        settingManager = plugin.getSettingManager();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Printer.sendParsedMessage(settingManager.getStringList(LanguageSetting.HELPMENU), sender);
    }
}
