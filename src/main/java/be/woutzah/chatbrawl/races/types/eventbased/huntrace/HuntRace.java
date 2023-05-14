package be.woutzah.chatbrawl.races.types.eventbased.huntrace;

import be.woutzah.chatbrawl.contestants.ContestantsManager;
import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.ContestantRace;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.HuntRaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.ErrorHandler;
import be.woutzah.chatbrawl.util.Printer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HuntRace extends ContestantRace {


    private final List<HuntEntry> huntEntryList;
    private HuntEntry huntEntry;

    public HuntRace(RaceManager raceManager, SettingManager settingManager,
                    RewardManager rewardManager, TimeManager timeManager,
                    ContestantsManager contestantsManager, LeaderboardManager leaderboardManager) {
        super(RaceType.HUNT, raceManager, settingManager, rewardManager, timeManager, contestantsManager, leaderboardManager);
        this.huntEntryList = new ArrayList<>();
        initHuntEntryList();
    }

    private void initHuntEntryList() {
        settingManager.getConfigSection(HuntRaceSetting.MOBS).getKeys(false).forEach(entry -> {
            EntityType entityType = null;
            try {
                entityType = EntityType.valueOf(settingManager.getString(ConfigType.HUNTRACE, "mobs." + entry + ".mob").toUpperCase());
            } catch (Exception exception) {
                ErrorHandler.error("huntrace: huntentry " + entry + " has an invalid entitytype!");
            }
            int amount = settingManager.getInt(ConfigType.HUNTRACE, "mobs." + entry + ".amount");
            List<Integer> rewardIds = settingManager.getIntegerList(ConfigType.HUNTRACE, "mobs." + entry + ".rewards");
            huntEntryList.add(new HuntEntry(entityType, amount, rewardIds));
        });
    }

    public void initRandomHuntEntry() {
        huntEntry = huntEntryList.get(random.nextInt(huntEntryList.size()));
    }

    @EventHandler
    public void checkMobsKilled(EntityDeathEvent e) {
        //do checks
        if (!isActive()) return;
        Player player = e.getEntity().getKiller();
        if (raceChecks(player)) return;
        EntityType killedEntityType = e.getEntity().getType();
        if (killedEntityType.equals(huntEntry.getEntityType())) {
            UUID uuid = player.getUniqueId();
            contestantsManager.addScore(uuid);
            if (contestantsManager.hasWon(uuid, huntEntry.getAmount())) {
                onWinning(player);
                contestantsManager.removeOnlinePlayers();
            }
        }
    }


    @Override
    public String replacePlaceholders(String message) {
        return message.replace("<mob>", Printer.capitalize(huntEntry.getEntityType().toString().toLowerCase().replace("_", " ")))
                .replace("<amount>", String.valueOf(huntEntry.getAmount()));
    }

    @Override
    public void beforeRaceStart() {
        initRandomHuntEntry();
        super.beforeRaceStart();
    }
}
