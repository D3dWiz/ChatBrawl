package be.woutzah.chatbrawl.commands.subcommands;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.Race;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import be.woutzah.chatbrawl.settings.LanguageSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.util.Printer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class StopCommand extends SubCommand {
    private final RaceManager raceManager;
    private final SettingManager settingManager;

    public StopCommand(ChatBrawl plugin) {
        super("stop", "cb.stop", new ArrayList<>(), true);
        this.raceManager = plugin.getRaceManager();
        this.settingManager = plugin.getSettingManager();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (raceManager.getCurrentRunningRace() == RaceType.NONE) {
            Printer.sendMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                    settingManager.getString(LanguageSetting.NO_RACE_RUNNING), sender);
            return;
        }
        Race race = raceManager.getCurrentRace();
        if (race == null) {
            Printer.sendMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                    settingManager.getString(LanguageSetting.NO_RACE_RUNNING), sender);
            return;
        }
        race.disable();
        raceManager.setCurrentRunningRace(RaceType.NONE);
        Printer.sendMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                settingManager.getString(LanguageSetting.STOPPED_RACE)
                        .replace("<race>", race.getType().toString().toLowerCase() + " race"), sender);
    }
}
