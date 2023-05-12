package be.woutzah.chatbrawl.races.types;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.contestants.ContestantsManager;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.leaderboard.LeaderboardStatistic;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.RaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.FireWorkUtil;
import be.woutzah.chatbrawl.util.Printer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
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

    public void showBossBar() {
        Component startMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(replacePlaceholders(settingManager.getString(this.type, RaceSetting.LANGUAGE_BOSSBAR))
                .replace("<timeLeft>", String.valueOf(timeManager.formatTime(raceManager.getRace(this.type).getDurationSeconds()))));
        final BossBar bossBar = BossBar.bossBar(startMessage, 1.0f, BossBar.Color.valueOf(settingManager.getString(this.type, RaceSetting.BOSSBAR_COLOR)), BossBar.Overlay.valueOf(settingManager.getString(this.type, RaceSetting.BOSSBAR_STYLE)));
        this.activeBossBar = bossBar;
        this.bossBarTask = new BukkitRunnable() {
            @Override
            public void run() {
                int remainingTime = timeManager.getRemainingTime(raceManager.getCurrentRunningRace());
                float remainingTimePercent = ((float) timeManager.getRemainingTime(RaceType.BLOCK) / getDurationSeconds());
                Component message = LegacyComponentSerializer.legacyAmpersand().deserialize(replacePlaceholders(settingManager.getString(raceManager.getCurrentRunningRace(), RaceSetting.LANGUAGE_BOSSBAR))
                        .replace("<timeLeft>", String.valueOf(timeManager.formatTime(remainingTime))));
                bossBar.name(message);
                bossBar.progress(remainingTimePercent);
                Bukkit.getServer().showBossBar(activeBossBar);
            }
        }.runTaskTimer(ChatBrawl.getInstance(), 0, 20);
    }
    public void showActionBar() {
        Component message = LegacyComponentSerializer.legacyAmpersand().deserialize(replacePlaceholders(settingManager.getString(this.type, RaceSetting.LANGUAGE_ACTIONBAR)));
        this.actionBarTask = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().sendActionBar(message);
            }
        }.runTaskTimer(ChatBrawl.getInstance(), 0, 20);
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
        setActive(false);
    }

    public void run(ChatBrawl plugin) {
        timeManager.startTimer();
        beforeRaceStart();
        if (isSoundEnabled()) playSound(beginSound);
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
        afterRaceEnd();
        if(!raceTask.isCancelled()) raceTask.cancel();;
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
