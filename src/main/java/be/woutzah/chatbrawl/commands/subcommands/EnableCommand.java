package be.woutzah.chatbrawl.commands.subcommands;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import be.woutzah.chatbrawl.settings.LanguageSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.util.Printer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class EnableCommand extends SubCommand {
    private final RaceManager raceManager;
    private final SettingManager settingManager;

    public EnableCommand(ChatBrawl plugin) {
        super("enable", "cb.enable", new ArrayList<>(), true);
        this.raceManager = plugin.getRaceManager();
        this.settingManager = plugin.getSettingManager();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (raceManager.isAutoCreating()) {
            Printer.sendMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                    settingManager.getString(LanguageSetting.ALREADY_ENABLED), sender);
            return;
        }
        raceManager.autoCreateRaces();
        Printer.sendMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                settingManager.getString(LanguageSetting.ENABLED), sender);
    }
}
