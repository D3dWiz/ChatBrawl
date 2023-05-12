package be.woutzah.chatbrawl;

import be.woutzah.chatbrawl.commands.CommandManager;
import be.woutzah.chatbrawl.contestants.ContestantsManager;
import be.woutzah.chatbrawl.database.DatabaseManager;
import be.woutzah.chatbrawl.files.ConfigManager;
import be.woutzah.chatbrawl.general.GeneralListener;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.placeholders.PlaceholderManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatBrawl extends JavaPlugin {

    private static ChatBrawl instance;
    private ConfigManager configManager;
    private SettingManager settingManager;
    private RaceManager raceManager;
    private RewardManager rewardManager;
    private TimeManager timeManager;
    private ContestantsManager contestantsManager;
    private DatabaseManager databaseManager;
    private LeaderboardManager leaderboardManager;
    private static boolean langUtilsIsEnabled;

    @Override
    public void onEnable() {
        instance = this;
        init();
        if (Bukkit.getPluginManager().getPlugin("LangUtils") != null) {
            langUtilsIsEnabled = true;
        }
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new PlaceholderManager(this).register();
        }
        enableMetrics();
    }

    private void enableMetrics() {
        int pluginId = 10423;
        Metrics metrics = new Metrics(this, pluginId);
    }

    public void init(){
        this.configManager = new ConfigManager(this);
        this.settingManager = new SettingManager(this);
        if (settingManager.getBoolean(GeneralSetting.MYSQL_ENABLED)) {
            this.databaseManager = new DatabaseManager(this);
            this.leaderboardManager = new LeaderboardManager(this);
        }
        this.rewardManager = new RewardManager(this);
        this.timeManager = new TimeManager(this);
        this.contestantsManager = new ContestantsManager();
        this.raceManager = new RaceManager(this);
        new CommandManager(this).setup();
        raceManager.registerEventHandlers();
        if (settingManager.getBoolean(GeneralSetting.NOTIFY_ON_LOGIN)) {
            getServer().getPluginManager().registerEvents(new GeneralListener(this), this);
        }
    }

    @Override
    public void onDisable() {

    }

    public RaceManager getRaceManager() {
        return raceManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public SettingManager getSettingManager() {
        return settingManager;
    }

    public TimeManager getTimeManager() {
        return timeManager;
    }

    public ContestantsManager getContestantsManager() {
        return contestantsManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }

    public static ChatBrawl getInstance() {
        return instance;
    }

    public static boolean isLangUtilsIsEnabled() {
        return langUtilsIsEnabled;
    }
}
