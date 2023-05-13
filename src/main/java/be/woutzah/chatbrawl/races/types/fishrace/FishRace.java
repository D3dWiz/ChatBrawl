package be.woutzah.chatbrawl.races.types.fishrace;

import be.woutzah.chatbrawl.contestants.ContestantsManager;
import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.ContestantRace;
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

public class FishRace extends ContestantRace {

    private final List<FishEntry> fishEntryList;
    private FishEntry fishEntry;

    public FishRace(RaceManager raceManager, SettingManager settingManager,
                    RewardManager rewardManager, TimeManager timeManager,
                    ContestantsManager contestantsManager, LeaderboardManager leaderboardManager) {
        super(RaceType.FISH, raceManager, settingManager, rewardManager, timeManager, contestantsManager, leaderboardManager);
        this.fishEntryList = new ArrayList<>();
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
            fishEntryList.add(new FishEntry(material, amount, rewardIds));
        });
    }

    public void initRandomFishEntry() {
        fishEntry = fishEntryList.get(random.nextInt(fishEntryList.size()));
    }

    @EventHandler
    public void checkFishedObjects(PlayerFishEvent e) {
        if (!isActive()) return;
        Player player = e.getPlayer();
        raceChecks(player);
        if (e.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
            Item caughtItem = (Item) e.getCaught();
            if (caughtItem == null) return;
            Material caughtMaterial = caughtItem.getItemStack().getType();
            if (caughtMaterial.equals(fishEntry.getMaterial())) {
                onWinning(player);
                contestantsManager.removeOnlinePlayers();
            }
        }
    }

    @Override
    public String replacePlaceholders(String message) {
        return message.replace("<fish>", fishEntry.getMaterial().toString().replace("_", " "))
                .replace("<amount>", String.valueOf(fishEntry.getAmount()));
    }

    @Override
    public void beforeRaceStart() {
        initRandomFishEntry();
        if (isAnnounceStartEnabled()) announceStart(isCenterMessages());
        if (isBossBarEnabled()) showBossBar();
        if (isActionBarEnabled()) showActionBar();
    }
}
