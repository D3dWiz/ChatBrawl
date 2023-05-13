package be.woutzah.chatbrawl.races.types.blockrace;

import be.woutzah.chatbrawl.contestants.ContestantsManager;
import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.leaderboard.LeaderboardStatistic;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.ContestantRace;
import be.woutzah.chatbrawl.races.types.RaceEntry;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.BlockRaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.ErrorHandler;
import be.woutzah.chatbrawl.util.FireWorkUtil;
import be.woutzah.chatbrawl.util.Printer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
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
        //do checks
        if (!isActive()) return;
        Player player = e.getPlayer();
        if (!raceManager.isCreativeAllowed()) {
            if (player.getGameMode() == GameMode.CREATIVE) return;
        }
        World world = player.getWorld();
        if (!raceManager.isWorldAllowed(world.getName())) return;
        Block minedBlock = e.getBlock();
        if (minedBlock.getType().equals(blockEntry.getMaterial())) {
            UUID uuid = player.getUniqueId();
            contestantsManager.addScore(uuid);
            if (contestantsManager.hasWon(uuid, blockEntry.getAmount())) {
                //when correct
                afterRaceEnd();
                if (isAnnounceEndEnabled()) announceWinner(isCenterMessages(), player);
                if (isFireWorkEnabled()) FireWorkUtil.shootFireWorkSync(player);
                this.raceTask.cancel();
                rewardManager.executeRandomRewardSync(RaceEntry.getRewardIds(), player);
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
        return message
                .replace("<block>", blockEntry.getMaterial().toString().toLowerCase().replace("_", " "))
                .replace("<amount>", String.valueOf(blockEntry.getAmount()))
                .replace("<prefix>", settingManager.getString(GeneralSetting.PLUGIN_PREFIX));
    }

    @Override
    public void beforeRaceStart() {
        initRandomBlockEntry();
        if (isAnnounceStartEnabled()) announceStart(isCenterMessages());
        if (isBossBarEnabled()) showBossBar();
        if (isActionBarEnabled()) showActionBar();
    }
}
