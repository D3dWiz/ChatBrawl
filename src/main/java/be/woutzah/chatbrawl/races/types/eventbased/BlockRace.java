package be.woutzah.chatbrawl.races.types.eventbased;

import be.woutzah.chatbrawl.contestants.ContestantsManager;
import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.BlockRaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.ErrorHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;
import java.util.UUID;

public class BlockRace extends EventRace {
    public BlockRace(RaceManager raceManager, SettingManager settingManager,
                     RewardManager rewardManager, TimeManager timeManager,
                     ContestantsManager contestantsManager, LeaderboardManager leaderboardManager) {
        super(RaceType.BLOCK, raceManager, settingManager, rewardManager, timeManager, contestantsManager, leaderboardManager);
        initEventEntryList();
    }

    private void initEventEntryList() {
        settingManager.getConfigSection(BlockRaceSetting.BLOCKS).getKeys(false).forEach(entry -> {
            Material material = null;
            try {
                material = Material.valueOf(settingManager.getString(ConfigType.BLOCKRACE, "blocks." + entry + ".block").toUpperCase());
            } catch (Exception exception) {
                ErrorHandler.error("blockrace: blockentry " + entry + " has an invalid material!");
            }
            int amount = settingManager.getInt(ConfigType.BLOCKRACE, "blocks." + entry + ".amount");
            List<Integer> rewardIds = settingManager.getIntegerList(ConfigType.BLOCKRACE, "blocks." + entry + ".rewards");
            eventEntryList.add(new EventEntry<>(material, rewardIds, amount));
        });
    }

    @EventHandler
    public void checkBlocksMined(BlockBreakEvent e) {
        if (isInactive()) return;
        Player player = e.getPlayer();
        if (raceChecks(player)) return;
        Block minedBlock = e.getBlock();
        if (eventEntryList.stream().anyMatch(s -> s.getMaterial().equals(minedBlock.getType()))) {
            UUID uuid = player.getUniqueId();
            contestantsManager.addScore(uuid);
            if (contestantsManager.hasWon(uuid, eventEntry.getAmount())) {
                onWinning(player);
                contestantsManager.removeOnlinePlayers();
            }
        }
    }
}
