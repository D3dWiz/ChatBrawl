package be.woutzah.chatbrawl.general;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.Race;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import be.woutzah.chatbrawl.settings.LanguageSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.util.Printer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class GeneralListener implements Listener {

    private final ChatBrawl plugin;
    private final RaceManager raceManager;
    private final SettingManager settingManager;

    public GeneralListener(ChatBrawl plugin) {
        this.plugin = plugin;
        this.raceManager = plugin.getRaceManager();
        this.settingManager = plugin.getSettingManager();
    }

    @EventHandler
    public void notifyOnLogin(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        Race race = raceManager.getCurrentRace();
        Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (race == null) {
                Printer.sendParsedMessage(settingManager.getString(GeneralSetting.PLUGIN_PREFIX) +
                        settingManager.getString(LanguageSetting.NO_RACE_RUNNING), player);
                return;
            }
            race.sendStart(player);
        }, 300);
    }
}
