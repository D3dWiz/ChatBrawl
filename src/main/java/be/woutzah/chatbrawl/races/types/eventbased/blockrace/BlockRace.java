package be.woutzah.chatbrawl.races.types.eventbased.blockrace;

import be.woutzah.chatbrawl.contestants.ContestantsManager;
import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.ContestantRace;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.BlockRaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.ErrorHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlockRace extends ContestantRace {

    private final List<BlockEntry> blockEntryList;
    private BlockEntry blockEntry;

    public BlockRace(RaceManager raceManager, SettingManager settingManager,
                     RewardManager rewardManager, TimeManager timeManager,
                     ContestantsManager contestantsManager, LeaderboardManager leaderboardManager) {
        super(RaceType.BLOCK, raceManager, settingManager, rewardManager, timeManager, contestantsManager, leaderboardManager);
        this.blockEntryList = new ArrayList<>();
        initBlockEntryList();
    }

    private void initBlockEntryList() {
        settingManager.getConfigSection(BlockRaceSetting.BLOCKS).getKeys(false).forEach(entry -> {
            Material material = null;
            try {
                material = Material.valueOf(settingManager.getString(ConfigType.BLOCKRACE, "blocks." + entry + ".block").toUpperCase());
            } catch (Exception exception) {
                ErrorHandler.error("blockrace: blockentry " + entry + " has an invalid material!");
            }
            int amount = settingManager.getInt(ConfigType.BLOCKRACE, "blocks." + entry + ".amount");
            List<Integer> rewardIds = settingManager.getIntegerList(ConfigType.BLOCKRACE, "blocks." + entry + ".rewards");
            blockEntryList.add(new BlockEntry(material, amount, rewardIds));
        });
    }

    public void initRandomBlockEntry() {
        blockEntry = blockEntryList.get(random.nextInt(blockEntryList.size()));
    }


    @EventHandler
    public void checkBlocksMined(BlockBreakEvent e) {
        if (!isActive()) return;
        Player player = e.getPlayer();
        raceChecks(player);
        Block minedBlock = e.getBlock();
        if (minedBlock.getType().equals(blockEntry.getMaterial())) {
            UUID uuid = player.getUniqueId();
            contestantsManager.addScore(uuid);
            if (contestantsManager.hasWon(uuid, blockEntry.getAmount())) {
                onWinning(player);
            }
        }
    }

    @Override
    public String replacePlaceholders(String message) {
        return message
                .replace("<block>", blockEntry.getMaterial().toString().toLowerCase().replace("_", " "))
                .replace("<amount>", String.valueOf(blockEntry.getAmount()))
                .replace("<prefix>", settingManager.getString(GeneralSetting.PLUGIN_PREFIX));
    }

    @Override
    public void beforeRaceStart() {
        initRandomBlockEntry();
        super.beforeRaceStart();
    }
}
