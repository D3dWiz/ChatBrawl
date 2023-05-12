package be.woutzah.chatbrawl.races.types;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.RaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.Printer;
import be.woutzah.chatbrawl.util.SchedulerUtil;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.util.Random;

public abstract class Race implements Raceable, Announceable, Listener {

    protected final Random random;
    protected final Sound endSound;
    protected final RaceType type;
    private final Sound beginSound;
    private final int chance;
    public final int duration;
    protected LeaderboardManager leaderboardManager;
    protected RaceManager raceManager;
    protected SettingManager settingManager;
    protected RewardManager rewardManager;
    protected TimeManager timeManager;
    protected BukkitTask raceTask;
    protected @Nullable BossBar activeBossBar;
    protected BukkitTask actionBarTask;
    protected BukkitTask bossBarTask;
    private boolean isActive;

    public Race(RaceType type, RaceManager raceManager, SettingManager settingManager,
                RewardManager rewardManager, TimeManager timeManager,
                LeaderboardManager leaderboardManager) {
        this.random = new Random();
        this.type = type;
        this.raceManager = raceManager;
        this.rewardManager = rewardManager;
        this.settingManager = settingManager;
        this.timeManager = timeManager;
        this.leaderboardManager = leaderboardManager;
        this.chance = settingManager.getInt(type, RaceSetting.CHANCE);
        this.duration = settingManager.getInt(type, RaceSetting.DURATION) * 20;
        this.beginSound = Sound.valueOf(settingManager.getString(type, RaceSetting.BEGINSOUND));
        this.endSound = Sound.valueOf(settingManager.getString(type, RaceSetting.ENDSOUND));
    }

    protected void playSound(Sound sound) {
        Bukkit.getOnlinePlayers()
                .forEach(p -> p.playSound(p.getLocation(), sound, 1.0F, 8.0F));
    }

    @Override
    public void stopBossBar() {
        bossBarTask.cancel();
        Bukkit.getServer().hideBossBar(this.activeBossBar);
        this.activeBossBar = null;
    }

    @Override
    public void stopActionBar() {
        actionBarTask.cancel();
    }

    @Override
    public void announceEnd() {
        Printer.broadcast(settingManager.getStringList(type, RaceSetting.LANGUAGE_ENDED));
    }

    @Override
    public void afterRaceEnd() {
        timeManager.stopTimer();
        if (isSoundEnabled()) playSound(endSound);
        if (isBossBarEnabled()) stopBossBar();
        if (isActionBarEnabled()) stopActionBar();
        raceManager.setCurrentRunningRace(RaceType.NONE);
        isActive = false;
    }

    public void run(ChatBrawl plugin) {
        isActive = true;
        beforeRaceStart();
        if (isSoundEnabled()) playSound(beginSound);
        timeManager.startTimer();
        this.raceTask = new BukkitRunnable() {
            @Override
            public void run() {
                afterRaceEnd();
                if (isAnnounceEndEnabled()) announceEnd();
            }
        }.runTaskLater(plugin, duration);
        Bukkit.getScheduler().isCurrentlyRunning(raceTask.getTaskId());
    }

    @Override
    public void disable() {
        SchedulerUtil.cancel(raceTask);
        SchedulerUtil.cancel(bossBarTask);
        SchedulerUtil.cancel(actionBarTask);
    }

    public boolean isEnabled() {
        return settingManager.getBoolean(type, RaceSetting.ENABLED);
    }

    protected boolean isSoundEnabled() {
        return settingManager.getBoolean(type, RaceSetting.ENABLE_SOUND);
    }

    protected boolean isBossBarEnabled() {
        return settingManager.getBoolean(type, RaceSetting.ENABLE_BOSSBAR);
    }

    protected boolean isActionBarEnabled() {
        return settingManager.getBoolean(type, RaceSetting.ENABLE_ACTIONBAR);
    }

    protected boolean isAnnounceStartEnabled() {
        return settingManager.getBoolean(type, RaceSetting.BROADCAST_ENABLE_START);
    }

    protected boolean isAnnounceEndEnabled() {
        return settingManager.getBoolean(type, RaceSetting.BROADCAST_ENABLE_END);
    }

    protected boolean isCenterMessages() {
        return settingManager.getBoolean(type, RaceSetting.BROADCAST_CENTER_MESSAGES);
    }

    protected boolean isFireWorkEnabled() {
        return settingManager.getBoolean(type, RaceSetting.ENABLE_FIREWORK);
    }

    protected String getWinnerPersonal() {
        return settingManager.getString(type, RaceSetting.LANGUAGE_WINNER_PERSONAL);
    }

    public RaceType getType() {
        return type;
    }

    public int getChance() {
        return chance;
    }

    public int getDuration() {
        return duration;
    }

    public int getDurationSeconds() {
        return settingManager.getInt(type, RaceSetting.DURATION);
    }

    protected boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

}
