package be.woutzah.chatbrawl.races.types.foodrace;

import be.woutzah.chatbrawl.contestants.ContestantsManager;
import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.leaderboard.LeaderboardStatistic;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.ContestantRace;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.FoodRaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.ErrorHandler;
import be.woutzah.chatbrawl.util.FireWorkUtil;
import be.woutzah.chatbrawl.util.Printer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        //do checks
        if (!isActive()) return;
        Player player = e.getPlayer();
        if (!raceManager.isCreativeAllowed()) {
            if (player.getGameMode() == GameMode.CREATIVE) return;
        }
        World world = player.getWorld();
        if (!raceManager.isWorldAllowed(world.getName())) return;
        ItemStack consumedItemstack = e.getItem();
        if (consumedItemstack.getType().equals(foodEntry.getMaterial())) {
            UUID uuid = player.getUniqueId();
            contestantsManager.addScore(uuid);
            if (contestantsManager.hasWon(uuid, foodEntry.getAmount())) {
                //when correct
                afterRaceEnd();
                if (isAnnounceEndEnabled()) announceWinner(isCenterMessages(), player);
                if (isFireWorkEnabled()) FireWorkUtil.shootFireWorkSync(player);
                this.raceTask.cancel();
                rewardManager.executeRandomRewardSync(foodEntry.getRewardIds(), player);
                if (settingManager.getBoolean(GeneralSetting.MYSQL_ENABLED)) {
                    leaderboardManager.addWin(new LeaderboardStatistic(player.getUniqueId(), type, timeManager.getTotalSeconds()));
                }
                Printer.sendMessage(getWinnerPersonal(), player);
                contestantsManager.removeOnlinePlayers();
            }
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
