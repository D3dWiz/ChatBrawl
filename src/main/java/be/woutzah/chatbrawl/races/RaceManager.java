package be.woutzah.chatbrawl.races;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.contestants.ContestantsManager;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.types.Race;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.races.types.chateventbased.ChatEntry;
import be.woutzah.chatbrawl.races.types.chateventbased.ScrambleRace;
import be.woutzah.chatbrawl.races.types.chateventbased.TypeRace;
import be.woutzah.chatbrawl.races.types.eventbased.*;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import be.woutzah.chatbrawl.settings.LanguageSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.ErrorHandler;
import be.woutzah.chatbrawl.util.Printer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;


public class RaceManager {

    EnumMap<RaceType, Race> raceMap;
    private ChatBrawl plugin;
    private SettingManager settingManager;
    private RewardManager rewardManager;
    private TimeManager timeManager;
    private ContestantsManager contestantsManager;
    private LeaderboardManager leaderboardManager;
    private int totalChance;
    private int raceDelay;
    private boolean autoCreationEnabled;
    private int minimumPlayers;
    private Random random;
    private BukkitTask raceCreationTask;
    private RaceType currentRunningRace = RaceType.NONE;
    private boolean isAutoCreating;

    public RaceManager(ChatBrawl plugin) {
        this.plugin = plugin;
        this.settingManager = plugin.getSettingManager();
        this.rewardManager = plugin.getRewardManager();
        this.timeManager = plugin.getTimeManager();
        this.contestantsManager = plugin.getContestantsManager();
        this.leaderboardManager = plugin.getLeaderboardManager();
        this.raceMap = new EnumMap<>(Map.of(
                RaceType.TYPE, new TypeRace(this, settingManager, rewardManager, timeManager, leaderboardManager),
                RaceType.BLOCK, new BlockRace(this, settingManager, rewardManager, timeManager, contestantsManager, leaderboardManager),
                RaceType.FISH, new FishRace(this, settingManager, rewardManager, timeManager, contestantsManager, leaderboardManager),
                RaceType.FOOD, new FoodRace(this, settingManager, rewardManager, timeManager, contestantsManager, leaderboardManager),
                RaceType.HUNT, new HuntRace(this, settingManager, rewardManager, timeManager, contestantsManager, leaderboardManager),
                RaceType.QUIZ, new ChatEntry.QuizRace(this, settingManager, rewardManager, timeManager, leaderboardManager),
                RaceType.CRAFT, new CraftRace(this, settingManager, rewardManager, timeManager, contestantsManager, leaderboardManager),
                RaceType.SCRAMBLE, new ScrambleRace(this, settingManager, rewardManager, timeManager, leaderboardManager)
        ));
        raceMap.forEach((r, k) -> this.totalChance += k.getChance());
        this.random = new Random();
        this.raceDelay = settingManager.getInt(GeneralSetting.RACE_DELAY) * 20;
        this.minimumPlayers = settingManager.getInt(GeneralSetting.MINIMUM_PLAYERS);
        this.autoCreationEnabled = settingManager.getBoolean(GeneralSetting.AUTO_CREATE_RACES);
        if (autoCreationEnabled) autoCreateRaces();
    }

    public void autoCreateRaces() {
        Printer.printConsole(settingManager.getString(LanguageSetting.STARTED_CREATING));
        isAutoCreating = true;
        raceCreationTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getServer().getOnlinePlayers().size() < minimumPlayers) return;
                Race race = null;
                RaceType type = null;
                boolean enabled = false;
                while (!enabled) {
                    type = getRandomRaceType();
                    race = raceMap.get(type);
                    enabled = race.isEnabled();
                }
                if (race.getDuration() > raceDelay) {
                    ErrorHandler.error(type.name().toLowerCase()
                            + "race's duration is longer than the event delay!");
                    cancel();
                    return;
                }
                currentRunningRace = race.getType();
                Printer.broadcast(currentRunningRace.toString());
                race.run(plugin);
            }
        }.runTaskTimer(plugin, 150, raceDelay);
    }

    private RaceType getRandomRaceType() {
        int index = random.nextInt(totalChance);
        int sum = 0;
        int i = 0;
        List<RaceType> types = Arrays.stream(RaceType.values())
                .filter(rt -> rt != RaceType.NONE)
                .toList();
        while (sum < index) {
            sum += raceMap.get(types.get(i++)).getChance();
        }
        return types.get(Math.max(0, i - 1));
    }

    public void registerEventHandlers() {
        raceMap.values().forEach(race -> plugin.getServer().getPluginManager()
                .registerEvents(race, plugin));
    }

    public void disableAutoCreation() {
        raceCreationTask.cancel();
        Race race = getCurrentRace();
        if (race != null) race.disable();
        isAutoCreating = false;
    }

    public void startRace(RaceType raceType) {
        try {
            currentRunningRace = raceType;
            raceMap.get(raceType).run(plugin);
        } catch (Exception e) {
            currentRunningRace = RaceType.NONE;
            e.printStackTrace();
        }
    }

    public boolean isCreativeAllowed() {
        return settingManager.getBoolean(GeneralSetting.ALLOW_CREATIVE);
    }

    public boolean isWorldAllowed(String worldName) {
        return !settingManager.getStringList(GeneralSetting.DISABLED_WORLDS)
                .contains(worldName);
    }

    public boolean startsWithForbiddenCommand(String message) {
        return settingManager.getStringList(GeneralSetting.IGNORED_COMMANDS)
                .stream()
                .anyMatch(ic -> message.toLowerCase()
                        .startsWith(ic.toLowerCase()));
    }

    public RaceType getCurrentRunningRace() {
        return currentRunningRace;
    }

    public void setCurrentRunningRace(RaceType currentRunningRace) {
        this.currentRunningRace = currentRunningRace;
    }

    public Race getRace(RaceType raceType) {
        return raceMap.get(raceType);
    }

    public Race getCurrentRace() {
        return raceMap.get(currentRunningRace);
    }

    public boolean isAutoCreating() {
        return isAutoCreating;
    }
}
