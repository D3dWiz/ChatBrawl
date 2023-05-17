package be.woutzah.chatbrawl.races.types.eventbased;

import be.woutzah.chatbrawl.contestants.ContestantsManager;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.Race;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.Printer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class EventRace extends Race {

    protected ContestantsManager contestantsManager;
    protected EventEntry eventEntry;
    protected List<EventEntry> eventEntryList = new ArrayList<>();

    public EventRace(RaceType type, RaceManager raceManager, SettingManager settingManager,
                     RewardManager rewardManager, TimeManager timeManager,
                     ContestantsManager contestantsManager, LeaderboardManager leaderboardManager) {
        super(type, raceManager, settingManager, rewardManager, timeManager, leaderboardManager);
        this.contestantsManager = contestantsManager;
    }

    @Override
    public void beforeRaceStart() {
        contestantsManager.fillOnlinePlayers();
        initRandomEventEntry();
        super.beforeRaceStart();
    }

    @Override
    public void afterRaceEnd() {
        super.afterRaceEnd();
        contestantsManager.removeOnlinePlayers();
    }

    @EventHandler
    public void addContestant(PlayerJoinEvent e) {
        if (isInactive()) return;
        contestantsManager.addContestant(e.getPlayer().getUniqueId());
    }

    public void initRandomEventEntry() {
        eventEntry = eventEntryList.get(random.nextInt(eventEntryList.size()));
    }

    @Override
    public String replacePlaceholders(String message) {
        String newMessage = message;
        if (message.contains("<mob>")) {
            newMessage = newMessage.replace("<mob>", Printer.capitalize(eventEntry.getEntityType().toString().toLowerCase().replace("_", " ")));
        }
        return newMessage
                .replace("<amount>", String.valueOf(eventEntry.getAmount()))
                .replace("<prefix>", settingManager.getString(GeneralSetting.PLUGIN_PREFIX))
                .replaceAll("<(block|item|fish|food)>", Printer.capitalize(eventEntry.getMaterial().toString().toLowerCase().replace("_", " ")));
    }
}
