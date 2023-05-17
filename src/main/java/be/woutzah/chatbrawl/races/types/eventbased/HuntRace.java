package be.woutzah.chatbrawl.races.types.eventbased;

import be.woutzah.chatbrawl.contestants.ContestantsManager;
import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.HuntRaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.ErrorHandler;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;
import java.util.UUID;

public class HuntRace extends EventRace {

    public HuntRace(RaceManager raceManager, SettingManager settingManager,
                    RewardManager rewardManager, TimeManager timeManager,
                    ContestantsManager contestantsManager, LeaderboardManager leaderboardManager) {
        super(RaceType.HUNT, raceManager, settingManager, rewardManager, timeManager, contestantsManager, leaderboardManager);
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
            eventEntryList.add(new EventEntry<>(entityType, rewardIds, amount));
        });
    }

    @EventHandler
    public void checkMobsKilled(EntityDeathEvent e) {
        if (isInactive()) return;
        Player player = e.getEntity().getKiller();
        if (raceChecks(player)) return;
        EntityType killedEntityType = e.getEntity().getType();
        if (eventEntryList.stream().anyMatch(s -> s.getEntityType().equals(killedEntityType))) {
            UUID uuid = player.getUniqueId();
            contestantsManager.addScore(uuid);
            if (contestantsManager.hasWon(uuid, eventEntry.getAmount())) {
                onWinning(player);
                contestantsManager.removeOnlinePlayers();
            }
        }
    }
}
