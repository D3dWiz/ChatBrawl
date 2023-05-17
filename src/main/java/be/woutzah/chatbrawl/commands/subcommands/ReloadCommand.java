package be.woutzah.chatbrawl.commands.subcommands;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.util.Printer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;

public class ReloadCommand extends SubCommand {

    private final RaceManager raceManager;
    private final ChatBrawl plugin;
    private final SettingManager settingManager;

    public ReloadCommand(ChatBrawl plugin) {
        super("reload", "cb.reload", new ArrayList<>(), true);
        this.raceManager = plugin.getRaceManager();
        this.settingManager = plugin.getSettingManager();
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        raceManager.disableAutoCreation();
        HandlerList.unregisterAll(plugin);
        plugin.init();
        Printer.sendParsedMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                "<yellow>ChatBrawl V" + plugin.getPluginMeta().getVersion() + " <white>has been <red>areloaded<white>!", sender);
    }
}
