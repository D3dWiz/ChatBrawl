package be.woutzah.chatbrawl.commands.subcommands;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import be.woutzah.chatbrawl.settings.LanguageSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.util.Printer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;

public class StartCommand extends SubCommand {
    private final RaceManager raceManager;
    private final SettingManager settingManager;

    public StartCommand(ChatBrawl plugin) {
        super("start", "cb.start", new ArrayList<>(), true);
        this.raceManager = plugin.getRaceManager();
        this.settingManager = plugin.getSettingManager();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            Printer.sendParsedMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                    "You should insert a race", sender);
            return;
        }
        if (Arrays.stream(RaceType.values()).noneMatch(raceType -> raceType.name().equals(args[0].toUpperCase()))) {
            Printer.sendParsedMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                    "Invalid type of race.", sender);
            return;
        }
        ;

        if (raceManager.getCurrentRunningRace() != RaceType.NONE) {
            Printer.sendParsedMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                    settingManager.getString(LanguageSetting.RACE_STILL_RUNNING), sender);
            return;
        }
        raceManager.disableAutoCreation();
        String raceTypeString = args[0];
        RaceType raceType = RaceType.NONE;
        raceType = RaceType.valueOf(raceTypeString.toUpperCase());
        raceManager.startRace(raceType);

        Printer.sendParsedMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                settingManager.getString(LanguageSetting.STARTED_RACE)
                        .replace("<race>", raceType.toString().toLowerCase() + " race"), sender);
    }
}
