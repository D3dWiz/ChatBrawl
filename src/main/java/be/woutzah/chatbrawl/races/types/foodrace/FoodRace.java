package be.woutzah.chatbrawl.races.types.foodrace;

import be.woutzah.chatbrawl.contestants.ContestantsManager;
import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.ContestantRace;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.FoodRaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.ErrorHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class FoodRace extends ContestantRace {
    private final List<FoodEntry> foodEntryList;
    private FoodEntry foodEntry;

    public FoodRace(RaceManager raceManager, SettingManager settingManager,
                    RewardManager rewardManager, TimeManager timeManager,
                    ContestantsManager contestantsManager, LeaderboardManager leaderboardManager) {
        super(RaceType.FOOD, raceManager, settingManager, rewardManager, timeManager, contestantsManager, leaderboardManager);
        this.foodEntryList = new ArrayList<>();
        initFoodEntryList();
    }

    private void initFoodEntryList() {
        settingManager.getConfigSection(FoodRaceSetting.FOOD).getKeys(false).forEach(entry -> {
            Material material = null;
            try {
                material = Material.valueOf(settingManager.getString(ConfigType.FOODRACE, "food." + entry + ".food").toUpperCase());
            } catch (Exception exception) {
                ErrorHandler.error("foodrace: foodentry " + entry + " has an invalid material!");
            }
            int amount = settingManager.getInt(ConfigType.FOODRACE, "food." + entry + ".amount");
            List<Integer> rewardIds = settingManager.getIntegerList(ConfigType.FOODRACE, "food." + entry + ".rewards");
            foodEntryList.add(new FoodEntry(material, amount, rewardIds));
        });
    }

    public void initRandomFoodEntry() {
        foodEntry = foodEntryList.get(random.nextInt(foodEntryList.size()));
    }

    @EventHandler
    public void onFoodConsume(PlayerItemConsumeEvent e) {
        if (!isActive()) return;
        Player player = e.getPlayer();
        raceChecks(player);
        ItemStack consumedItemstack = e.getItem();
        if (consumedItemstack.getType().equals(foodEntry.getMaterial())) {
            onWinning(player);
            contestantsManager.removeOnlinePlayers();
        }
    }

    @Override
    public String replacePlaceholders(String message) {
        return message.replace("<food>", foodEntry.getMaterial().toString().toLowerCase().replace("_", " "))
                .replace("<amount>", String.valueOf(foodEntry.getAmount()));
    }

    @Override
    public void beforeRaceStart() {
        super.beforeRaceStart();
        initRandomFoodEntry();
        if (isAnnounceStartEnabled()) announceStart(isCenterMessages());
        if (isBossBarEnabled()) showBossBar();
        if (isActionBarEnabled()) showActionBar();
    }
}
