package be.woutzah.chatbrawl.races.types.blockrace;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.contestants.ContestantsManager;
import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.leaderboard.LeaderboardStatistic;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.ContestantRace;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import be.woutzah.chatbrawl.settings.LanguageSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.BlockRaceSetting;
import be.woutzah.chatbrawl.settings.races.RaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.ErrorHandler;
import be.woutzah.chatbrawl.util.FireWorkUtil;
import be.woutzah.chatbrawl.util.Printer;
import com.meowj.langutils.lang.LanguageHelper;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Override
    public void announceStart(boolean center) {
        List<String> messageList = settingManager.getStringList(RaceType.BLOCK, RaceSetting.LANGUAGE_START)
                .stream()
                .map(this::replacePlaceholders)
                .collect(Collectors.toList());
        if (center) {
            Printer.broadcast(Printer.centerMessage(messageList));
            return;
        }
        Printer.broadcast(messageList);
    }

    @Override
    public void sendStart(Player player) {
        List<String> messageList = settingManager.getStringList(RaceType.BLOCK, RaceSetting.LANGUAGE_START)
                .stream()
                .map(this::replacePlaceholders)
                .collect(Collectors.toList());
        if (isCenterMessages()) {
            Printer.sendMessage(Printer.centerMessage(messageList), player);
            return;
        }
        Printer.sendMessage(messageList, player);
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
                rewardManager.executeRandomRewardSync(blockEntry.getRewardIds(), player);
                if (settingManager.getBoolean(GeneralSetting.MYSQL_ENABLED)) {
                    leaderboardManager.addWin(new LeaderboardStatistic(player.getUniqueId(), type, timeManager.getTotalSeconds()));
                }
                Printer.sendMessage(getWinnerPersonal(), player);
                contestantsManager.removeOnlinePlayers();
            }
        }

    }


    @Override
    public void announceWinner(boolean center, Player player) {
        List<String> messageList = settingManager.getStringList(RaceType.BLOCK, RaceSetting.LANGUAGE_WINNER)
                .stream()
                .map(this::replacePlaceholders)
                .map(s -> s.replace("<displayname>", player.displayName().toString()))
                .map(s -> s.replace("<player>", player.getName()))
                .map(s -> s.replace("<time>", timeManager.getTimeString()))
                .collect(Collectors.toList());
        if (center) {
            Printer.broadcast(Printer.centerMessage(messageList));
            return;
        }
        Printer.broadcast(messageList);
    }

    @Override
    public void showBossBar() {
        Component startMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(replacePlaceholders(settingManager.getString(RaceType.BLOCK, RaceSetting.LANGUAGE_BOSSBAR))
                .replace("<timeLeft>", String.valueOf(timeManager.formatTime(raceManager.getRace(RaceType.BLOCK).getDurationSeconds()))));
        final BossBar bossBar = BossBar.bossBar(startMessage, 1.0f, BossBar.Color.valueOf(settingManager.getString(RaceType.BLOCK, RaceSetting.BOSSBAR_COLOR)), BossBar.Overlay.valueOf(settingManager.getString(RaceType.BLOCK, RaceSetting.BOSSBAR_STYLE)));
        this.activeBossBar = bossBar;
        this.bossBarTask = new BukkitRunnable() {
            @Override
            public void run() {
                int remainingTime = timeManager.getRemainingTime(RaceType.BLOCK);
                float remainingTimePercent = ((float) timeManager.getRemainingTime(RaceType.BLOCK) / getDurationSeconds());
                Component message = LegacyComponentSerializer.legacyAmpersand().deserialize(replacePlaceholders(settingManager.getString(RaceType.BLOCK, RaceSetting.LANGUAGE_BOSSBAR))
                        .replace("<timeLeft>", String.valueOf(timeManager.formatTime(remainingTime))));
                bossBar.name(message);
                bossBar.progress(remainingTimePercent);
                Bukkit.getServer().showBossBar(bossBar);
            }
        }.runTaskTimer(ChatBrawl.getInstance(), 0, 20);
    }


    @Override
    public void showActionBar() {
        Component message = LegacyComponentSerializer.legacyAmpersand().deserialize(replacePlaceholders(settingManager.getString(RaceType.BLOCK, RaceSetting.LANGUAGE_ACTIONBAR)
                .replace("<timeLeft>", String.valueOf(timeManager.formatTime(timeManager.getRemainingTime(RaceType.BLOCK))))));
        this.actionBarTask = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().sendActionBar(message);
            }
        }.runTaskTimer(ChatBrawl.getInstance(), 0, 20);
    }

    @Override
    public String replacePlaceholders(String message) {
        return message
                .replace("<block>", ChatBrawl.isLangUtilsIsEnabled() ?
                        LanguageHelper.getItemName(new ItemStack(blockEntry.getMaterial()), settingManager.getString(LanguageSetting.LANG))
                        : blockEntry.getMaterial().toString().toLowerCase().replace("_", " "))
                .replace("<amount>", String.valueOf(blockEntry.getAmount()))
                .replace("<prefix>", settingManager.getString(GeneralSetting.PLUGIN_PREFIX));
    }

    @Override
    public void beforeRaceStart() {
        super.beforeRaceStart();
        initRandomBlockEntry();
        if (isAnnounceStartEnabled()) announceStart(isCenterMessages());
        if (isBossBarEnabled()) showBossBar();
        if (isActionBarEnabled()) showActionBar();
    }
}
