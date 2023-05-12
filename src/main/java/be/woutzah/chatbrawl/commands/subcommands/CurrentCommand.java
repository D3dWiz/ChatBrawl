package be.woutzah.chatbrawl.commands.subcommands;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.Race;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import be.woutzah.chatbrawl.settings.LanguageSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.util.Printer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CurrentCommand extends SubCommand {

    private final SettingManager settingManager;
    private final RaceManager raceManager;

    public CurrentCommand(ChatBrawl plugin) {
        super("current", "cb.current", new ArrayList<>(), false);
        this.settingManager = plugin.getSettingManager();
        this.raceManager = plugin.getRaceManager();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Race race = raceManager.getCurrentRace();
        if (race == null) {
            Printer.sendMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                    settingManager.getString(LanguageSetting.NO_RACE_RUNNING), player);
            return;
        }
        race.sendStart(player);
    }
}
