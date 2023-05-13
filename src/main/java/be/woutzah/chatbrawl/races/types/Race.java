package be.woutzah.chatbrawl.races.types;

import be.woutzah.chatbrawl.ChatBrawl;
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
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class Race implements Raceable, Announceable, Listener {

    public final int duration;
    protected final Random random;
    protected final Sound endSound;
    protected final RaceType type;
    private final Sound beginSound;
    private final int chance;
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

    public void announceStart(boolean center) {
        List<String> messageList = settingManager.getStringList(this.type, RaceSetting.LANGUAGE_START)
                .stream()
                .map(this::replacePlaceholders)
                .collect(Collectors.toList());
        if (center) {
            Printer.broadcast(Printer.centerMessage(messageList));
            return;
        }
        Printer.broadcast(messageList);
    }

    public void sendStart(Player player) {
        List<String> messageList = settingManager.getStringList(this.type, RaceSetting.LANGUAGE_START)
                .stream()
                .map(this::replacePlaceholders)
                .collect(Collectors.toList());
        if (isCenterMessages()) {
            Printer.sendParsedMessage(Printer.centerMessage(messageList), player);
            return;
        }
        Printer.sendParsedMessage(messageList, player);
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
        int remainingTime = timeManager.getRemainingTime(raceManager.getCurrentRunningRace());
        Component message = LegacyComponentSerializer.legacyAmpersand().deserialize(replacePlaceholders(settingManager.getString(this.type, RaceSetting.LANGUAGE_ACTIONBAR))
                .replace("<timeLeft>", String.valueOf(timeManager.formatTime(remainingTime))));
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

    public void announceEnd() {
        Printer.broadcast(settingManager.getStringList(type, RaceSetting.LANGUAGE_ENDED));
    }

    public void announceWinner(boolean center, Player player) {
        List<String> messageList = settingManager.getStringList(this.type, RaceSetting.LANGUAGE_WINNER)
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
    public void afterRaceEnd() {
        timeManager.stopTimer();
        if (isSoundEnabled()) playSound(endSound);
        if (isBossBarEnabled()) stopBossBar();
        if (isActionBarEnabled()) stopActionBar();
        raceManager.setCurrentRunningRace(RaceType.NONE);
        setActive(false);
    }

    public void run(ChatBrawl plugin) {
        try {
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
        } catch (Exception e) {
            raceManager.setCurrentRunningRace(RaceType.NONE);
            e.printStackTrace();
        }
    }

    @Override
    public void disable() {
        afterRaceEnd();
        if (!raceTask.isCancelled()) raceTask.cancel();
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

    public void raceChecks(Player player) {
        if (!raceManager.isCreativeAllowed()) {
            if (player.getGameMode() == GameMode.CREATIVE) return;
        }
        World world = player.getWorld();
        if (!raceManager.isWorldAllowed(world.getName())) return;
    }

    public void onWinning(Player player) {
        afterRaceEnd();
        if (isAnnounceEndEnabled()) announceWinner(isCenterMessages(), player);
        if (isFireWorkEnabled()) FireWorkUtil.shootFireWorkSync(player);
        this.raceTask.cancel();
        rewardManager.executeRandomRewardSync(RaceEntry.getRewardIds(), player);
        if (settingManager.getBoolean(GeneralSetting.MYSQL_ENABLED)) {
            leaderboardManager.addWin(new LeaderboardStatistic(player.getUniqueId(), type, timeManager.getTotalSeconds()));
        }
        Printer.sendParsedMessage(getWinnerPersonal(), player);
    }

}