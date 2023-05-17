package be.woutzah.chatbrawl.races.types.eventbased;

import be.woutzah.chatbrawl.contestants.ContestantsManager;
import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
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

import java.util.List;

public class FoodRace extends EventRace {

    public FoodRace(RaceManager raceManager, SettingManager settingManager,
                    RewardManager rewardManager, TimeManager timeManager,
                    ContestantsManager contestantsManager, LeaderboardManager leaderboardManager) {
        super(RaceType.FOOD, raceManager, settingManager, rewardManager, timeManager, contestantsManager, leaderboardManager);
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
            eventEntryList.add(new EventEntry<>(material, rewardIds, amount));
        });
    }

    @EventHandler
    public void onFoodConsume(PlayerItemConsumeEvent e) {
        if (isInactive()) return;
        Player player = e.getPlayer();
        if (raceChecks(player)) return;
        ItemStack consumedItemstack = e.getItem();
        if (eventEntryList.stream().anyMatch(s -> s.getMaterial().equals(consumedItemstack.getType()))) {
            onWinning(player);
            contestantsManager.removeOnlinePlayers();
        }
    }
}
