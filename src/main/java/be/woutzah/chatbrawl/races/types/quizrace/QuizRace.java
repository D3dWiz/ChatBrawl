package be.woutzah.chatbrawl.races.types.quizrace;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.files.ConfigType;
import be.woutzah.chatbrawl.leaderboard.LeaderboardManager;
import be.woutzah.chatbrawl.leaderboard.LeaderboardStatistic;
import be.woutzah.chatbrawl.races.RaceManager;
import be.woutzah.chatbrawl.races.types.Race;
import be.woutzah.chatbrawl.races.types.RaceType;
import be.woutzah.chatbrawl.rewards.RewardManager;
import be.woutzah.chatbrawl.settings.GeneralSetting;
import be.woutzah.chatbrawl.settings.SettingManager;
import be.woutzah.chatbrawl.settings.races.QuizRaceSetting;
import be.woutzah.chatbrawl.settings.races.RaceSetting;
import be.woutzah.chatbrawl.time.TimeManager;
import be.woutzah.chatbrawl.util.FireWorkUtil;
import be.woutzah.chatbrawl.util.Printer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QuizRace extends Race {
    private final List<Question> questionList;
    private Question question;

    public QuizRace(RaceManager raceManager, SettingManager settingManager,
                    RewardManager rewardManager, TimeManager timeManager,
                    LeaderboardManager leaderboardManager) {
        super(RaceType.QUIZ, raceManager, settingManager, rewardManager, timeManager, leaderboardManager);
        this.questionList = new ArrayList<>();
        initQuestionsList();
    }

    private void initQuestionsList() {
        settingManager.getConfigSection(QuizRaceSetting.QUESTIONS).getKeys(false).forEach(entry -> {
            String question = settingManager.getString(ConfigType.QUIZRACE, "questions." + entry + ".question");
            List<String> answers = settingManager.getStringList(ConfigType.QUIZRACE, "questions." + entry + ".answer");
            List<Integer> rewardIds = settingManager.getIntegerList(ConfigType.QUIZRACE, "questions." + entry + ".rewards");
            questionList.add(new Question(question, answers, rewardIds));
        });
    }

    public void initRandomWord() {
        question = questionList.get(random.nextInt(questionList.size()));
    }

    @Override
    public void announceStart(boolean center) {
        List<String> messageList = settingManager.getStringList(RaceType.QUIZ, RaceSetting.LANGUAGE_START)
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
        List<String> messageList = settingManager.getStringList(RaceType.QUIZ, RaceSetting.LANGUAGE_START)
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
    public void checkAnswerInChat(AsyncPlayerChatEvent e) {
        //do checks
        if (!isActive()) return;
        Player player = e.getPlayer();
        if (!raceManager.isCreativeAllowed()) {
            if (player.getGameMode() == GameMode.CREATIVE) return;
        }
        World world = player.getWorld();
        if (!raceManager.isWorldAllowed(world.getName())) return;
        String message = Printer.stripColors(e.getMessage());
        if (raceManager.startsWithForbiddenCommand(message)) return;
        if (question.getAnswers().stream().anyMatch(a -> a.equalsIgnoreCase(message))) {
            //when correct
            afterRaceEnd();
            if (isAnnounceEndEnabled()) announceWinner(isCenterMessages(), player);
            if (isFireWorkEnabled()) FireWorkUtil.shootFireWorkSync(player);
            this.raceTask.cancel();
            rewardManager.executeRandomRewardSync(question.getRewardIds(), player);
            if (settingManager.getBoolean(GeneralSetting.MYSQL_ENABLED)) {
                leaderboardManager.addWin(new LeaderboardStatistic(player.getUniqueId(), type, timeManager.getTotalSeconds()));
            }
            Printer.sendMessage(getWinnerPersonal(), player);
        }
    }

    @Override
    public void announceWinner(boolean center, Player player) {
        List<String> messageList = settingManager.getStringList(RaceType.QUIZ, RaceSetting.LANGUAGE_WINNER)
                .stream()
                .map(this::replacePlaceholders)
                .map(s -> s.replace("<displayname>", player.getDisplayName()))
                .map(s -> s.replace("<player>", player.getName()))
                .map(s -> s.replace("<time>", timeManager.getTimeString()))
                .map(s -> s.replace("<answer>", question.getAnswers().get(0)))
                .collect(Collectors.toList());
        if (center) {
            Printer.broadcast(Printer.centerMessage(messageList));
            return;
        }
        Printer.broadcast(messageList);
    }

    @Override
    public void showBossBar() {
        Component startMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(replacePlaceholders(settingManager.getString(RaceType.QUIZ, RaceSetting.LANGUAGE_BOSSBAR))
                .replace("<timeLeft>", String.valueOf(timeManager.formatTime(raceManager.getRace(RaceType.QUIZ).getDurationSeconds()))));
        final BossBar bossBar = BossBar.bossBar(startMessage, 1.0f, BossBar.Color.valueOf(settingManager.getString(RaceType.QUIZ, RaceSetting.BOSSBAR_COLOR)), BossBar.Overlay.valueOf(settingManager.getString(RaceType.QUIZ, RaceSetting.BOSSBAR_STYLE)));
        this.activeBossBar = bossBar;
        this.bossBarTask = new BukkitRunnable() {
            @Override
            public void run() {
                int remainingTime = timeManager.getRemainingTime(RaceType.QUIZ);
                float remainingTimePercent = ((float) timeManager.getRemainingTime(RaceType.QUIZ) / getDurationSeconds());
                Component message = LegacyComponentSerializer.legacyAmpersand().deserialize(replacePlaceholders(settingManager.getString(RaceType.QUIZ, RaceSetting.LANGUAGE_BOSSBAR))
                        .replace("<timeLeft>", String.valueOf(timeManager.formatTime(remainingTime))));
                bossBar.name(message);
                bossBar.progress(remainingTimePercent);
                Bukkit.getServer().showBossBar(bossBar);
            }
        }.runTaskTimer(ChatBrawl.getInstance(), 0, 20);
    }

    @Override
    public void showActionBar() {
        Component message = LegacyComponentSerializer.legacyAmpersand().deserialize(replacePlaceholders(settingManager.getString(RaceType.QUIZ, RaceSetting.LANGUAGE_ACTIONBAR)));
        this.actionBarTask = new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().sendActionBar(message);
            }
        }.runTaskTimer(ChatBrawl.getInstance(), 0, 20);
    }

    @Override
    public String replacePlaceholders(String message) {
        return message.replace("<question>", question.getQuestion());
    }

    @Override
    public void beforeRaceStart() {
        initRandomWord();
        if (isAnnounceStartEnabled()) announceStart(isCenterMessages());
        if (isBossBarEnabled()) showBossBar();
        if (isActionBarEnabled()) showActionBar();
    }

    @Override
    public void announceEnd() {
        List<String> lines = settingManager.getStringList(RaceType.QUIZ, RaceSetting.LANGUAGE_ENDED)
                .stream()
                .map(line -> line.replace("<answer>", question.getAnswers().get(0)))
                .collect(Collectors.toList());
        Printer.broadcast(lines);
    }
}
