package be.woutzah.chatbrawl.races.types.eventbased;

import be.woutzah.chatbrawl.contestants.ContestantsManager;
import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.FishRaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.ErrorHandler;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.ArrayList;
import java.util.List;

public class FishRace extends EventRace {
    private EventEntry eventEntry;

    public FishRace(RaceManager raceManager, SettingManager settingManager,
                    RewardManager rewardManager, TimeManager timeManager,
                    ContestantsManager contestantsManager, LeaderboardManager leaderboardManager) {
        super(RaceType.FISH, raceManager, settingManager, rewardManager, timeManager, contestantsManager, leaderboardManager);
        this.eventEntryList = new ArrayList<>();
        initFishEntryList();
    }

    private void initFishEntryList() {
        settingManager.getConfigSection(FishRaceSetting.FISH).getKeys(false).forEach(entry -> {
            Material material = null;
            try {
                material = Material.valueOf(settingManager.getString(ConfigType.FISHRACE, "fish." + entry + ".catch").toUpperCase());
            } catch (Exception exception) {
                ErrorHandler.error("fishrace: catchentry " + entry + " has an invalid material!");
            }
            int amount = settingManager.getInt(ConfigType.FISHRACE, "fish." + entry + ".amount");
            List<Integer> rewardIds = settingManager.getIntegerList(ConfigType.FISHRACE, "fish." + entry + ".rewards");
            eventEntryList.add(new EventEntry<>(material, rewardIds, amount));
        });
    }

    @EventHandler
    public void checkFishedObjects(PlayerFishEvent e) {
        if (isInactive()) return;
        Player player = e.getPlayer();
        if (raceChecks(player)) return;
        if (e.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
            Item caughtItem = (Item) e.getCaught();
            if (caughtItem == null) return;
            Material caughtMaterial = caughtItem.getItemStack().getType();
            if (eventEntryList.stream().anyMatch(s -> s.getMaterial().equals(caughtMaterial))) {
                onWinning(player);
                contestantsManager.removeOnlinePlayers();
            }
        }
    }
}
